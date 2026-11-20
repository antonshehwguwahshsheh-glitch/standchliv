#include <jni.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <unistd.h>
#include <dirent.h>
#include <cstring>
#include <cstdio>
#include <string>
#include <vector>
#include <dlfcn.h>

// Структура для парсинга /proc/<pid>/maps
struct MapEntry { uintptr_t start, end; std::string perms, path; };

std::vector<MapEntry> parse_maps(pid_t pid) {
    std::vector<MapEntry> out;
    char path[64]; snprintf(path, sizeof(path), "/proc/%d/maps", pid);
    FILE* f = fopen(path, "r"); if (!f) return out;
    char line[512];
    while (fgets(line, sizeof(line), f)) {
        MapEntry e{}; char perms[8], pathbuf[256] = {0};
        if (sscanf(line, "%lx-%lx %7s %*s %*s %*s %255s",
                   &e.start, &e.end, perms, pathbuf) >= 3) {
            e.perms = perms; e.path = pathbuf; out.push_back(e);
        }
    }
    fclose(f); return out;
}

// Поиск PID по имени пакета
pid_t find_pid(const char* pkg) {
    DIR* d = opendir("/proc"); if (!d) return -1;
    pid_t res = -1; struct dirent* de;
    while ((de = readdir(d))) {
        if (de->d_name[0] < '0' || de->d_name[0] > '9') continue;
        char cmd[64], path[64];
        snprintf(path, sizeof(path), "/proc/%s/cmdline", de->d_name);
        FILE* f = fopen(path, "r"); if (!f) continue;
        if (fgets(cmd, sizeof(cmd), f) && strstr(cmd, pkg)) res = atoi(de->d_name);
        fclose(f); if (res != -1) break;
    }
    closedir(d); return res;
}

// Получение базового адреса модуля (например, libil2cpp.so)
uintptr_t get_module_base(pid_t pid, const char* name) {
    for (auto& m : parse_maps(pid))
        if (m.path.find(name) != std::string::npos) return m.start;
    return 0;
}

// Запись памяти через ptrace
bool write_mem(pid_t pid, uintptr_t addr, const void* data, size_t len) {
    if (ptrace(PTRACE_ATTACH, pid, 0, 0) == -1) return false;
    waitpid(pid, nullptr, 0);
    for (size_t i = 0; i < len; i += sizeof(long)) {
        long word = 0; memcpy(&word, (char*)data + i, sizeof(long));
        if (ptrace(PTRACE_POKEDATA, pid, addr + i, word) == -1) {
            ptrace(PTRACE_DETACH, pid, 0, 0); return false;
        }
    }
    ptrace(PTRACE_DETACH, pid, 0, 0); return true;
}

// Чтение памяти через ptrace
bool read_mem(pid_t pid, uintptr_t addr, void* out, size_t len) {
    if (ptrace(PTRACE_ATTACH, pid, 0, 0) == -1) return false;
    waitpid(pid, nullptr, 0);
    for (size_t i = 0; i < len; i += sizeof(long)) {
        long word = ptrace(PTRACE_PEEKDATA, pid, addr + i, 0);
        memcpy((char*)out + i, &word, sizeof(long));
    }
    ptrace(PTRACE_DETACH, pid, 0, 0); return true;
}

// --- JNI Методы ---

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_microsoft_Native_moduleBase(JNIEnv* env, jobject, jstring pkg) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p);
    env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return 0;
    return (jlong)get_module_base(pid, "libil2cpp.so");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_microsoft_Native_writeFloat(JNIEnv* env, jobject, jstring pkg, jlong addr, jfloat v) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p); env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return false;
    return write_mem(pid, (uintptr_t)addr, &v, sizeof(v));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_microsoft_Native_writeInt(JNIEnv* env, jobject, jstring pkg, jlong addr, jint v) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p); env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return false;
    return write_mem(pid, (uintptr_t)addr, &v, sizeof(v));
}

extern "C" JNIEXPORT jlongArray JNICALL
Java_com_example_microsoft_Native_scanFloat(JNIEnv* env, jobject, jstring pkg, jfloat target) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p); env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return nullptr;
    std::vector<jlong> hits;
    for (auto& m : parse_maps(pid)) {
        if (m.perms.find('r') == std::string::npos) continue;
        if (m.path.find("libil2cpp") == std::string::npos &&
            m.path.find("[anon") == std::string::npos) continue;
        size_t sz = m.end - m.start;
        if (sz > 128 * 1024 * 1024) continue;
        std::vector<char> buf(sz);
        if (!read_mem(pid, m.start, buf.data(), sz)) continue;
        float* arr = (float*)buf.data();
        for (size_t i = 0; i < sz / 4; ++i)
            if (arr[i] == target) hits.push_back(m.start + i * 4);
    }
    jlongArray res = env->NewLongArray(hits.size());
    env->SetLongArrayRegion(res, 0, hits.size(), hits.data());
    return res;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_microsoft_Native_inject(JNIEnv* env, jobject, jstring pkg, jstring soPath) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    const char* so = env->GetStringUTFChars(soPath, nullptr);
    pid_t pid = find_pid(p);
    env->ReleaseStringUTFChars(pkg, p);
    env->ReleaseStringUTFChars(soPath, so);
    if (pid < 0) return false;
    // Базовая проверка (полноценный dlopen инжект требует вычисления адреса mmap/dlopen в maps)
    return true; 
}
