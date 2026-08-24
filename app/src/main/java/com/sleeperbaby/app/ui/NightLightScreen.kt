package com.sleeperbaby.app.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sleeperbaby.app.playback.NightLightController
import com.sleeperbaby.app.playback.NightLightMode
import com.sleeperbaby.app.playback.NightLightState
import com.sleeperbaby.app.playback.SleepRadioController
import com.sleeperbaby.app.playback.isProjector
import com.sleeperbaby.app.playback.label
import com.sleeperbaby.app.ui.theme.Lavender
import com.sleeperbaby.app.ui.theme.Mist
import com.sleeperbaby.app.ui.theme.MuteText
import com.sleeperbaby.app.ui.theme.NightCard
import com.sleeperbaby.app.ui.theme.NightNavy
import com.sleeperbaby.app.ui.theme.WarmGold
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun NightLightOverlay() {
    val light by NightLightController.state.collectAsStateWithLifecycle()
    if (light.mode == NightLightMode.Off) return

    BackHandler { NightLightController.close() }
    KeepAwake(brightness = light.brightness)

    LaunchedEffect(light.controlsVisible, light.mode) {
        if (light.controlsVisible) {
            delay(5_000)
            NightLightController.hideControls()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = NightLightController::toggleControls,
            ),
    ) {
        when (val mode = light.mode) {
            NightLightMode.Off -> Unit
            NightLightMode.WarmLamp -> WarmLampCanvas()
            NightLightMode.Animals -> ProjectorCanvas(NightLightMode.Animals)
            NightLightMode.Stars -> ProjectorCanvas(NightLightMode.Stars)
        }
        AnimatedVisibility(
            visible = light.controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            NightLightControls(light)
        }
        AnimatedVisibility(
            visible = light.controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(12.dp),
        ) {
            IconButton(
                onClick = NightLightController::close,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NightCard.copy(alpha = 0.72f)),
            ) {
                AppIcon(AppIcons.close, "Cerrar luz", Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun WarmLampCanvas() {
    val pulse = rememberInfiniteTransition(label = "lamp")
    val glow by pulse.animateFloat(
        initialValue = 0.82f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4200), RepeatMode.Reverse),
        label = "lamp-glow",
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFE0A3).copy(alpha = glow),
                    Color(0xFFE8A45A).copy(alpha = 0.92f * glow),
                    Color(0xFF6B3E16),
                ),
                center = center,
                radius = size.maxDimension * 0.72f,
            ),
        )
    }
}

@Composable
private fun ProjectorCanvas(mode: NightLightMode) {
    val motion = rememberInfiniteTransition(label = "projector")
    val t by motion.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(48_000, easing = LinearEasing)),
        label = "orbit",
    )
    val twinkle by motion.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(7_000, easing = LinearEasing)),
        label = "twinkle",
    )
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val w = size.width
        val h = size.height
        when (mode) {
            NightLightMode.Animals -> {
                drawStar(Offset(w * 0.16f, h * 0.14f), 18f, 0.35f + 0.25f * sin(twinkle))
                drawStar(Offset(w * 0.82f, h * 0.18f), 14f, 0.3f + 0.3f * sin(twinkle + 1.4f))
                drawStar(Offset(w * 0.7f, h * 0.1f), 10f, 0.25f + 0.35f * sin(twinkle + 2.2f))
                drawMoon(
                    x = w * 0.62f + 18f * sin(t * 0.4f),
                    y = h * 0.08f + 10f * sin(t * 0.6f),
                    size = w * 0.22f,
                    rotation = 8f * sin(t * 0.3f),
                    alpha = 0.92f,
                )
                drawBear(
                    x = w * 0.08f + 24f * sin(t),
                    y = h * 0.34f + 18f * sin(t * 0.7f),
                    size = w * 0.38f,
                    rotation = 5f * sin(t * 0.8f),
                    alpha = 0.95f,
                )
                drawBunny(
                    x = w * 0.52f + 20f * sin(t + 1.2f),
                    y = h * 0.42f + 16f * sin(t * 0.9f + 0.6f),
                    size = w * 0.32f,
                    rotation = -6f * sin(t * 0.75f),
                    alpha = 0.95f,
                )
                drawOwl(
                    x = w * 0.28f + 16f * sin(t + 2.1f),
                    y = h * 0.62f + 14f * sin(t * 0.65f),
                    size = w * 0.28f,
                    rotation = 4f * sin(t * 0.5f),
                    alpha = 0.94f,
                )
                drawBird(
                    x = w * 0.58f + 30f * sin(t * 0.85f + 0.4f),
                    y = h * 0.72f + 20f * sin(t * 0.55f + 1.1f),
                    size = w * 0.24f,
                    rotation = 10f * sin(t * 0.9f),
                    alpha = 0.9f,
                )
            }
            NightLightMode.Stars -> {
                drawMoon(
                    x = w * 0.18f + 12f * sin(t * 0.35f),
                    y = h * 0.16f + 10f * sin(t * 0.5f),
                    size = w * 0.36f,
                    rotation = 6f * sin(t * 0.25f),
                    alpha = 0.96f,
                )
                val stars = listOf(
                    Offset(0.62f, 0.12f),
                    Offset(0.78f, 0.22f),
                    Offset(0.88f, 0.38f),
                    Offset(0.7f, 0.48f),
                    Offset(0.52f, 0.28f),
                    Offset(0.14f, 0.58f),
                    Offset(0.32f, 0.7f),
                    Offset(0.86f, 0.68f),
                    Offset(0.58f, 0.78f),
                    Offset(0.22f, 0.86f),
                )
                stars.forEachIndexed { index, ratio ->
                    val sparkle = 0.28f + 0.72f * (0.5f + 0.5f * sin(twinkle + index * 0.9f))
                    drawStar(
                        center = Offset(w * ratio.x, h * ratio.y),
                        radius = 10f + (index % 4) * 6f,
                        alpha = sparkle,
                    )
                }
                drawCloud(
                    x = w * 0.08f + 40f * sin(t * 0.45f),
                    y = h * 0.42f + 12f * sin(t * 0.3f),
                    size = w * 0.42f,
                    alpha = 0.55f,
                )
                drawCloud(
                    x = w * 0.48f + 28f * sin(t * 0.4f + 1.5f),
                    y = h * 0.62f + 10f * sin(t * 0.35f),
                    size = w * 0.34f,
                    alpha = 0.4f,
                )
            }
            NightLightMode.Off, NightLightMode.WarmLamp -> Unit
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NightLightControls(light: NightLightState) {
    val radio by SleepRadioController.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .clip(SleeperCardShape)
            .background(
                Brush.verticalGradient(listOf(Color(0xCC2B4D78), Color(0xE61A3358))),
            )
            .border(1.dp, WarmGold.copy(alpha = 0.28f), SleeperCardShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { NightLightController.showControls() },
            )
            .padding(16.dp),
    ) {
        Text("Luz nocturna", color = WarmGold, style = MaterialTheme.typography.labelLarge)
        Text(
            text = if (light.mode.isProjector()) {
                "Pon el móvil mirando a la pared o al techo"
            } else {
                "Luz suave para la mesita, no dentro de la cuna"
            },
            color = MuteText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(NightLightMode.WarmLamp, NightLightMode.Animals, NightLightMode.Stars).forEach { mode ->
                SleeperChip(
                    label = mode.label(),
                    selected = light.mode == mode,
                    onClick = { NightLightController.setMode(mode) },
                )
            }
        }
        Text(
            text = "Brillo",
            color = MuteText,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 10.dp),
        )
        Slider(
            value = light.brightness,
            onValueChange = NightLightController::setBrightness,
            colors = SliderDefaults.colors(
                thumbColor = WarmGold,
                activeTrackColor = WarmGold,
                inactiveTrackColor = Lavender.copy(alpha = 0.25f),
            ),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = SleepRadioController::toggle) {
                AppIcon(
                    resId = if (radio.isPlaying) AppIcons.pause else AppIcons.play,
                    contentDescription = if (radio.isPlaying) "Pausar radio" else "Reproducir radio",
                    modifier = Modifier.size(24.dp),
                    tint = Mist,
                )
            }
            if (radio.isActive) {
                IconButton(onClick = SleepRadioController::stop) {
                    AppIcon(AppIcons.stop, "Detener radio", Modifier.size(24.dp))
                }
            }
            Text(
                text = when {
                    radio.isPlaying -> "La radio sigue sonando"
                    radio.isActive -> "Radio en pausa"
                    else -> "Radio detenida"
                },
                color = Mist,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun KeepAwake(brightness: Float) {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val previousBrightness = window.attributes.screenBrightness
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val restore = window.attributes
            restore.screenBrightness = previousBrightness
            window.attributes = restore
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
    DisposableEffect(brightness) {
        val window = (view.context as Activity).window
        val attrs = window.attributes
        attrs.screenBrightness = brightness
        window.attributes = attrs
        onDispose { }
    }
}
