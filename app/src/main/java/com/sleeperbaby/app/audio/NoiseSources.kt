package com.sleeperbaby.app.audio

import kotlin.math.sin
import kotlin.random.Random

class WhiteNoiseSource : AudioSource {
    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            buffer[i] = (Random.nextFloat() * 2f - 1f) * 0.22f
        }
    }
}

class PinkNoiseSource : AudioSource {
    private var b0 = 0.0
    private var b1 = 0.0
    private var b2 = 0.0
    private var b3 = 0.0
    private var b4 = 0.0
    private var b5 = 0.0
    private var b6 = 0.0

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            val white = Random.nextDouble() * 2.0 - 1.0
            b0 = 0.99886 * b0 + white * 0.0555179
            b1 = 0.99332 * b1 + white * 0.0750759
            b2 = 0.96900 * b2 + white * 0.1538520
            b3 = 0.86650 * b3 + white * 0.3104856
            b4 = 0.55000 * b4 + white * 0.5329522
            b5 = -0.7616 * b5 - white * 0.0168980
            val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
            b6 = white * 0.115926
            buffer[i] = (pink * 0.11).toFloat().coerceIn(-0.35f, 0.35f)
        }
    }
}

class BrownNoiseSource : AudioSource {
    private var last = 0.0

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            val white = Random.nextDouble() * 2.0 - 1.0
            last += white * 0.02
            last = last.coerceIn(-1.0, 1.0)
            buffer[i] = (last * 0.28).toFloat()
        }
    }
}

class RainNoiseSource(private val sampleRate: Int) : AudioSource {
    private val pink = PinkNoiseSource()
    private var dropPhase = 0
    private var dropAmp = 0f
    private var nextDrop = sampleRate / 3

    override fun fill(buffer: FloatArray) {
        pink.fill(buffer)
        for (i in buffer.indices) {
            buffer[i] *= 0.55f
            if (dropPhase >= nextDrop) {
                dropAmp = 0.18f + Random.nextFloat() * 0.12f
                dropPhase = 0
                nextDrop = (sampleRate * (0.12f + Random.nextFloat() * 0.55f)).toInt()
            }
            if (dropAmp > 0.0008f) {
                buffer[i] += dropAmp * (Random.nextFloat() * 2f - 1f)
                dropAmp *= 0.992f
            }
            dropPhase++
        }
    }
}

class OceanNoiseSource(private val sampleRate: Int) : AudioSource {
    private val brown = BrownNoiseSource()
    private var t = 0.0

    override fun fill(buffer: FloatArray) {
        brown.fill(buffer)
        val slow = 0.07 * twoPi / sampleRate
        val swell = 0.031 * twoPi / sampleRate
        for (i in buffer.indices) {
            val wave = 0.62 + 0.28 * sin(t * slow) + 0.10 * sin(t * swell + 1.7)
            buffer[i] = (buffer[i] * wave.toFloat() * 0.95f)
            t += 1.0
        }
    }
}

class FanNoiseSource(private val sampleRate: Int) : AudioSource {
    private val brown = BrownNoiseSource()
    private var phase60 = 0.0
    private var phase120 = 0.0

    override fun fill(buffer: FloatArray) {
        brown.fill(buffer)
        val step60 = 60.0 * twoPi / sampleRate
        val step120 = 120.0 * twoPi / sampleRate
        for (i in buffer.indices) {
            val hum = (sin(phase60) * 0.04 + sin(phase120) * 0.025).toFloat()
            buffer[i] = buffer[i] * 0.7f + hum
            phase60 += step60
            phase120 += step120
        }
    }
}

private const val twoPi = Math.PI * 2.0
