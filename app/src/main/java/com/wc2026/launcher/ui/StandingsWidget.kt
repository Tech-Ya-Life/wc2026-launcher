package com.wc2026.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import com.wc2026.launcher.schedule.Standing

/**
 * Horizontally scrollable row of group-stage standing cards.
 * Top 2 positions per group are highlighted in the accent colour (they advance).
 */
@Composable
fun StandingsWidget(
    groups: List<Standing>,
    accentColor: Color,
    onBackground: Color,
    modifier: Modifier = Modifier
) {
    if (groups.isEmpty()) return

    Column(modifier = modifier) {
        Text(
            text = "GROUP STANDINGS",
            color = accentColor.copy(alpha = 0.75f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(groups, key = { it.group ?: "" }) { standing ->
                GroupCard(standing, accentColor, onBackground)
            }
        }
    }
}

@Composable
private fun GroupCard(
    standing: Standing,
    accentColor: Color,
    onBackground: Color
) {
    // API returns "GROUP_A" → display "GROUP A"
    val groupLabel = standing.group
        ?.replace("GROUP_", "GROUP ")  // "GROUP_A" → "GROUP A"
        ?.replace("_", " ")            // any other underscores
        ?: "GROUP ?"

    Column(
        modifier = Modifier
            .width(152.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.30f))
            .padding(10.dp)
    ) {
        // Group header
        Text(
            text = groupLabel,
            color = accentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Column headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(" ", modifier = Modifier.width(18.dp)) // position col
            Text(
                "Team",
                color = onBackground.copy(alpha = 0.4f),
                fontSize = 9.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Pld",
                color = onBackground.copy(alpha = 0.4f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(26.dp)
            )
            Text(
                "Pts",
                color = onBackground.copy(alpha = 0.4f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(26.dp)
            )
        }

        HorizontalDivider(
            color = onBackground.copy(alpha = 0.12f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Team rows — top 2 are advancing teams
        standing.table.take(4).forEach { entry ->
            val advancing = entry.position <= 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${entry.position}",
                    color = if (advancing) accentColor else onBackground.copy(alpha = 0.35f),
                    fontSize = 10.sp,
                    fontWeight = if (advancing) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.width(18.dp)
                )
                Text(
                    text = entry.team.tla,
                    color = if (advancing) onBackground else onBackground.copy(alpha = 0.60f),
                    fontSize = 11.sp,
                    fontWeight = if (advancing) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${entry.playedGames}",
                    color = onBackground.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(26.dp)
                )
                Text(
                    text = "${entry.points}",
                    color = if (advancing) accentColor else onBackground.copy(alpha = 0.60f),
                    fontSize = 10.sp,
                    fontWeight = if (advancing) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(26.dp)
                )
            }
        }
    }
}
