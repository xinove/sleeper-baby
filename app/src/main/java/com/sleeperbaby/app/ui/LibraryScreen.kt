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
import androidx.compose.material.icons.outlined.Castle
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.sleeperbaby.app.library.Story
import com.sleeperbaby.app.library.StoryCatalog
import com.sleeperbaby.app.library.StoryId
import com.sleeperbaby.app.library.StoryLibraryController
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
    val today = StoryLibraryController.today()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 118.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.verticalGradient(listOf(NightCard, Color(0x99203A5C))),
            )
            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(30.dp))
            .clickable(onClick = StoryLibraryController::openLibrary)
            .padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(WarmGold.copy(alpha = 0.28f), Color.White.copy(alpha = 0.06f)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                resId = AppIcons.library,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Biblioteca", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                text = "Solo el cuento de hoy",
                style = MaterialTheme.typography.bodyMedium,
                color = MuteText,
            )
            Text(
                text = "Hoy: ${today.title}",
                style = MaterialTheme.typography.labelLarge,
                color = WarmGold,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        AppIcon(
            resId = AppIcons.gift,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
fun TodayGiftButton() {
    val today = StoryLibraryController.today()
    val cover = Color(today.coverColor.toInt())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 8.dp)
            .clickable(onClick = StoryLibraryController::openTodayGift),
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(cover)
                    .border(2.dp, WarmGold, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                val art = today.coverArt
                if (art != null) {
                    Image(
                        painter = painterResource(art),
                        contentDescription = today.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = storyGlyph(today.id),
                        contentDescription = today.title,
                        tint = WarmGold,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(WarmGold)
                    .border(1.dp, SoftPeach, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    resId = AppIcons.gift,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                )
            }
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
    val today = StoryLibraryController.today()
    val hint by StoryLibraryController.lockedHint.collectAsStateWithLifecycle()
    BackHandler { StoryLibraryController.closeLibrary() }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = StoryLibraryController::closeLibrary) {
                    AppIcon(AppIcons.close, "Cerrar biblioteca", Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Biblioteca", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Hoy: ${today.title}",
                        color = MuteText,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                StoryCatalog.stories.chunked(3).forEach { row ->
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
                                isToday = unlocked && story.id == today.id,
                                locked = !unlocked,
                                modifier = Modifier.weight(1f),
                                onClick = { StoryLibraryController.open(story) },
                            )
                        }
                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
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
                    .clip(RoundedCornerShape(50))
                    .background(WarmGold)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
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
                .height(if (story.coverArt != null) 132.dp else 118.dp)
                .shadow(if (isToday) 10.dp else 4.dp, RoundedCornerShape(18.dp), clip = false)
                .clip(RoundedCornerShape(18.dp))
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
                    1.dp,
                    if (isToday) WarmGold else Color.White.copy(alpha = 0.22f),
                    RoundedCornerShape(18.dp),
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
            } else {
                if (isToday) {
                    Text(
                        text = "Hoy",
                        color = NightNavy,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(WarmGold)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WarmGold)
                        .clickable { StoryLibraryController.openAndListen(story) },
                    contentAlignment = Alignment.Center,
                ) {
                    AppIcon(
                        resId = AppIcons.listen,
                        contentDescription = "Escuchar ${story.title}",
                        modifier = Modifier.size(16.dp),
                        tint = NightNavy,
                    )
                }
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
    val story = StoryLibraryController.today()
    val cover = Color(story.coverColor.toInt())

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
                .padding(horizontal = 28.dp, vertical = 18.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 36.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(NightCard)
                    .border(1.dp, WarmGold.copy(alpha = 0.45f), RoundedCornerShape(28.dp))
                    .padding(start = 22.dp, top = 44.dp, end = 22.dp, bottom = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Regalo de hoy", color = WarmGold, style = MaterialTheme.typography.labelLarge)
                Box(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .then(
                            if (story.coverArt != null) {
                                Modifier
                                    .fillMaxWidth()
                                    .height(132.dp)
                            } else {
                                Modifier.size(88.dp)
                            },
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(cover),
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
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }
                Text(
                    text = story.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 14.dp),
                )
                Text(
                    text = story.teaser,
                    color = MuteText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Row(
                    modifier = Modifier.padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Más tarde",
                        color = Mist,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(50))
                            .clickable(onClick = StoryLibraryController::dismissPopup)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                    Text(
                        text = "Leer ahora",
                        color = NightNavy,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(WarmGold)
                            .clickable(onClick = StoryLibraryController::readToday)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                    Text(
                        text = "Escuchar",
                        color = NightNavy,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(WarmGold)
                            .clickable { StoryLibraryController.openAndListen(story) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .offset(y = 8.dp)
                    .size(72.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(WarmGold)
                    .border(2.dp, SoftPeach, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    resId = AppIcons.gift,
                    contentDescription = "Regalo del día",
                    modifier = Modifier.size(36.dp),
                )
            }
        }
    }
}

@Composable
fun StoryReaderOverlay(bottomPadding: Dp = 108.dp) {
    val story by StoryLibraryController.openStory.collectAsStateWithLifecycle()
    val current = story ?: return
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = StoryLibraryController::closeReader) {
                AppIcon(AppIcons.close, "Cerrar cuento", Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(current.title, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(current.origin, color = MuteText, style = MaterialTheme.typography.labelLarge)
            }
            IconButton(onClick = { StoryLibraryController.openAndListen(current) }) {
                AppIcon(AppIcons.listen, "Escuchar ${current.title}", Modifier.size(22.dp))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SoftPeach)
                .verticalScroll(rememberScrollState())
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
                        .clip(RoundedCornerShape(18.dp)),
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
            current.paragraphs.forEach { paragraph ->
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
            Text(
                text = "Buenas noches.",
                color = Color(current.coverColor.toInt()),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
            )
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
}
