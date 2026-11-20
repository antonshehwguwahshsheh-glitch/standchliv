package com.example.microsoft

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val bgColor = Color(0xFF121212)
    val cardColor = Color(0xFF222222)
    val accentBlue = Color(0xFF00BFFF)
    val textColor = Color.White

    var sliderValue by remember { mutableStateOf(2.75f) }
    var showMenu by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (showMenu) {
        CheatMenuScreen()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "micro_soft",
                color = textColor,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "I N T E R N A L",
                color = accentBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Блок слайдера
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Menu scale", color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(String.format("%.2fx", sliderValue), color = accentBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 1f..3f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentBlue,
                        activeTrackColor = accentBlue,
                        inactiveTrackColor = Color.DarkGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка START
            Button(
                onClick = {
                    if (isLoading) return@Button
                    isLoading = true
                    Thread {
                        // 1. Проверка Root
                        if (!RootUtils.isRootAvailable()) {
                            (context as ComponentActivity).runOnUiThread {
                                Toast.makeText(context, "Root не получен!", Toast.LENGTH_LONG).show()
                                isLoading = false
                            }
                            return@Thread
                        }

                        // 2. Отключение SELinux
                        RootUtils.executeRootCommand("setenforce 0")

                        // 3. Скачивание софта
                        try {
                            val f = java.io.File(context.cacheDir, "soft.so")
                            val url = java.net.URL("https://raw.githubusercontent.com/USER/REPO/main/soft.so")
                            url.openStream().use { i -> f.outputStream().use { o -> i.copyTo(o) } }

                            // 4. Инжект
                            val ok = Native().inject("com.axlebolt.standoff2", f.absolutePath)
                            
                            (context as ComponentActivity).runOnUiThread {
                                if (ok) {
                                    Toast.makeText(context, "Инжект выполнен. Открываем меню.", Toast.LENGTH_SHORT).show()
                                    showMenu = true
                                } else {
                                    Toast.makeText(context, "Ошибка инжекта. Игра запущена?", Toast.LENGTH_LONG).show()
                                }
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            (context as ComponentActivity).runOnUiThread {
                                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                                isLoading = false
                            }
                        }
                    }.start()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("▶  START", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
        }
    }
}
