package com.example.microsoft

import java.io.DataOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader

object RootUtils {
    fun isRootAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c id")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText()
            process.waitFor()
            output.contains("uid=0")
        } catch (e: Exception) { false }
    }

    fun executeRootCommand(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("$command\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor() == 0
        } catch (e: Exception) { false }
    }

    // Новая функция: автоматическая выдача разрешения на оверлей через ADB/root
    fun grantOverlayPermission(packageName: String) {
        // Эта команда через root (appops) выдает разрешение на рисование поверх окон
        executeRootCommand("appops set $packageName SYSTEM_ALERT_WINDOW allow")
    }
}
