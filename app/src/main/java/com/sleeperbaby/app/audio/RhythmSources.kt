package com.sleeperbaby.app.audio

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

class HeartbeatSource(private val sampleRate: Int) : AudioSource {
    private var t = 0
    private val cycle = (sampleRate * 60.0 / 62.0).toInt()
    private val lub = (sampleRate * 0.07).toInt()
    private val dubDelay = (sampleRate * 0.22).toInt()
    private val dub = (sampleRate * 0.05).toInt()

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            val pos = t % cycle
            val first = thump(pos, lub, 0.28f)
            val second = thump(pos - dubDelay, dub, 0.18f)
            buffer[i] = first + second
            t++
        }
    }

    private fun thump(pos: Int, width: Int, amp: Float): Float {
        if (pos < 0 || pos >= width) return 0f
        val n = pos.toFloat() / width
        val env = n * exp(-n * 6.0).toFloat()
        val tone = sin(2.0 * PI * 48.0 * pos / sampleRate).toFloat()
        return tone * env * amp
    }
}

class RockingRhythmSource(private val sampleRate: Int) : AudioSource {
    private var t = 0
    private val beat = (sampleRate * 60.0 / 72.0 / 2.0).toInt()
    private val pattern = intArrayOf(1, 0, 0, 1, 0, 0)
    private var phase = 0.0

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            val step = (t / beat) % pattern.size
            val inBeat = t % beat
            val accent = pattern[step]
            val env = if (accent == 1) {
                val n = inBeat.toFloat() / (sampleRate * 0.12f)
                if (n in 0f..1f) (1f - n) * 0.16f else 0f
            } else {
                0f
            }
            val tone = sin(phase).toFloat()
            buffer[i] = tone * env
            phase += 110.0 * twoPi / sampleRate
            if (phase > twoPi) phase -= twoPi
            t++
        }
    }
}

class MarimbaOstinatoSource(private val sampleRate: Int) : AudioSource {
    private val voiceA = PluckVoice(sampleRate)
    private val voiceB = PluckVoice(sampleRate)
    private val notes = intArrayOf(60, 67, 64, 72, 67, 62, 64, 69)
    private var t = 0
    private var index = 0
    private val stepSamples = (sampleRate * 60.0 / 68.0).toInt()

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            if (t % stepSamples == 0) {
                val midi = notes[index % notes.size]
                if (index % 2 == 0) voiceA.pluck(midi) else voiceB.pluck(midi)
                index++
            }
            buffer[i] = (voiceA.next() + voiceB.next()) * 0.7f
            t++
        }
    }
}

class PulseRhythmSource(private val sampleRate: Int) : AudioSource {
    private var t = 0
    private val cycle = (sampleRate * 1.8).toInt()
    private var noisePhase = 0.0

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            val pos = t % cycle
            val n = pos.toFloat() / cycle
            val env = (0.5f - kotlin.math.abs(n - 0.5f)) * 2f
            val soft = env * env * 0.12f
            val tone = sin(2.0 * PI * 174.0 * t / sampleRate).toFloat()
            val air = sin(noisePhase).toFloat() * 0.015f
            buffer[i] = tone * soft + air
            noisePhase += 420.0 * twoPi / sampleRate
            t++
        }
    }
}

private class PluckVoice(private val sampleRate: Int) {
    private var phase = 0.0
    private var freq = 0.0
    private var env = 0f
    private val twoPi = PI * 2.0

    fun pluck(midi: Int) {
        freq = 440.0 * 2.0.pow((midi - 69) / 12.0)
        env = 1f
        phase = 0.0
    }

    fun next(): Float {
        if (env <= 0.0002f) return 0f
        val sample = (sin(phase) + 0.25 * sin(phase * 2.0)).toFloat() * env * 0.12f
        phase += freq * twoPi / sampleRate
        if (phase > twoPi) phase -= twoPi
        env *= 0.9994f
        return sample
    }
}

private const val twoPi = PI * 2.0
