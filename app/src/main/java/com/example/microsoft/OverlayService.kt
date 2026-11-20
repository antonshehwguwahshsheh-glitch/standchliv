package com.example.microsoft

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import kotlin.concurrent.thread

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: EspOverlay

    override fun onCreate() {
        super.onCreate()
        
        val channelId = "esp_overlay_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "ESP Overlay", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("micro_soft ESP")
                .setContentText("Оверлей запущен")
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("micro_soft ESP")
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .build()
        }
        startForeground(1, notification)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = EspOverlay(this)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        windowManager.addView(overlayView, params)

        thread {
            while (true) {
                // ЗДЕСЬ ДОЛЖЕН БЫТЬ КОД ЧТЕНИЯ ПАМЯТИ (WorldToScreen)
                // Пока для теста просто добавляем квадрат в центр экрана
                overlayView.enemies = listOf(EspOverlay.Enemy(540f, 1200f, 100f)) 
                
                Thread.sleep(16)
                overlayView.post { overlayView.invalidate() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
