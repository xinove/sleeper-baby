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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import com.sleeperbaby.app.data.LullabyShelf
import com.sleeperbaby.app.data.Station
import com.sleeperbaby.app.data.StationKind
import com.sleeperbaby.app.data.lullabyInfinite
import com.sleeperbaby.app.data.lullabyMix
import com.sleeperbaby.app.data.lullabyShelf
import com.sleeperbaby.app.data.lullabySongs
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
    var playerExpanded by remember { mutableStateOf(false) }
    val listBottomPadding by animateDpAsState(
        targetValue = (if (playerExpanded) 292.dp else 92.dp) + AdBannerHeight + 8.dp,
        label = "list-bottom-padding",
    )
    val listState = rememberLazyListState()
    var stationScrollReady by remember { mutableStateOf(false) }
    LaunchedEffect(state.station) {
        if (!stationScrollReady) {
            stationScrollReady = true
            return@LaunchedEffect
        }
        val index = 1 + Catalog.stations.indexOfFirst { it.kind == state.station }.coerceAtLeast(0)
        listState.animateScrollToItem(index)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(Unit) {
            StoryLibraryController.scheduleDailyPopup()
        }
        NightSkyBackground()
        LazyColumn(
            state = listState,
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                    ) {
                        StationCard(
                            station = station,
                            selected = station.kind == state.station,
                            onClick = { SleepRadioController.selectStation(station.kind) },
                        )
                        AnimatedVisibility(
                            visible = station.kind == state.station,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                        ) {
                            StationChannelPicker(
                                station = station,
                                selectedId = state.channelId,
                                onSelect = SleepRadioController::selectChannel,
                            )
                        }
                    }
                }
            }
            item {
                LibrarySection()
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 108.dp)
            .clip(SleeperCardShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (selected) NightCardSelected else NightCard,
                        if (selected) Color(0xCC2A4A72) else Color(0x99203A5C),
                    ),
                ),
            )
            .border(1.dp, border, SleeperCardShape)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SleeperIconWell(selected = selected) {
            AppIcon(
                resId = station.leadingArt,
                contentDescription = station.title,
                modifier = Modifier.size(28.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(station.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(station.subtitle, style = MaterialTheme.typography.bodyMedium, color = MuteText)
        }
        val trailing = station.trailingArt
        if (trailing != null) {
            SleeperArtCircle(artRes = trailing, selected = selected)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NightLightCard() {
    val light by NightLightController.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sleeperCard()
            .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SleeperIconWell {
                AppIcon(
                    resId = AppIcons.nightLight,
                    contentDescription = "Luz nocturna",
                    modifier = Modifier.size(26.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Luz nocturna", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(
                    text = "Lámpara suave o siluetas en la pared",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MuteText,
                )
            }
            SleeperArtCircle(artRes = AppIcons.nightLightArt)
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
                SleeperChip(
                    label = mode.label(),
                    selected = light.mode == mode,
                    onClick = { NightLightController.setMode(mode) },
                    icon = {
                        AppIcon(
                            resId = AppIcons.nightLight(mode),
                            contentDescription = mode.label(),
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun StationChannelPicker(
    station: Station,
    selectedId: String,
    onSelect: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (station.kind == StationKind.Lullaby) {
            Text(
                text = "Elige un estilo. Luego la mezcla o una nana concreta.",
                style = MaterialTheme.typography.bodyMedium,
                color = MuteText,
                modifier = Modifier.padding(top = 12.dp),
            )
            LullabyPicker(
                station = station,
                selectedId = selectedId,
                onSelect = onSelect,
            )
        } else {
            Text(
                text = "Canal de ${station.title}",
                style = MaterialTheme.typography.labelLarge,
                color = MuteText,
                modifier = Modifier.padding(top = 12.dp),
            )
            ChannelRow(
                channels = station.channels,
                selectedId = selectedId,
                onSelect = onSelect,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LullabyPicker(
    station: Station,
    selectedId: String,
    onSelect: (String) -> Unit,
) {
    val selectedChannel = station.channels.firstOrNull { it.id == selectedId }
    var shelf by remember { mutableStateOf(selectedChannel?.lullabyShelf() ?: LullabyShelf.All) }
    LaunchedEffect(selectedId) {
        shelf = station.channels.firstOrNull { it.id == selectedId }?.lullabyShelf() ?: LullabyShelf.All
    }
    val mix = station.lullabyMix(shelf)
    val infinite = station.lullabyInfinite()
    val songs = station.lullabySongs(shelf)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LullabyShelf.entries.forEach { option ->
                SleeperChip(
                    label = option.title,
                    selected = option == shelf,
                    onClick = {
                        shelf = option
                        station.lullabyMix(option)?.let { onSelect(it.id) }
                    },
                )
            }
        }
        if (mix != null) {
            LullabyChoiceRow(
                channel = mix,
                selected = mix.id == selectedId,
                onSelect = onSelect,
            )
        }
        if (shelf == LullabyShelf.All && infinite != null) {
            LullabyChoiceRow(
                channel = infinite,
                selected = infinite.id == selectedId,
                onSelect = onSelect,
            )
        }
        if (songs.isNotEmpty()) {
            Text(
                text = "O una nana",
                style = MaterialTheme.typography.labelLarge,
                color = MuteText,
                modifier = Modifier.padding(top = 4.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                songs.forEach { channel ->
                    LullabyChoiceRow(
                        channel = channel,
                        selected = channel.id == selectedId,
                        onSelect = onSelect,
                    )
                }
            }
        }
    }
}

@Composable
private fun LullabyChoiceRow(
    channel: Channel,
    selected: Boolean,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SleeperCardShape)
            .background(if (selected) WarmGold else Color.White.copy(alpha = 0.08f))
            .border(
                1.dp,
                if (selected) WarmGold else Color.White.copy(alpha = 0.14f),
                SleeperCardShape,
            )
            .clickable { onSelect(channel.id) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AppIcon(
            resId = channel.iconRes,
            contentDescription = channel.label,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = channel.label,
                color = if (selected) NightNavy else Mist,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = channel.hint,
                color = if (selected) NightNavy.copy(alpha = 0.7f) else MuteText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
                        SleeperChip(
                            label = channel.label,
                            selected = channel.id == selectedId,
                            onClick = { onSelect(channel.id) },
                            icon = {
                                AppIcon(
                                    resId = channel.iconRes,
                                    contentDescription = channel.label,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                        )
                    }
                }
            }
        }
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
            .shadow(18.dp, SleeperSheetShape, clip = false)
            .clip(SleeperSheetShape)
            .background(
                Brush.verticalGradient(listOf(Color(0xE8243A5C), Color(0xF2111C30))),
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)),
                SleeperSheetShape,
            )
            .animateContentSize()
            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SleeperChipShape)
                .clickable(onClick = onToggleExpanded)
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                resId = AppIcons.chevronDown,
                contentDescription = if (expanded) "Minimizar reproductor" else "Mostrar reproductor",
                modifier = Modifier
                    .size(20.dp)
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
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    IconButton(onClick = { SleepRadioController.cycleChannel(-1) }) {
                        AppIcon(AppIcons.previous, "Canal anterior", Modifier.size(28.dp))
                    }
                    PlayButton(
                        isPlaying = state.isPlaying,
                        scale = scale,
                        size = 64.dp,
                        iconSize = if (state.isPlaying) 24.dp else 26.dp,
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
                Spacer(Modifier.height(4.dp))
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
                    modifier = Modifier.padding(bottom = 2.dp),
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
            .padding(bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
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
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = when {
                    isStory && story != null -> story.title
                    channel != null -> channel.label
                    else -> "Elige un canal"
                },
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(
            onClick = { SleepRadioController.cycleChannel(-1) },
            modifier = Modifier.size(36.dp),
        ) {
            AppIcon(AppIcons.previous, "Anterior", Modifier.size(18.dp))
        }
        PlayButton(
            isPlaying = state.isPlaying,
            scale = 1f,
            size = 36.dp,
            iconSize = if (state.isPlaying) 16.dp else 18.dp,
            onPlayRequest = onPlayRequest,
        )
        IconButton(
            onClick = { SleepRadioController.cycleChannel(1) },
            modifier = Modifier.size(36.dp),
        ) {
            AppIcon(AppIcons.next, "Siguiente", Modifier.size(18.dp))
        }
        if (state.kind == PlaybackKind.Story) {
            SleeperChip(
                label = speedLabel(state.playbackSpeed),
                selected = true,
                onClick = SleepRadioController::cyclePlaybackSpeed,
            )
        }
        IconButton(
            onClick = NightLightController::open,
            modifier = Modifier.size(36.dp),
        ) {
            NightLightOrb(size = 32.dp)
        }
    }
}

@Composable
private fun NightLightOrb(size: Dp = 44.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(8.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(SoftPeach, WarmGold))),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            resId = AppIcons.nightLight,
            contentDescription = "Luz nocturna",
            modifier = Modifier.size(size * 0.52f),
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
            SleeperChip(
                label = speedLabel(speed),
                selected = selected == speed,
                onClick = { SleepRadioController.setPlaybackSpeed(speed) },
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
            SleeperChip(
                label = label,
                selected = isOn,
                onClick = { SleepRadioController.setTimerMinutes(minutes) },
            )
        }
    }
}

private fun formatRemaining(total: Int): String {
    val minutes = total / 60
    val seconds = total % 60
    return "%d:%02d".format(minutes, seconds)
}
