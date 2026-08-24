package com.sleeperbaby.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sleeperbaby.app.ui.theme.Mist
import com.sleeperbaby.app.ui.theme.MuteText
import com.sleeperbaby.app.ui.theme.NightCard
import com.sleeperbaby.app.ui.theme.NightCardSelected
import com.sleeperbaby.app.ui.theme.NightNavy
import com.sleeperbaby.app.ui.theme.WarmGold

val SleeperCardShape = RoundedCornerShape(24.dp)
val SleeperChipShape = RoundedCornerShape(50)
val SleeperCoverShape = RoundedCornerShape(16.dp)
val SleeperSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

fun Modifier.sleeperCard(selected: Boolean = false): Modifier {
    val border = if (selected) WarmGold.copy(alpha = 0.88f) else Color.White.copy(alpha = 0.18f)
    return clip(SleeperCardShape)
        .background(
            Brush.verticalGradient(
                listOf(
                    if (selected) NightCardSelected else NightCard,
                    if (selected) Color(0xCC2A4A72) else Color(0x99203A5C),
                ),
            ),
        )
        .border(1.dp, border, SleeperCardShape)
}

@Composable
fun SleeperIconWell(
    selected: Boolean = false,
    size: Dp = 48.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        if (selected) WarmGold.copy(alpha = 0.34f) else Color.White.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.04f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
fun SleeperChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .clip(SleeperChipShape)
            .background(if (selected) WarmGold else Color.White.copy(alpha = 0.08f))
            .border(
                1.dp,
                if (selected) WarmGold else Color.White.copy(alpha = 0.14f),
                SleeperChipShape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) icon()
        Text(
            text = label,
            color = if (selected) NightNavy else Mist,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SleeperSegmented(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SleeperChipShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), SleeperChipShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Text(
                text = label,
                color = if (selected) NightNavy else Mist,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clip(SleeperChipShape)
                    .background(if (selected) WarmGold else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 10.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
fun OverlayBar(
    title: String,
    subtitle: String? = null,
    onClose: () -> Unit,
    closeLabel: String,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            AppIcon(AppIcons.close, closeLabel, Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MuteText,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) trailing()
    }
}
