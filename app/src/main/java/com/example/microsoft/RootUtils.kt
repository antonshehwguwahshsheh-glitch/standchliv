package com.example.microsoft

import java.io.DataOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader

object RootUtils {

    /**
     * Проверяет, выдан ли приложению root-доступ.
     * Вызывает команду "su -c id" и проверяет, равен ли UID нулю.
     */
    fun isRootAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c id")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText()
            process.waitFor()
            output.contains("uid=0")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Выполняет произвольную команду от имени root.
     * Возвращает true, если команда выполнена без ошибок.
     */
    fun executeRootCommand(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("$command\n")
            os.writeBytes("exit\n")
            os.flush()
            
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Скрывает следы ptrace (обнуляет TracerPid в статусе процесса),
     * чтобы античит игры не увидел инжектор.
     */
    fun hideTracer(pid: Int): Boolean {
        // Команда для изменения TracerPid в памяти (требует root и setenforce 0)
        // В реальности это делается через нативный код (ptrace POKEDATA),
        // но здесь мы даем базовую команду для демонстрации.
        return executeRootCommand("echo 0 > /proc/$pid/status")
    }
}
