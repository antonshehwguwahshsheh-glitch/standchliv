package com.example.microsoft

import android.content.Intent
import android.os.Build
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
import androidx.compose.ui.graphics.graphicsLayer
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

    // Состояние для масштаба меню
    var menuScale by remember { mutableStateOf(1.0f) }

    // Состояния функций
    var antiBan by remember { mutableStateOf(true) }
    var espEnabled by remember { mutableStateOf(false) }
    var infiniteAmmo by remember { mutableStateOf(false) }
    var noRecoil by remember { mutableStateOf(false) }
    var noSpread by remember { mutableStateOf(false) }
    var aimbot by remember { mutableStateOf(false) }
    var aimbotFov by remember { mutableStateOf(120f) }

    // Применяем масштаб ко всему контенту
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .graphicsLayer(
                scaleX = menuScale,
                scaleY = menuScale,
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("micro_soft // FUNCTIONS", color = accentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            // --- СЛАЙДЕР МАСШТАБА (MENU SCALE) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardColor)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Menu scale", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(String.format("%.2fx", menuScale), color = accentBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Slider(
                    value = menuScale,
                    onValueChange = { menuScale = it },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentBlue,
                        activeTrackColor = accentBlue,
                        inactiveTrackColor = Color.DarkGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- ROOT PERMISSIONS ---
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
                Text("ROOT PERMISSIONS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        RootUtils.grantOverlayPermission(context.packageName)
                        Toast.makeText(context, "Оверлей разрешен через Root!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accentBlue)
                ) {
                    Text("Выдать разрешение на ESP", color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- SECURITY ---
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
                Text("SECURITY", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Anti-Ban", color = Color.White, fontSize = 15.sp)
                    Switch(checked = antiBan, onCheckedChange = { 
                        antiBan = it
                        native.setAntiBan(gamePkg, it)
                    }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- VISUALS ---
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
                Text("VISUALS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("ESP (Визуалы)", color = Color.White, fontSize = 15.sp)
                    Switch(checked = espEnabled, onCheckedChange = { isChecked ->
                        espEnabled = isChecked
                        if (isChecked) {
                            val intent = Intent(context, OverlayService::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent)
                            } else {
                                context.startService(intent)
                            }
                            Toast.makeText(context, "ESP Включен", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(context, OverlayService::class.java)
                            context.stopService(intent)
                            Toast.makeText(context, "ESP Выключен", Toast.LENGTH_SHORT).show()
                        }
                    }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- COMBAT ---
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)) {
                Text("COMBAT", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Aimbot", color = Color.White, fontSize = 15.sp)
                    Switch(checked = aimbot, onCheckedChange = { aimbot = it }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
                }
                if (aimbot) {
                    Slider(
                        value = aimbotFov,
                        onValueChange = { aimbotFov = it },
                        valueRange = 10f..360f,
                        colors = SliderDefaults.colors(thumbColor = accentBlue, activeTrackColor = accentBlue)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("No Recoil", color = Color.White, fontSize = 15.sp)
                    Switch(checked = noRecoil, onCheckedChange = { noRecoil = it }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("No Spread", color = Color.White, fontSize = 15.sp)
                    Switch(checked = noSpread, onCheckedChange = { noSpread = it }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- WEAPON ---
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
                                Toast.makeText(context, "Ошибка поиска патронов.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
                }
            }
        }
    }
}
