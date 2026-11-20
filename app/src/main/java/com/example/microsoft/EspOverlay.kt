package com.example.microsoft

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class EspOverlay(context: Context) : View(context) {
    private val paintBox = Paint().apply {
        color = Color.GREEN
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }
    private val paintLine = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val paintHealthBg = Paint().apply { color = Color.BLACK }
    private val paintHealth = Paint().apply { color = Color.RED }

    var enemies: List<Enemy> = emptyList()

    data class Enemy(
        val screenX: Float,
        val screenY: Float,
        val health: Float,
        val maxHealth: Float = 100f,
        val width: Float = 100f,
        val height: Float = 200f
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        for (enemy in enemies) {
            canvas.drawRect(
                enemy.screenX - enemy.width / 2,
                enemy.screenY - enemy.height,
                enemy.screenX + enemy.width / 2,
                enemy.screenY,
                paintBox
            )
            
            canvas.drawLine(
                width / 2f, height.toFloat(),
                enemy.screenX, enemy.screenY - enemy.height,
                paintLine
            )

            val healthPercent = enemy.health / enemy.maxHealth
            val barHeight = 200f
            val barWidth = 10f
            val barX = enemy.screenX - enemy.width / 2 - 20f
            val barY = enemy.screenY - enemy.height

            canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, paintHealthBg)
            paintHealth.color = if (healthPercent > 0.5f) Color.GREEN else Color.RED
            canvas.drawRect(barX, barY + barHeight * (1 - healthPercent), barX + barWidth, barY + barHeight, paintHealth)
        }
    }
}
