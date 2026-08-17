package com.sleeperbaby.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sleeperbaby.app.data.Catalog
import com.sleeperbaby.app.data.Channel
import com.sleeperbaby.app.data.Station
import com.sleeperbaby.app.library.StoryLibraryController
import com.sleeperbaby.app.playback.NightLightController
import com.sleeperbaby.app.playback.NightLightMode
import com.sleeperbaby.app.playback.PlaybackKind
import com.sleeperbaby.app.playback.PlayerState
import com.sleeperbaby.app.playback.SPEED_STEPS
import com.sleeperbaby.app.playback.SleepRadioController
import com.sleeperbaby.app.playback.currentStory
import com.sleeperbaby.app.playback.label
import com.sleeperbaby.app.playback.speedLabel
import com.sleeperbaby.app.ui.theme.Lavender
import com.sleeperbaby.app.ui.theme.Mist
import com.sleeperbaby.app.ui.theme.MuteText
import com.sleeperbaby.app.ui.theme.NightCard
import com.sleeperbaby.app.ui.theme.NightCardSelected
import com.sleeperbaby.app.ui.theme.NightNavy
import com.sleeperbaby.app.ui.theme.SkyDeep
import com.sleeperbaby.app.ui.theme.SkyMid
import com.sleeperbaby.app.ui.theme.SoftPeach
import com.sleeperbaby.app.ui.theme.WarmGold

@Composable
fun RadioScreen(onPlayRequest: () -> Unit) {
    val state by SleepRadioController.state.collectAsStateWithLifecycle()
    val selected = Catalog.station(state.station)
    val channel = Catalog.channel(state.channelId)
    var playerExpanded by remember { mutableStateOf(true) }
    val listBottomPadding by animateDpAsState(
        targetValue = (if (playerExpanded) 338.dp else 124.dp) + AdBannerHeight + 8.dp,
        label = "list-bottom-padding",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(Unit) {
            StoryLibraryController.scheduleDailyPopup()
        }
        NightSkyBackground()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = listBottomPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SLEEPER",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                        )
                        Text(
                            text = "Baby",
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmGold,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                        Text(
                            text = "Nanas, ruidos suaves y luz para dormir",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.82f),
                            modifier = Modifier.padding(top = 8.dp, bottom = 14.dp),
                        )
                    }
                    TodayGiftButton()
                }
            }
            Catalog.stations.forEach { station ->
                item(key = station.kind.name) {
                    StationCard(
                        station = station,
                        selected = station.kind == state.station,
                        onClick = { SleepRadioController.selectStation(station.kind) },
                    )
                }
            }
            item {
                LibrarySection()
            }
            item {
                Text(
                    text = "Canal de ${selected.title}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Mist,
                    modifier = Modifier.padding(top = 8.dp),
                )
                ChannelRow(
                    channels = selected.channels,
                    selectedId = state.channelId,
                    onSelect = SleepRadioController::selectChannel,
                )
            }
            item {
                NightLightCard()
            }
        }

        LibraryOverlay(bottomPadding = listBottomPadding)
        StoryReaderOverlay(bottomPadding = listBottomPadding)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            PlayerPanel(
                state = state,
                station = selected,
                channel = channel,
                expanded = playerExpanded,
                onToggleExpanded = { playerExpanded = !playerExpanded },
                onPlayRequest = onPlayRequest,
            )
            Spacer(Modifier.height(8.dp))
            BannerAd()
        }
        DailyStoryPopup()
        NightLightOverlay()
    }
}

@Composable
private fun NightSkyBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyDeep, NightNavy, SkyMid))),
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val galaxy = Offset(size.width * 0.86f, size.height * 0.07f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Lavender.copy(alpha = 0.28f), Color.Transparent),
                center = galaxy,
                radius = size.minDimension * 0.62f,
            ),
            radius = size.minDimension * 0.62f,
            center = galaxy,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(WarmGold.copy(alpha = 0.22f), Color(0xFF3E5C86).copy(alpha = 0.12f), Color.Transparent),
                center = galaxy,
                radius = size.minDimension * 0.34f,
            ),
            radius = size.minDimension * 0.34f,
            center = galaxy,
        )
        starField.forEach { star ->
            val center = Offset(star.x * size.width, star.y * size.height)
            drawCircle(
                color = Color.White.copy(alpha = star.alpha),
                radius = star.radius,
                center = center,
                style = Fill,
            )
        }
    }
}

private data class SkyStar(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
)

private val starField: List<SkyStar> = List(86) { index ->
    val seed = kotlin.random.Random(index * 131 + 17)
    SkyStar(
        x = seed.nextFloat(),
        y = seed.nextFloat() * 0.72f,
        radius = 0.7f + seed.nextFloat() * 1.8f,
        alpha = 0.18f + seed.nextFloat() * 0.55f,
    )
}

@Composable
private fun StationCard(
    station: Station,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val border by animateColorAsState(
        if (selected) WarmGold.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.22f),
        label = "station-border",
    )
    val shape = RoundedCornerShape(30.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 118.dp)
            .shadow(if (selected) 14.dp else 0.dp, shape, clip = false)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (selected) NightCardSelected else NightCard,
                        if (selected) Color(0xCC2A4A72) else Color(0x99203A5C),
                    ),
                ),
            )
            .border(1.dp, border, shape)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StationLeading(station = station, selected = selected)
        Column(modifier = Modifier.weight(1f)) {
            Text(station.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(station.subtitle, style = MaterialTheme.typography.bodyMedium, color = MuteText)
        }
        val trailing = station.trailingArt
        if (trailing != null) {
            StationArtCircle(artRes = trailing, selected = selected)
        }
    }
}

@Composable
private fun StationArtCircle(
    artRes: Int,
    selected: Boolean,
) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .shadow(if (selected) 10.dp else 4.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(Color(0xFF1A3358))
            .border(
                1.5.dp,
                if (selected) WarmGold.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.28f),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(artRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )
    }
}

@Composable
private fun StationLeading(
    station: Station,
    selected: Boolean,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        if (selected) WarmGold.copy(alpha = 0.38f) else Color.White.copy(alpha = 0.14f),
                        if (selected) WarmGold.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.04f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            resId = station.leadingArt,
            contentDescription = station.title,
            modifier = Modifier.size(28.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NightLightCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(listOf(NightCardSelected, NightCard)),
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
            .padding(18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Image(
                painter = painterResource(AppIcons.shush),
                contentDescription = "Luz nocturna",
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Fit,
            )
            Column {
                Text("Luz nocturna", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(
                    text = "Lámpara suave o siluetas en la pared",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MuteText,
                )
            }
        }
        FlowRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                NightLightMode.WarmLamp,
                NightLightMode.Animals,
                NightLightMode.Stars,
            ).forEach { mode ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(WarmGold)
                        .clickable { NightLightController.setMode(mode) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    AppIcon(
                        resId = AppIcons.nightLight(mode),
                        contentDescription = mode.label(),
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = mode.label(),
                        color = NightNavy,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChannelRow(
    channels: List<Channel>,
    selectedId: String,
    onSelect: (String) -> Unit,
) {
    val groups = channels.groupBy { it.section }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        groups.forEach { (section, items) ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (section != null) {
                    Text(
                        text = section,
                        style = MaterialTheme.typography.labelLarge,
                        color = MuteText,
                    )
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items.forEach { channel ->
                        ChannelChip(
                            channel = channel,
                            selected = channel.id == selectedId,
                            onSelect = onSelect,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelChip(
    channel: Channel,
    selected: Boolean,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .shadow(if (selected) 10.dp else 0.dp, RoundedCornerShape(50), clip = false)
            .clip(RoundedCornerShape(50))
            .background(if (selected) WarmGold else Color.White.copy(alpha = 0.08f))
            .border(
                1.dp,
                if (selected) WarmGold else Color.White.copy(alpha = 0.18f),
                RoundedCornerShape(50),
            )
            .clickable { onSelect(channel.id) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AppIcon(
            resId = channel.iconRes,
            contentDescription = channel.label,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = channel.label,
            color = if (selected) NightNavy else Mist,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun PlayerPanel(
    state: PlayerState,
    station: Station,
    channel: Channel?,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onPlayRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (state.isPlaying) 1.06f else 1f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "play-scale",
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(18.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), clip = false)
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xE8243A5C), Color(0xF2111C30))),
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)),
                RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            )
            .animateContentSize()
            .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onToggleExpanded)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                resId = AppIcons.chevronDown,
                contentDescription = if (expanded) "Minimizar reproductor" else "Mostrar reproductor",
                modifier = Modifier
                    .size(28.dp)
                    .rotate(if (expanded) 0f else 180f),
            )
        }
        if (!expanded) {
            MiniPlayerBar(
                state = state,
                station = station,
                channel = channel,
                onPlayRequest = onPlayRequest,
                onExpand = onToggleExpanded,
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            val story = state.currentStory()
            val isStory = state.kind == PlaybackKind.Story
            val heading = if (isStory) "Biblioteca" else station.title
            val line = when {
                isStory && story != null -> "${story.title} • ${story.origin}"
                channel != null -> "${channel.label} • ${channel.hint}"
                else -> "Elige un canal"
            }
            val footer = if (isStory) {
                "Toca la velocidad para oírlo más rápido. El temporizador también vale para el cuento."
            } else {
                "Bucle continuo. Baja el volumen y deja el móvil cerca, no en la cuna."
            }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(heading, color = WarmGold, style = MaterialTheme.typography.labelLarge)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (!isStory && channel != null) {
                                AppIcon(channel.iconRes, null, Modifier.size(18.dp))
                            }
                            if (isStory) {
                                AppIcon(AppIcons.library, null, Modifier.size(18.dp))
                            }
                            Text(
                                text = line,
                                color = Mist,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                    IconButton(onClick = NightLightController::open) {
                        NightLightOrb()
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    IconButton(onClick = { SleepRadioController.cycleChannel(-1) }) {
                        AppIcon(AppIcons.previous, "Canal anterior", Modifier.size(28.dp))
                    }
                    PlayButton(
                        isPlaying = state.isPlaying,
                        scale = scale,
                        size = 76.dp,
                        iconSize = if (state.isPlaying) 28.dp else 30.dp,
                        onPlayRequest = onPlayRequest,
                    )
                    IconButton(onClick = { SleepRadioController.cycleChannel(1) }) {
                        AppIcon(AppIcons.next, "Canal siguiente", Modifier.size(28.dp))
                    }
                }
                if (isStory) {
                    Text(
                        "Velocidad",
                        color = MuteText,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    SpeedRow(selected = state.playbackSpeed)
                }
                if (state.isActive) {
                    IconButton(
                        onClick = SleepRadioController::stop,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        AppIcon(AppIcons.stop, "Detener", Modifier.size(26.dp))
                    }
                }
                Text("Temporizador", color = MuteText, style = MaterialTheme.typography.labelLarge)
                TimerRow(selected = state.timerMinutes, remainingSeconds = state.remainingSeconds)
                Spacer(Modifier.height(8.dp))
                Text("Volumen", color = MuteText, style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Slider(
                        value = state.volume,
                        onValueChange = SleepRadioController::setVolume,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = SoftPeach,
                            activeTrackColor = WarmGold,
                            inactiveTrackColor = Color.White.copy(alpha = 0.12f),
                        ),
                    )
                    AppIcon(
                        resId = AppIcons.volumeStar,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = footer,
                    color = MuteText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerBar(
    state: PlayerState,
    station: Station,
    channel: Channel?,
    onPlayRequest: () -> Unit,
    onExpand: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onExpand),
        ) {
            val story = state.currentStory()
            val isStory = state.kind == PlaybackKind.Story
            Text(
                text = if (isStory) "Biblioteca" else station.title,
                color = WarmGold,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = when {
                    isStory && story != null -> story.title
                    channel != null -> channel.label
                    else -> "Elige un canal"
                },
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = { SleepRadioController.cycleChannel(-1) }) {
            AppIcon(AppIcons.previous, "Anterior", Modifier.size(22.dp))
        }
        PlayButton(
            isPlaying = state.isPlaying,
            scale = 1f,
            size = 44.dp,
            iconSize = if (state.isPlaying) 18.dp else 20.dp,
            onPlayRequest = onPlayRequest,
        )
        IconButton(onClick = { SleepRadioController.cycleChannel(1) }) {
            AppIcon(AppIcons.next, "Siguiente", Modifier.size(22.dp))
        }
        if (state.kind == PlaybackKind.Story) {
            Text(
                text = speedLabel(state.playbackSpeed),
                color = NightNavy,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(WarmGold)
                    .clickable(onClick = SleepRadioController::cyclePlaybackSpeed)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            )
        }
        IconButton(onClick = NightLightController::open) {
            NightLightOrb()
        }
    }
}

@Composable
private fun NightLightOrb() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .shadow(8.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(SoftPeach, WarmGold))),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(AppIcons.shush),
            contentDescription = "Luz nocturna",
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun PlayButton(
    isPlaying: Boolean,
    scale: Float,
    size: Dp,
    iconSize: Dp,
    onPlayRequest: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .shadow(8.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(SoftPeach, WarmGold),
                ),
            )
            .clickable(onClick = onPlayRequest),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            resId = if (isPlaying) AppIcons.pause else AppIcons.play,
            contentDescription = if (isPlaying) "Pausar" else "Reproducir",
            modifier = Modifier.size(iconSize),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpeedRow(selected: Float) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SPEED_STEPS.forEach { speed ->
            val isOn = selected == speed
            Text(
                text = speedLabel(speed),
                color = if (isOn) NightNavy else Mist,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isOn) WarmGold else Color.White.copy(alpha = 0.06f))
                    .border(
                        1.dp,
                        if (isOn) WarmGold else Color.White.copy(alpha = 0.22f),
                        RoundedCornerShape(50),
                    )
                    .clickable { SleepRadioController.setPlaybackSpeed(speed) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimerRow(selected: Int?, remainingSeconds: Int?) {
    val options = listOf<Int?>(null, 15, 30, 45, 60, 90)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { minutes ->
            val label = if (minutes == null) {
                "∞"
            } else if (selected == minutes && remainingSeconds != null) {
                formatRemaining(remainingSeconds)
            } else {
                "${minutes}m"
            }
            val isOn = selected == minutes
            Text(
                text = label,
                color = if (isOn) NightNavy else Mist,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isOn) Lavender else Color.White.copy(alpha = 0.06f))
                    .border(
                        1.dp,
                        if (isOn) Lavender else Color.White.copy(alpha = 0.22f),
                        RoundedCornerShape(50),
                    )
                    .clickable { SleepRadioController.setTimerMinutes(minutes) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            )
        }
    }
}

private fun formatRemaining(total: Int): String {
    val minutes = total / 60
    val seconds = total % 60
    return "%d:%02d".format(minutes, seconds)
}
