// Добавь эту функцию в самый конец файла native-lib.cpp
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
        if (sz > 128 * 1024 * 1024) continue; // Пропускаем слишком большие блоки
        
        std::vector<char> buf(sz);
        if (ptrace(PTRACE_ATTACH, pid, 0, 0) == -1) continue;
        waitpid(pid, nullptr, 0);
        
        bool found = false;
        jlong foundAddr = 0;
        for (size_t i = 0; i < sz; i += sizeof(long)) {
            long word = ptrace(PTRACE_PEEKDATA, pid, m.start + i, 0);
            if (word == target) {
                foundAddr = m.start + i;
                found = true;
                break;
            }
        }
        ptrace(PTRACE_DETACH, pid, 0, 0);
        
        if (found) return foundAddr;
    }
    return 0;
}
