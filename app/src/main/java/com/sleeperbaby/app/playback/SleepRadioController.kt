package com.sleeperbaby.app.playback

import com.sleeperbaby.app.audio.AudioEngine
import com.sleeperbaby.app.data.Catalog
import com.sleeperbaby.app.data.StationKind
import com.sleeperbaby.app.library.Story
import com.sleeperbaby.app.library.StoryCatalog
import com.sleeperbaby.app.library.StoryId
import com.sleeperbaby.app.library.StoryLibraryController
import com.sleeperbaby.app.library.StoryTtsController
import com.sleeperbaby.app.library.StoryVoiceStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PlaybackKind {
    Radio,
    Story,
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val isActive: Boolean = false,
    val kind: PlaybackKind = PlaybackKind.Radio,
    val station: StationKind = StationKind.Lullaby,
    val channelId: String = Catalog.station(StationKind.Lullaby).channels.first().id,
    val storyId: StoryId? = null,
    val volume: Float = 0.7f,
    val playbackSpeed: Float = 1f,
    val timerMinutes: Int? = null,
    val remainingSeconds: Int? = null,
)

object SleepRadioController {
    private val engine = AudioEngine()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutableState = MutableStateFlow(PlayerState())
    private var timerJob: Job? = null

    val state: StateFlow<PlayerState> = mutableState.asStateFlow()

    fun play(channelId: String = mutableState.value.channelId) {
        if (mutableState.value.kind == PlaybackKind.Story) {
            playCurrent()
            return
        }
        startRadio(channelId)
    }

    fun playStory(story: Story, nodeId: String? = null) {
        engine.stop()
        engine.setDuck(1f)
        StoryTtsController.setVolume(mutableState.value.volume)
        StoryTtsController.setSpeed(mutableState.value.playbackSpeed)
        StoryTtsController.play(story, nodeId)
        mutableState.update {
            it.copy(
                isPlaying = true,
                isActive = true,
                kind = PlaybackKind.Story,
                storyId = story.id,
            )
        }
        restartTimerIfNeeded()
    }

    private fun startRadio(channelId: String) {
        val channel = Catalog.channel(channelId) ?: return
        val station = Catalog.stationFor(channelId) ?: return
        StoryTtsController.stop(notifyPlayer = false)
        engine.setDuck(1f)
        engine.setSource(channel.createSource(engine.sampleRate()))
        engine.setVolume(mutableState.value.volume)
        engine.start()
        mutableState.update {
            it.copy(
                isPlaying = true,
                isActive = true,
                kind = PlaybackKind.Radio,
                station = station.kind,
                channelId = channelId,
                storyId = null,
            )
        }
        restartTimerIfNeeded()
    }

    fun pause() {
        val current = mutableState.value
        if (!current.isPlaying && !current.isActive) return
        when (current.kind) {
            PlaybackKind.Radio -> engine.stop()
            PlaybackKind.Story -> StoryTtsController.pause()
        }
        timerJob?.cancel()
        mutableState.update {
            it.copy(
                isPlaying = false,
                isActive = true,
                remainingSeconds = it.timerMinutes?.times(60),
            )
        }
    }

    fun stop() {
        engine.stop()
        StoryTtsController.stop(notifyPlayer = false)
        engine.setDuck(1f)
        timerJob?.cancel()
        mutableState.update {
            it.copy(
                isPlaying = false,
                isActive = false,
                remainingSeconds = it.timerMinutes?.times(60),
            )
        }
    }

    fun toggle() {
        val current = mutableState.value
        when {
            current.isPlaying -> pause()
            current.kind == PlaybackKind.Story -> playCurrent()
            current.isActive -> play()
            else -> play()
        }
    }

    fun selectStation(kind: StationKind) {
        val first = Catalog.station(kind).channels.first()
        if (mutableState.value.isPlaying || mutableState.value.kind == PlaybackKind.Story) {
            startRadio(first.id)
        } else {
            mutableState.update {
                it.copy(kind = PlaybackKind.Radio, station = kind, channelId = first.id, storyId = null)
            }
        }
    }

    fun selectChannel(channelId: String) {
        if (mutableState.value.isPlaying || mutableState.value.kind == PlaybackKind.Story) {
            startRadio(channelId)
        } else {
            val station = Catalog.stationFor(channelId) ?: return
            mutableState.update {
                it.copy(kind = PlaybackKind.Radio, station = station.kind, channelId = channelId, storyId = null)
            }
        }
    }

    fun cycleChannel(delta: Int) {
        if (mutableState.value.kind == PlaybackKind.Story) {
            cycleStory(delta)
            return
        }
        val station = Catalog.station(mutableState.value.station)
        val index = station.channels.indexOfFirst { it.id == mutableState.value.channelId }
        val nextIndex = Math.floorMod(index + delta, station.channels.size)
        selectChannel(station.channels[nextIndex].id)
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        engine.setVolume(clamped)
        StoryTtsController.setVolume(clamped)
        mutableState.update { it.copy(volume = clamped) }
    }

    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.75f, 2f)
        StoryTtsController.setSpeed(clamped)
        mutableState.update { it.copy(playbackSpeed = clamped) }
    }

    fun cyclePlaybackSpeed() {
        val steps = SPEED_STEPS
        val current = mutableState.value.playbackSpeed
        val index = steps.indexOfFirst { it == current }.let { found ->
            if (found < 0) 0 else found
        }
        setPlaybackSpeed(steps[(index + 1) % steps.size])
    }

    fun setTimerMinutes(minutes: Int?) {
        mutableState.update {
            it.copy(
                timerMinutes = minutes,
                remainingSeconds = minutes?.times(60),
            )
        }
        restartTimerIfNeeded()
    }

    fun onNarrationEnded() {
        if (mutableState.value.kind != PlaybackKind.Story) return
        timerJob?.cancel()
        engine.setDuck(1f)
        mutableState.update {
            it.copy(
                isPlaying = false,
                isActive = false,
                remainingSeconds = it.timerMinutes?.times(60),
            )
        }
    }

    private fun playCurrent() {
        val current = mutableState.value
        when (current.kind) {
            PlaybackKind.Radio -> startRadio(current.channelId)
            PlaybackKind.Story -> {
                val story = current.storyId?.let(StoryCatalog::story) ?: return
                if (StoryTtsController.state.value.status == StoryVoiceStatus.Paused &&
                    StoryTtsController.state.value.storyId == story.id
                ) {
                    StoryTtsController.setVolume(current.volume)
                    StoryTtsController.resume()
                    mutableState.update { it.copy(isPlaying = true, isActive = true) }
                    restartTimerIfNeeded()
                } else {
                    playStory(story, StoryTtsController.activeNodeId())
                }
            }
        }
    }

    private fun cycleStory(delta: Int) {
        val unlocked = StoryLibraryController.unlockedStories()
        if (unlocked.isEmpty()) return
        val index = unlocked.indexOfFirst { it.id == mutableState.value.storyId }.coerceAtLeast(0)
        val next = unlocked[Math.floorMod(index + delta, unlocked.size)]
        if (mutableState.value.isPlaying) {
            playStory(next)
        } else {
            mutableState.update { it.copy(storyId = next.id) }
        }
    }

    private fun restartTimerIfNeeded() {
        timerJob?.cancel()
        val minutes = mutableState.value.timerMinutes ?: return
        if (!mutableState.value.isPlaying) return
        var remaining = minutes * 60
        mutableState.update { it.copy(remainingSeconds = remaining) }
        timerJob = scope.launch {
            while (remaining > 0) {
                delay(1_000)
                remaining--
                mutableState.update { it.copy(remainingSeconds = remaining) }
            }
            stop()
        }
    }
}

fun PlayerState.currentStory(): Story? = storyId?.let(StoryCatalog::story)

val SPEED_STEPS: List<Float> = listOf(1f, 1.25f, 1.5f, 1.75f, 2f)

fun speedLabel(speed: Float): String {
    val text = if (speed % 1f == 0f) {
        speed.toInt().toString()
    } else {
        speed.toString().trimEnd('0').trimEnd('.')
    }
    return "${text}×"
}
