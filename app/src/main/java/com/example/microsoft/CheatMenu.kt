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

    // Состояния безопасности и оружия
    var antiBan by remember { mutableStateOf(true) }
    var infiniteAmmo by remember { mutableStateOf(false) }
    var noRecoil by remember { mutableStateOf(false) }
    var noSpread by remember { mutableStateOf(false) }

    // Состояния Аимбота
    var aimbot by remember { mutableStateOf(false) }
    var aimbotFov by remember { mutableStateOf(120f) } // Радиус захвата

    Column(
        modifier = Modifier.fillMaxSize().background(bgColor).padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("micro_soft // FUNCTIONS", color = accentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // --- БЛОК: БЕЗОПАСНОСТЬ ---
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

        // --- БЛОК: БОЙ (Aimbot, Отдача, Разброс) ---
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
            Text("COMBAT", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Aimbot
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Aimbot", color = Color.White, fontSize = 15.sp)
                Switch(checked = aimbot, onCheckedChange = { isChecked ->
                    aimbot = isChecked
                    // В реальности здесь должен запускаться фоновый поток в C++, 
                    // который читает координаты врага и двигает камеру.
                    Toast.makeText(context, if (isChecked) "Aimbot: ON (Фон. поток)" else "Aimbot: OFF", Toast.LENGTH_SHORT).show()
                }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }
            
            // Регулировка FOV для Aimbot (появляется только если включен аимбот)
            if (aimbot) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Aimbot FOV", color = Color.LightGray, fontSize = 14.sp)
                        Text(String.format("%.0f", aimbotFov), color = accentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = aimbotFov,
                        onValueChange = { aimbotFov = it },
                        valueRange = 10f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = accentBlue,
                            activeTrackColor = accentBlue,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }
            }

            // No Recoil (Нет отдачи)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("No Recoil", color = Color.White, fontSize = 15.sp)
                Switch(checked = noRecoil, onCheckedChange = { isChecked ->
                    noRecoil = isChecked
                    if (isChecked) {
                        // Ищем значение отдачи (например, 1.0f) и меняем на 0.0f
                        // В текущем C++ коде нет scanFloat, поэтому показываем уведомление
                        Toast.makeText(context, "No Recoil активирован (нужен scanFloat)", Toast.LENGTH_SHORT).show()
                    }
                }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }

            // No Spread (Нет разброса)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("No Spread", color = Color.White, fontSize = 15.sp)
                Switch(checked = noSpread, onCheckedChange = { isChecked ->
                    noSpread = isChecked
                    if (isChecked) {
                        Toast.makeText(context, "No Spread активирован (нужен scanFloat)", Toast.LENGTH_SHORT).show()
                    }
                }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- БЛОК: ОРУЖИЕ ---
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
            Text("WEAPON", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Infinite Ammo", color = Color.White, fontSize = 15.sp)
                Switch(checked = infiniteAmmo, onCheckedChange = { isChecked ->
                    infiniteAmmo = isChecked
                    if (isChecked) {
                        val addr = native.scanInt(gamePkg, 30)
                        if (addr != 0L) {
                            native.writeInt(gamePkg, addr, 9999)
                            Toast.makeText(context, "Патроны изменены!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Не удалось найти патроны.", Toast.LENGTH_LONG).show()
                        }
                    }
                }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }
        }
    }
}
