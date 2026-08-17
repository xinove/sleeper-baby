package com.sleeperbaby.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.min

class AudioEngine {
    private val sampleRate = 44_100
    private val bufferFrames = 2048

    @Volatile
    private var running = false

    @Volatile
    private var volume = 0.75f

    @Volatile
    private var duck = 1f

    @Volatile
    private var fade = 0f

    @Volatile
    private var fadeTarget = 0f

    private val lock = Any()
    private var current: AudioSource = SilentSource
    private var incoming: AudioSource? = null
    private var mix = 1f

    private var track: AudioTrack? = null
    private var thread: Thread? = null

    private val mixA = FloatArray(bufferFrames)
    private val mixB = FloatArray(bufferFrames)
    private val pcm = ShortArray(bufferFrames)

    @Synchronized
    fun start() {
        if (running) {
            fadeTarget = 1f
            return
        }
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(minBuf.coerceAtLeast(bufferFrames * 4))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        running = true
        fade = 0f
        fadeTarget = 1f
        track?.play()
        thread = Thread(::loop, "sleeper-audio").also { it.start() }
    }

    @Synchronized
    fun stop() {
        fadeTarget = 0f
        running = false
        thread?.join(400)
        thread = null
        track?.stop()
        track?.release()
        track = null
        fade = 0f
    }

    fun setVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
    }

    fun setDuck(factor: Float) {
        duck = factor.coerceIn(0.05f, 1f)
    }

    fun setSource(source: AudioSource) {
        synchronized(lock) {
            incoming = source
            mix = 1f
        }
    }

    fun sampleRate(): Int = sampleRate

    private fun loop() {
        val localTrack = track ?: return
        while (running) {
            render()
            val written = localTrack.write(pcm, 0, pcm.size)
            if (written < 0) break
        }
    }

    private fun render() {
        val next: AudioSource?
        synchronized(lock) {
            next = incoming
        }
        current.fill(mixA)
        if (next != null) {
            next.fill(mixB)
            val step = 1f / (sampleRate * 0.22f)
            for (i in mixA.indices) {
                mixA[i] = mixA[i] * mix + mixB[i] * (1f - mix)
                mix = (mix - step).coerceAtLeast(0f)
            }
            if (mix <= 0f) {
                synchronized(lock) {
                    current = next
                    if (incoming === next) incoming = null
                    mix = 1f
                }
            }
        }
        val fadeStep = 1f / (sampleRate * 0.18f)
        for (i in mixA.indices) {
            if (fade < fadeTarget) fade = min(fadeTarget, fade + fadeStep)
            if (fade > fadeTarget) fade = (fade - fadeStep).coerceAtLeast(fadeTarget)
            val sample = (mixA[i] * volume * duck * fade).coerceIn(-1f, 1f)
            pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
    }
}

private object SilentSource : AudioSource {
    override fun fill(buffer: FloatArray) {
        buffer.fill(0f)
    }
}
