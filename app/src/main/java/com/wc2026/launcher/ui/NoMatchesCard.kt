package com.wc2026.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoMatchesCard(onBackground: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "⚽",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "FIFA WORLD CUP 2026",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = Color(0xFFFFD700)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "USA · Canada · Mexico",
                fontSize = 11.sp,
                color = onBackground.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No matches scheduled right now.\nCheck back closer to kick-off.",
                fontSize = 12.sp,
                color = onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
