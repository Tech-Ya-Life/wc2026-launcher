package com.wc2026.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wc2026.launcher.theme.TeamColorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamPickerSheet(
    currentTla: String,
    onTeamSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val teams = remember(query) {
        TeamColorPalette.palettes.values
            .sortedBy { it.name }
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) || it.tla.contains(query, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D1B2A),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Text(
                text = "Pick your team",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search teams…", color = Color.White.copy(alpha = 0.4f)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                    cursorColor = Color(0xFFFFD700),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 48.dp),
                modifier = Modifier.heightIn(max = 440.dp)
            ) {
                items(teams, key = { it.tla }) { team ->
                    val isSelected = team.tla == currentTla
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) Color(0xFFFFD700).copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .clickable { onTeamSelected(team.tla) }
                            .padding(vertical = 6.dp, horizontal = 2.dp)
                    ) {
                        // Coloured circle with TLA
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(team.primary)
                                .then(
                                    if (isSelected)
                                        Modifier.padding(2.dp)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = team.tla,
                                color = team.onPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (isSelected) {
                            Text("✓", color = Color(0xFFFFD700), fontSize = 10.sp)
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Text(
                            text = team.name,
                            color = Color.White.copy(alpha = if (isSelected) 1f else 0.7f),
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            lineHeight = 11.sp
                        )
                    }
                }
            }
        }
    }
}
