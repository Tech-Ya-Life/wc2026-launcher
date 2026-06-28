package com.wc2026.launcher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LauncherClock(textColor: Color) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000)
            now = LocalDateTime.now()
        }
    }

    val timeStr = now.format(DateTimeFormatter.ofPattern("HH:mm"))
    val dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM"))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = timeStr,
            fontSize = 72.sp,
            fontWeight = FontWeight.Light,
            color = textColor
        )
        Text(
            text = dateStr,
            fontSize = 16.sp,
            color = textColor.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
