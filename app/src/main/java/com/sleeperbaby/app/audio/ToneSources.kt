package com.sleeperbaby.app.audio

import kotlin.math.PI
import kotlin.math.sin

class WarmToneSource(
    private val sampleRate: Int,
    private val frequency: Double,
    private val warmth: Float,
) : AudioSource {
    private var phase = 0.0
    private var subPhase = 0.0
    private var breath = 0.0

    override fun fill(buffer: FloatArray) {
        val step = frequency * twoPi / sampleRate
        val subStep = (frequency / 2.0) * twoPi / sampleRate
        val breathStep = 0.08 * twoPi / sampleRate
        for (i in buffer.indices) {
            val envelope = 0.82 + 0.18 * sin(breath)
            val fundamental = sin(phase)
            val sub = sin(subPhase) * warmth
            buffer[i] = ((fundamental * (1f - warmth * 0.35f) + sub) * 0.18 * envelope).toFloat()
            phase += step
            subPhase += subStep
            breath += breathStep
            if (phase > twoPi) phase -= twoPi
            if (subPhase > twoPi) subPhase -= twoPi
        }
    }
}

class FrequencyPadSource(private val sampleRate: Int) : AudioSource {
    private var p432 = 0.0
    private var p216 = 0.0
    private var p648 = 0.0
    private var lfo = 0.0

    override fun fill(buffer: FloatArray) {
        val s432 = 432.0 * twoPi / sampleRate
        val s216 = 216.0 * twoPi / sampleRate
        val s648 = 648.0 * twoPi / sampleRate
        val lfoStep = 0.05 * twoPi / sampleRate
        for (i in buffer.indices) {
            val slow = 0.75 + 0.25 * sin(lfo)
            val mix = sin(p432) * 0.14 + sin(p216) * 0.08 + sin(p648) * 0.03
            buffer[i] = (mix * slow).toFloat()
            p432 += s432
            p216 += s216
            p648 += s648
            lfo += lfoStep
            if (p432 > twoPi) p432 -= twoPi
            if (p216 > twoPi) p216 -= twoPi
            if (p648 > twoPi) p648 -= twoPi
        }
    }
}

private const val twoPi = PI * 2.0
