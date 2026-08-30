package com.sleeperbaby.app.library

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Handler
import android.os.Looper
import com.sleeperbaby.app.playback.SleepRadioController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class StoryVoiceStatus {
    Idle,
    Preparing,
    Playing,
    Paused,
}

data class StoryVoiceState(
    val status: StoryVoiceStatus = StoryVoiceStatus.Idle,
    val storyId: StoryId? = null,
    val partIndex: Int = 0,
    val available: Boolean = true,
)

object StoryTtsController {
    private val main = Handler(Looper.getMainLooper())
    private val mutableState = MutableStateFlow(StoryVoiceState())
    val state: StateFlow<StoryVoiceState> = mutableState.asStateFlow()

    private var appContext: Context? = null
    private var player: MediaPlayer? = null
    private var pending: Story? = null
    private var pendingNodeId: String? = null
    private var currentStory: Story? = null
    private var currentNodeId: String? = null
    private var volume = 0.7f
    private var speed = 1f

    fun init(context: Context) {
        appContext = context.applicationContext
        mutableState.update { it.copy(status = StoryVoiceStatus.Idle, available = true) }
        val queued = pending
        val queuedNode = pendingNodeId
        pending = null
        pendingNodeId = null
        if (queued != null) {
            play(queued, queuedNode)
        }
    }

    fun play(story: Story, nodeId: String? = null) {
        val context = appContext
        if (context == null) {
            pending = story
            pendingNodeId = nodeId
            mutableState.update {
                it.copy(status = StoryVoiceStatus.Preparing, storyId = story.id)
            }
            return
        }
        stopInternal(clearStory = false)
        currentStory = story
        currentNodeId = nodeId
        mutableState.update {
            it.copy(
                status = StoryVoiceStatus.Preparing,
                storyId = story.id,
                partIndex = 0,
                available = true,
            )
        }
        startFile(context, story, nodeId)
    }

    fun pause() {
        if (mutableState.value.status != StoryVoiceStatus.Playing) return
        runCatching { player?.pause() }
        mutableState.update { it.copy(status = StoryVoiceStatus.Paused) }
    }

    fun resume() {
        val story = currentStory ?: return
        if (mutableState.value.status != StoryVoiceStatus.Paused) return
        val media = player
        if (media == null) {
            play(story, currentNodeId)
            return
        }
        media.setVolume(volume, volume)
        runCatching { media.start() }
        applySpeed(media)
        mutableState.update {
            it.copy(status = StoryVoiceStatus.Playing, storyId = story.id)
        }
    }

    fun activeNodeId(): String? = currentNodeId

    fun toggle(story: Story, nodeId: String? = null) {
        val current = mutableState.value
        when {
            current.status == StoryVoiceStatus.Playing && current.storyId == story.id -> pause()
            current.status == StoryVoiceStatus.Paused && current.storyId == story.id -> resume()
            else -> play(story, nodeId)
        }
    }

    fun stop(notifyPlayer: Boolean = true) {
        pending = null
        stopInternal(clearStory = true)
        mutableState.update {
            it.copy(
                status = StoryVoiceStatus.Idle,
                storyId = null,
                partIndex = 0,
            )
        }
        if (notifyPlayer) {
            SleepRadioController.onNarrationEnded()
        }
    }

    fun setVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
        player?.setVolume(volume, volume)
    }

    fun setSpeed(value: Float) {
        speed = value.coerceIn(0.75f, 2f)
        applySpeed(player)
    }

    private fun startFile(context: Context, story: Story, nodeId: String? = null) {
        val path = story.audioAssetPath(nodeId)
        val descriptor = openStoryAsset(context, path)
        if (descriptor == null) {
            mutableState.update {
                it.copy(status = StoryVoiceStatus.Idle, available = false)
            }
            SleepRadioController.onNarrationEnded()
            return
        }
        val media = MediaPlayer()
        player = media
        media.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        media.setVolume(volume, volume)
        fun closeDescriptor() {
            runCatching { descriptor.close() }
        }
        media.setOnPreparedListener {
            closeDescriptor()
            if (currentStory?.id != story.id || player !== media) {
                media.release()
                return@setOnPreparedListener
            }
            media.start()
            applySpeed(media)
            mutableState.update {
                it.copy(status = StoryVoiceStatus.Playing, storyId = story.id)
            }
        }
        media.setOnCompletionListener {
            main.post {
                if (player === media) {
                    stop()
                }
            }
        }
        media.setOnErrorListener { _, _, _ ->
            closeDescriptor()
            main.post {
                if (player === media) {
                    stop()
                }
            }
            true
        }
        try {
            media.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length,
            )
            media.prepareAsync()
        } catch (_: Exception) {
            closeDescriptor()
            stopInternal(clearStory = false)
            mutableState.update {
                it.copy(status = StoryVoiceStatus.Idle, available = false)
            }
            SleepRadioController.onNarrationEnded()
        }
    }

    private fun applySpeed(media: MediaPlayer?) {
        if (media == null) return
        runCatching {
            val params = PlaybackParams().setSpeed(speed).setPitch(1f)
            media.playbackParams = params
        }
    }

    private fun openStoryAsset(context: Context, path: String): AssetFileDescriptor? =
        runCatching { context.assets.openFd(path) }.getOrNull()

    private fun stopInternal(clearStory: Boolean) {
        val media = player
        player = null
        if (media != null) {
            runCatching { media.stop() }
            media.release()
        }
        if (clearStory) {
            currentStory = null
            currentNodeId = null
        }
    }
}
