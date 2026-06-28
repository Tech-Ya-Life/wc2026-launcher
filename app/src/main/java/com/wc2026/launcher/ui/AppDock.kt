package com.wc2026.launcher.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wc2026.launcher.settings.SettingsActivity

data class DockItem(
    val icon: ImageVector,
    val label: String,
    val action: () -> Unit
)

@Composable
fun AppDock(accentColor: Color) {
    val context = LocalContext.current

    val dockItems = listOf(
        DockItem(Icons.Default.Phone, "Phone") {
            context.startActivity(
                Intent(Intent.ACTION_DIAL).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            )
        },
        DockItem(Icons.Default.Message, "Messages") {
            context.packageManager.getLaunchIntentForPackage("com.google.android.apps.messaging")
                ?.let { context.startActivity(it) }
        },
        DockItem(Icons.Default.Camera, "Camera") {
            context.startActivity(
                Intent("android.media.action.IMAGE_CAPTURE").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        },
        DockItem(Icons.Default.Settings, "Settings") {
            context.startActivity(
                Intent(context, SettingsActivity::class.java)
            )
        }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dockItems.forEach { item ->
            IconButton(onClick = item.action) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
