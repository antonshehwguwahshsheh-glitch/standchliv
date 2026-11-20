package com.example.microsoft

import android.os.Bundle
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MicroSoftScreen()
            }
        }
    }
}

@Composable
fun MicroSoftScreen() {
    val bgColor = Color(0xFF121212)
    val cardColor = Color(0xFF222222)
    val accentBlue = Color(0xFF00BFFF)
    val textColor = Color.White

    var sliderValue by remember { mutableStateOf(2.75f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "micro_soft",
            color = textColor,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
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

        Button(
            onClick = { /* Здесь позже будет логика инжекта */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentBlue)
        ) {
            Text("▶  START", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }
    }
}
