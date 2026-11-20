#include <jni.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <unistd.h>
#include <dirent.h>
#include <cstring>
#include <cstdio>
#include <string>
#include <vector>

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

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_microsoft_Native_writeInt(JNIEnv* env, jobject, jstring pkg, jlong addr, jint v) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p); env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return false;
    return write_mem(pid, (uintptr_t)addr, &v, sizeof(v));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_microsoft_Native_writeFloat(JNIEnv* env, jobject, jstring pkg, jlong addr, jfloat v) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p); env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return false;
    return write_mem(pid, (uintptr_t)addr, &v, sizeof(v));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_microsoft_Native_setAntiBan(JNIEnv* env, jobject, jstring pkg, jboolean enable) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p); env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return false;
    if (enable) {
        char cmd[128];
        snprintf(cmd, sizeof(cmd), "su -c 'echo 0 > /proc/%d/status'", pid);
        system(cmd);
    }
    return true;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_microsoft_Native_scanInt(JNIEnv* env, jobject, jstring pkg, jint target) {
    const char* p = env->GetStringUTFChars(pkg, nullptr);
    pid_t pid = find_pid(p); env->ReleaseStringUTFChars(pkg, p);
    if (pid < 0) return 0;

    std::vector<MapEntry> maps = parse_maps(pid);
    for (auto& m : maps) {
        if (m.perms.find('r') == std::string::npos) continue;
        if (m.path.find("libil2cpp") == std::string::npos && m.path.find("[anon") == std::string::npos) continue;
        
        size_t sz = m.end - m.start;
        if (sz > 128 * 1024 * 1024) continue; 
        
        if (ptrace(PTRACE_ATTACH, pid, 0, 0) == -1) continue;
        waitpid(pid, nullptr, 0);
        
        jlong foundAddr = 0;
        for (size_t i = 0; i < sz; i += sizeof(long)) {
            long word = ptrace(PTRACE_PEEKDATA, pid, m.start + i, 0);
            if (word == target) {
                foundAddr = m.start + i;
                break;
            }
        }
        ptrace(PTRACE_DETACH, pid, 0, 0);
        if (foundAddr != 0) return foundAddr;
    }
    return 0;
}
