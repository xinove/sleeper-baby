package com.sleeperbaby.app.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class NightLightMode {
    Off,
    WarmLamp,
    Animals,
    Stars,
}

data class NightLightState(
    val mode: NightLightMode = NightLightMode.Off,
    val lastScene: NightLightMode = NightLightMode.Animals,
    val brightness: Float = 0.5f,
    val controlsVisible: Boolean = true,
)

object NightLightController {
    private val mutableState = MutableStateFlow(NightLightState())
    val state: StateFlow<NightLightState> = mutableState.asStateFlow()

    fun setMode(mode: NightLightMode) {
        mutableState.update { current ->
            val brightness = when (mode) {
                NightLightMode.Off -> current.brightness
                NightLightMode.WarmLamp -> 0.28f
                NightLightMode.Animals, NightLightMode.Stars ->
                    if (current.mode.isProjector()) current.brightness else 0.7f
            }
            current.copy(
                mode = mode,
                lastScene = if (mode == NightLightMode.Off) current.lastScene else mode,
                brightness = brightness,
                controlsVisible = mode != NightLightMode.Off,
            )
        }
    }

    fun open() {
        val current = mutableState.value
        if (current.mode == NightLightMode.Off) {
            setMode(current.lastScene)
        } else {
            showControls()
        }
    }

    fun close() {
        mutableState.update { it.copy(mode = NightLightMode.Off, controlsVisible = false) }
    }

    fun setBrightness(value: Float) {
        mutableState.update { it.copy(brightness = value.coerceIn(0.08f, 1f)) }
    }

    fun toggleControls() {
        mutableState.update { it.copy(controlsVisible = !it.controlsVisible) }
    }

    fun showControls() {
        mutableState.update { it.copy(controlsVisible = true) }
    }

    fun hideControls() {
        mutableState.update { it.copy(controlsVisible = false) }
    }
}

fun NightLightMode.label(): String = when (this) {
    NightLightMode.Off -> "Apagada"
    NightLightMode.WarmLamp -> "Luz cálida"
    NightLightMode.Animals -> "Animalitos"
    NightLightMode.Stars -> "Cielo"
}

fun NightLightMode.isProjector(): Boolean = when (this) {
    NightLightMode.Off, NightLightMode.WarmLamp -> false
    NightLightMode.Animals, NightLightMode.Stars -> true
}
