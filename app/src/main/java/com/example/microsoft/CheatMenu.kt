package com.example.microsoft

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheatMenuScreen() {
    val bgColor = Color(0xFF121212)
    val cardColor = Color(0xFF222222)
    val accentBlue = Color(0xFF00BFFF)

    val native = Native()
    val gamePkg = "com.axlebolt.standoff2"

    var antiBan by remember { mutableStateOf(true) }
    var aimbot by remember { mutableStateOf(false) }
    var wallhack by remember { mutableStateOf(false) }
    var speedHack by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("micro_soft // MENU", color = accentBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // --- БЛОК АНТИБАНА ---
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)
        ) {
            Text("SECURITY", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Anti-Ban (Скрытие следов)", color = Color.White, fontSize = 15.sp)
                Switch(
                    checked = antiBan,
                    onCheckedChange = { 
                        antiBan = it
                        native.setAntiBan(gamePkg, it)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = accentBlue, checkedTrackColor = accentBlue.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- БЛОК ЧИТОВ ---
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardColor).padding(16.dp)
        ) {
            Text("COMBAT", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Aimbot", color = Color.White, fontSize = 15.sp)
                Switch(checked = aimbot, onCheckedChange = { aimbot = it }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Wallhack", color = Color.White, fontSize = 15.sp)
                Switch(checked = wallhack, onCheckedChange = { wallhack = it }, colors = SwitchDefaults.colors(checkedThumbColor = accentBlue))
            }
        }
    }
}
