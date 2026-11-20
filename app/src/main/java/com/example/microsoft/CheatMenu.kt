package com.example.microsoft

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun CheatMenuScreen() {
    val bgColor = Color(0xFF121212)
    val cardColor = Color(0xFF222222)
    val accentBlue = Color(0xFF00BFFF)
    val context = LocalContext.current

    val native = Native()
    val gamePkg = "com.axlebolt.standoff2"

    var antiBan by remember { mutableStateOf(true) }
    var infiniteAmmo by remember { mutableStateOf(false) }
    var noRecoil by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(bgColor).padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("micro_soft // FUNCTIONS", color = accentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // Блок Безопасности
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
            Text("SECURITY", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Anti-Ban", color = Color.White, fontSize = 15.sp)
                Switch(checked = antiBan, onCheckedChange = { 
                    antiBan = it
                    native.setAntiBan(gamePkg, it)
                    Toast.makeText(context, if (it) "Anti-Ban: ON" else "Anti-Ban: OFF", Toast.LENGTH_SHORT).show()
                }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Блок Читов
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
            Text("WEAPON", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Бесконечные патроны
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Infinite Ammo", color = Color.White, fontSize = 15.sp)
                Switch(checked = infiniteAmmo, onCheckedChange = { isChecked ->
                    infiniteAmmo = isChecked
                    if (isChecked) {
                        // Ищем адрес, где лежит число 30 (примерный размер обоймы) и меняем на 9999
                        val addr = native.scanInt(gamePkg, 30)
                        if (addr != 0L) {
                            native.writeInt(gamePkg, addr, 9999)
                            Toast.makeText(context, "Патроны найдены и изменены!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Ошибка: не удалось найти патроны в памяти. Игра запущена?", Toast.LENGTH_LONG).show()
                        }
                    }
                }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }

            // Убираем отдачу
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("No Recoil", color = Color.White, fontSize = 15.sp)
                Switch(checked = noRecoil, onCheckedChange = { isChecked ->
                    noRecoil = isChecked
                    if (isChecked) {
                        // Ищем адрес отдачи (обычно это float значение, например 1.0) и ставим 0.0
                        // Здесь упрощенный пример с int, в реальности нужен scanFloat
                        Toast.makeText(context, "Функция в разработке (требуется точный офсет)", Toast.LENGTH_SHORT).show()
                    }
                }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }
        }
    }
}
