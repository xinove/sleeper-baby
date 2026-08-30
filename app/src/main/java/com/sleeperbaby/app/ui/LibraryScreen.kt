package com.sleeperbaby.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Castle
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Sailing
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sleeperbaby.app.R
import com.sleeperbaby.app.library.Story
import com.sleeperbaby.app.library.StoryCatalog
import com.sleeperbaby.app.library.StoryId
import com.sleeperbaby.app.library.StoryLibraryController
import com.sleeperbaby.app.library.StoryShelf
import com.sleeperbaby.app.library.StoryTtsController
import com.sleeperbaby.app.library.StoryVoiceStatus
import com.sleeperbaby.app.library.STORY_CLOSING
import com.sleeperbaby.app.ui.theme.Ink
import com.sleeperbaby.app.ui.theme.Mist
import com.sleeperbaby.app.ui.theme.MuteText
import com.sleeperbaby.app.ui.theme.NightCard
import com.sleeperbaby.app.ui.theme.NightNavy
import com.sleeperbaby.app.ui.theme.SoftPeach
import com.sleeperbaby.app.ui.theme.WarmGold
import kotlinx.coroutines.delay

@Composable
fun LibrarySection() {
    val tale = StoryLibraryController.todayTale()
    val adventure = StoryLibraryController.todayAdventure()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 108.dp)
            .sleeperCard()
            .clickable(onClick = StoryLibraryController::openLibrary)
            .padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SleeperIconWell {
            AppIcon(
                resId = AppIcons.library,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Biblioteca", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                text = "Cuentos y decide tu aventura",
                style = MaterialTheme.typography.bodyMedium,
                color = MuteText,
            )
            Text(
                text = "Hoy: ${tale.title} · ${adventure.title}",
                style = MaterialTheme.typography.labelLarge,
                color = WarmGold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        SleeperArtCircle(artRes = AppIcons.libraryArt)
    }
}

@Composable
fun TodayGiftButton() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 8.dp)
            .clickable(onClick = StoryLibraryController::openTodayGift),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(SoftPeach, WarmGold))),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                resId = AppIcons.gift,
                contentDescription = "Regalos de hoy",
                modifier = Modifier.size(26.dp),
            )
        }
        Text(
            text = "Hoy",
            color = WarmGold,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun LibraryOverlay(bottomPadding: Dp = 108.dp) {
    val visible by StoryLibraryController.showLibrary.collectAsStateWithLifecycle()
    if (!visible) return
    val todayTale = StoryLibraryController.todayTale()
    val todayAdventure = StoryLibraryController.todayAdventure()
    val hint by StoryLibraryController.lockedHint.collectAsStateWithLifecycle()
    var browse by remember { mutableStateOf<LibraryBrowse>(LibraryBrowse.Home) }
    val atHome = browse is LibraryBrowse.Home
    BackHandler {
        if (atHome) {
            StoryLibraryController.closeLibrary()
        } else {
            browse = LibraryBrowse.Home
        }
    }
    LaunchedEffect(hint) {
        if (hint != null) {
            delay(2200)
            StoryLibraryController.clearLockedHint()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(NightNavy, Color(0xFF1A3358))),
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = bottomPadding),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OverlayBar(
                title = when (val room = browse) {
                    LibraryBrowse.Home -> "Biblioteca"
                    LibraryBrowse.Adventures -> "Decide tu aventura"
                    is LibraryBrowse.Section -> room.shelf.title
                },
                subtitle = if (atHome) {
                    "Elige una estantería"
                } else {
                    "Hoy: ${todayTale.title} · ${todayAdventure.title}"
                },
                onClose = {
                    if (atHome) {
                        StoryLibraryController.closeLibrary()
                    } else {
                        browse = LibraryBrowse.Home
                    }
                },
                closeLabel = if (atHome) "Cerrar biblioteca" else "Volver a la estantería",
            )
            val todayIds = setOf(todayTale.id, todayAdventure.id)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                when (val room = browse) {
                    LibraryBrowse.Home -> LibraryShelfHome(
                        onOpenSection = { browse = LibraryBrowse.Section(it) },
                        onOpenAdventures = { browse = LibraryBrowse.Adventures },
                    )
                    LibraryBrowse.Adventures -> StoryGrid(
                        stories = StoryCatalog.adventures,
                        todayIds = todayIds,
                    )
                    is LibraryBrowse.Section -> StoryGrid(
                        stories = StoryCatalog.stories.filter { room.shelf in it.shelves },
                        todayIds = todayIds,
                    )
                }
            }
        }
        val message = hint
        if (message != null) {
            Text(
                text = message,
                color = NightNavy,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
                    .clip(SleeperChipShape)
                    .background(WarmGold)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}

private sealed class LibraryBrowse {
    data object Home : LibraryBrowse()
    data object Adventures : LibraryBrowse()
    data class Section(val shelf: StoryShelf) : LibraryBrowse()
}

private val librarySections = listOf(
    StoryShelf.Classic,
    StoryShelf.Modern,
    StoryShelf.Christmas,
    StoryShelf.Heroes,
    StoryShelf.Magic,
    StoryShelf.Princesses,
    StoryShelf.Pirates,
)

private val LibraryTileShape = RoundedCornerShape(28.dp)

private data class ShelfTile(
    val title: String,
    val art: Int,
    val onClick: () -> Unit,
)

@Composable
private fun LibraryShelfHome(
    onOpenSection: (StoryShelf) -> Unit,
    onOpenAdventures: () -> Unit,
) {
    Text(
        text = "Toca una estantería para entrar",
        color = MuteText,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 14.dp),
    )
    val tiles = librarySections.map { shelf ->
        ShelfTile(shelf.title, shelfArt(shelf)) { onOpenSection(shelf) }
    } + ShelfTile("Aventuras", AppIcons.libraryArt, onOpenAdventures)
    tiles.chunked(2).forEach { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            row.forEach { tile ->
                ShelfBook(
                    title = tile.title,
                    art = tile.art,
                    onClick = tile.onClick,
                    modifier = Modifier.weight(1f),
                )
            }
            if (row.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ShelfBook(
    title: String,
    art: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(LibraryTileShape)
                .background(Color(0xFF1A3358))
                .border(1.5.dp, Color.White.copy(alpha = 0.22f), LibraryTileShape),
        ) {
            Image(
                painter = painterResource(art),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )
        }
        Text(
            text = title,
            color = Mist,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
        )
    }
}

@Composable
private fun StoryGrid(
    stories: List<Story>,
    todayIds: Set<StoryId>,
) {
    stories.chunked(2).forEach { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            row.forEach { story ->
                val unlocked = StoryLibraryController.isUnlocked(story)
                StoryCoverCard(
                    story = story,
                    isToday = unlocked && story.id in todayIds,
                    locked = !unlocked,
                    modifier = Modifier.weight(1f),
                    onClick = { StoryLibraryController.open(story) },
                )
            }
            repeat(2 - row.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun shelfArt(shelf: StoryShelf): Int = when (shelf) {
    StoryShelf.All -> R.drawable.ic_library
    StoryShelf.Classic -> R.drawable.ill_shelf_classic
    StoryShelf.Modern -> R.drawable.ill_shelf_modern
    StoryShelf.Christmas -> R.drawable.ill_shelf_christmas
    StoryShelf.Heroes -> R.drawable.ill_shelf_heroes
    StoryShelf.Magic -> R.drawable.ill_shelf_magic
    StoryShelf.Princesses -> R.drawable.ill_shelf_princesses
    StoryShelf.Pirates -> R.drawable.ill_shelf_pirates
}

@Composable
private fun StoryCoverCard(
    story: Story,
    isToday: Boolean,
    locked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cover = Color(story.coverColor.toInt())
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(LibraryTileShape)
                .then(
                    if (story.coverArt != null) {
                        Modifier.background(cover)
                    } else {
                        Modifier.background(
                            Brush.verticalGradient(
                                listOf(cover.copy(alpha = 0.95f), NightNavy.copy(alpha = 0.55f)),
                            ),
                        )
                    },
                )
                .border(
                    1.5.dp,
                    if (isToday) WarmGold else Color.White.copy(alpha = 0.22f),
                    LibraryTileShape,
                ),
        ) {
            val art = story.coverArt
            if (art != null) {
                Image(
                    painter = painterResource(art),
                    contentDescription = story.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = if (locked) 0.55f else 1f,
                )
            } else {
                Icon(
                    imageVector = storyGlyph(story.id),
                    contentDescription = story.title,
                    tint = WarmGold.copy(alpha = if (locked) 0.35f else 1f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(38.dp),
                )
            }
            if (locked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NightNavy.copy(alpha = 0.45f)),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(NightNavy.copy(alpha = 0.82f))
                        .border(1.dp, WarmGold.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    AppIcon(
                        resId = AppIcons.lock,
                        contentDescription = "Cerrado",
                        modifier = Modifier.size(13.dp),
                    )
                }
            } else if (isToday) {
                Text(
                    text = "Hoy",
                    color = NightNavy,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(SleeperChipShape)
                        .background(WarmGold)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
        }
        Text(
            text = story.title,
            color = if (locked) MuteText else Mist,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
fun DailyStoryPopup() {
    val visible by StoryLibraryController.showDailyPopup.collectAsStateWithLifecycle()
    if (!visible) return
    BackHandler { StoryLibraryController.dismissPopup() }
    val tale = StoryLibraryController.todayTale()
    val adventure = StoryLibraryController.todayAdventure()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.58f))
                .clickable(onClick = StoryLibraryController::dismissPopup),
        )
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 36.dp)
                    .clip(SleeperCardShape)
                    .background(NightCard)
                    .border(1.dp, WarmGold.copy(alpha = 0.45f), SleeperCardShape)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, top = 44.dp, end = 18.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Regalos de hoy", color = WarmGold, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "Un cuento y una aventura, cada día.",
                    color = MuteText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, bottom = 14.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DailyGiftChoice(
                        label = "Cuento",
                        story = tale,
                        modifier = Modifier.weight(1f),
                    )
                    DailyGiftChoice(
                        label = "Aventura",
                        story = adventure,
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    text = "Más tarde",
                    color = Mist,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clip(SleeperChipShape)
                        .border(1.dp, Color.White.copy(alpha = 0.22f), SleeperChipShape)
                        .clickable(onClick = StoryLibraryController::dismissPopup)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
            Box(
                modifier = Modifier
                    .offset(y = 8.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(SoftPeach, WarmGold))),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    resId = AppIcons.gift,
                    contentDescription = "Regalo del día",
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun DailyGiftChoice(
    label: String,
    story: Story,
    modifier: Modifier = Modifier,
) {
    val cover = Color(story.coverColor.toInt())
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, color = WarmGold, style = MaterialTheme.typography.labelLarge)
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                    .height(120.dp)
                    .clip(SleeperCoverShape)
                    .background(cover)
                    .clickable { StoryLibraryController.open(story) },
            contentAlignment = Alignment.Center,
        ) {
            val art = story.coverArt
            if (art != null) {
                Image(
                    painter = painterResource(art),
                    contentDescription = story.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    imageVector = storyGlyph(story.id),
                    contentDescription = null,
                    tint = WarmGold,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
        Text(
            text = story.title,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SleeperChip(
                label = "Leer",
                selected = true,
                onClick = { StoryLibraryController.open(story) },
            )
            SleeperChip(
                label = "Oír",
                selected = false,
                onClick = { StoryLibraryController.openAndListen(story) },
            )
        }
    }
}

@Composable
fun StoryReaderOverlay(bottomPadding: Dp = 108.dp) {
    val story by StoryLibraryController.openStory.collectAsStateWithLifecycle()
    val current = story ?: return
    val adventure = current.adventure
    var nodeId by remember(current.id) { mutableStateOf(adventure?.startId.orEmpty()) }
    val node = adventure?.node(nodeId)
    val voice by StoryTtsController.state.collectAsStateWithLifecycle()
    val listeningNow = voice.storyId == current.id &&
        voice.status != StoryVoiceStatus.Idle
    var keepListening by remember(current.id) { mutableStateOf(false) }
    LaunchedEffect(listeningNow) {
        if (listeningNow) keepListening = true
    }
    val scroll = rememberScrollState()
    LaunchedEffect(nodeId) { scroll.animateScrollTo(0) }
    BackHandler { StoryLibraryController.closeReader() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(NightNavy, Color(0xFF1A3358))),
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = bottomPadding),
    ) {
            OverlayBar(
                title = current.title,
                subtitle = if (adventure != null) "Pregunta a tu peque" else current.origin,
                onClose = StoryLibraryController::closeReader,
                closeLabel = "Cerrar cuento",
                trailing = {
                    IconButton(onClick = { StoryLibraryController.openAndListen(current, node?.id) }) {
                        AppIcon(AppIcons.listen, "Escuchar ${current.title}", Modifier.size(22.dp))
                    }
                },
            )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(SleeperCardShape)
                .background(SoftPeach)
                .verticalScroll(scroll)
                .padding(22.dp),
        ) {
            val art = current.coverArt
            if (art != null) {
                Image(
                    painter = painterResource(art),
                    contentDescription = current.title,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(196.dp)
                            .clip(SleeperCoverShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    imageVector = storyGlyph(current.id),
                    contentDescription = null,
                    tint = Color(current.coverColor.toInt()),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(42.dp),
                )
            }
            Text(
                text = current.title,
                color = Ink,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 16.dp),
            )
            val pages = node?.paragraphs ?: current.paragraphs
            pages.forEach { paragraph ->
                Text(
                    text = paragraph,
                    color = Ink.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                    ),
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
            val question = node?.question
            if (question != null) {
                Text(
                    text = question,
                    color = Color(current.coverColor.toInt()),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                )
                node.choices.forEach { choice ->
                    Text(
                        text = choice.label,
                        color = NightNavy,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(SleeperCardShape)
                            .background(WarmGold)
                            .clickable {
                                nodeId = choice.nextId
                                if (keepListening || listeningNow) {
                                    StoryLibraryController.openAndListen(current, choice.nextId)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    )
                }
            } else {
                Text(
                    text = STORY_CLOSING,
                    color = Color(current.coverColor.toInt()),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                )
                if (adventure != null) {
                    Text(
                        text = "Empezar otra vez",
                        color = NightNavy,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(SleeperChipShape)
                            .background(WarmGold)
                            .clickable {
                                nodeId = adventure.startId
                                if (keepListening || listeningNow) {
                                    StoryLibraryController.openAndListen(current, adventure.startId)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

private fun storyGlyph(id: StoryId): ImageVector = when (id) {
    StoryId.Caperucita -> Icons.Outlined.Park
    StoryId.TresCerditos -> Icons.Outlined.Cottage
    StoryId.Ricitos -> Icons.Outlined.Restaurant
    StoryId.PatitoFeo -> Icons.Outlined.Waves
    StoryId.Cenicienta -> Icons.Outlined.Watch
    StoryId.BellaDurmiente -> Icons.Outlined.Hotel
    StoryId.GatoConBotas -> Icons.Outlined.Pets
    StoryId.PrincesaGuisante -> Icons.Outlined.Grass
    StoryId.LiebreTortuga -> Icons.AutoMirrored.Outlined.DirectionsWalk
    StoryId.Pulgarcito -> Icons.Outlined.ChildCare
    StoryId.Soldadito -> Icons.Outlined.Shield
    StoryId.Rapunzel -> Icons.Outlined.Castle
    StoryId.NubeCohete -> Icons.Outlined.RocketLaunch
    StoryId.RobotDormilon -> Icons.Outlined.SmartToy
    StoryId.EstrellaNavidad -> Icons.Outlined.Star
    StoryId.RenoCalcetin -> Icons.Outlined.AcUnit
    StoryId.CapitanLuciernaga -> Icons.Outlined.FlashOn
    StoryId.Superabuela -> Icons.Outlined.Favorite
    StoryId.BosqueSusurros -> Icons.Outlined.AutoAwesome
    StoryId.PrincesaNube -> Icons.Outlined.Cloud
    StoryId.GuillePirata -> Icons.Outlined.Sailing
    StoryId.TesoroGalleta -> Icons.Outlined.Sailing
    StoryId.IslaSiesta -> Icons.Outlined.Hotel
    StoryId.DueloCumplidos -> Icons.Outlined.Favorite
    StoryId.GuilleElige -> Icons.Outlined.AltRoute
    StoryId.EstrellaElige -> Icons.Outlined.AltRoute
    StoryId.NinoElige -> Icons.Outlined.AltRoute
}
