package com.sleeperbaby.app.audio

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

data class Note(
    val midi: Int,
    val beats: Float,
)

class Melody(
    val bpm: Float,
    val notes: List<Note>,
)

object Melodies {
    val brahms = Melody(
        bpm = 80f,
        notes = listOf(
            Note(67, 1f),
            Note(64, 1f), Note(64, 1f), Note(67, 1f),
            Note(64, 1f), Note(64, 1f), Note(67, 1f),
            Note(64, 1f), Note(67, 1f), Note(72, 1f),
            Note(71, 1f), Note(69, 2f),
            Note(65, 1f), Note(65, 1f), Note(69, 1f),
            Note(65, 1f), Note(65, 1f), Note(69, 1f),
            Note(67, 1f), Note(72, 1f), Note(71, 1f),
            Note(69, 1f), Note(67, 2f),
            Note(72, 1f), Note(72, 1f), Note(72, 1f),
            Note(71, 1f), Note(69, 1f), Note(67, 1f),
            Note(69, 1f), Note(67, 1f), Note(65, 1f),
            Note(64, 3f),
            Note(67, 1f), Note(64, 1f), Note(60, 1f),
            Note(64, 1f), Note(62, 1f), Note(60, 1f),
            Note(62, 2f), Note(59, 1f),
            Note(60, 3f),
        ),
    )

    val twinkle = Melody(
        bpm = 88f,
        notes = listOf(
            Note(60, 1f), Note(60, 1f), Note(67, 1f), Note(67, 1f),
            Note(69, 1f), Note(69, 1f), Note(67, 2f),
            Note(65, 1f), Note(65, 1f), Note(64, 1f), Note(64, 1f),
            Note(62, 1f), Note(62, 1f), Note(60, 2f),
            Note(67, 1f), Note(67, 1f), Note(65, 1f), Note(65, 1f),
            Note(64, 1f), Note(64, 1f), Note(62, 2f),
            Note(67, 1f), Note(67, 1f), Note(65, 1f), Note(65, 1f),
            Note(64, 1f), Note(64, 1f), Note(62, 2f),
            Note(60, 1f), Note(60, 1f), Note(67, 1f), Note(67, 1f),
            Note(69, 1f), Note(69, 1f), Note(67, 2f),
            Note(65, 1f), Note(65, 1f), Note(64, 1f), Note(64, 1f),
            Note(62, 1f), Note(62, 1f), Note(60, 3f),
        ),
    )

    val frereJacques = Melody(
        bpm = 96f,
        notes = listOf(
            Note(60, 1f), Note(62, 1f), Note(64, 1f), Note(60, 1f),
            Note(60, 1f), Note(62, 1f), Note(64, 1f), Note(60, 1f),
            Note(64, 1f), Note(65, 1f), Note(67, 2f),
            Note(64, 1f), Note(65, 1f), Note(67, 2f),
            Note(67, 0.5f), Note(69, 0.5f), Note(67, 0.5f), Note(65, 0.5f),
            Note(64, 1f), Note(60, 1f),
            Note(67, 0.5f), Note(69, 0.5f), Note(67, 0.5f), Note(65, 0.5f),
            Note(64, 1f), Note(60, 1f),
            Note(60, 1f), Note(55, 1f), Note(60, 2f),
            Note(60, 1f), Note(55, 1f), Note(60, 2f),
        ),
    )

    val arrorro = Melody(
        bpm = 78f,
        notes = listOf(
            Note(67, 1.5f), Note(64, 1.5f), Note(67, 1.5f), Note(64, 1.5f),
            Note(65, 1f), Note(64, 1f), Note(62, 2f),
            Note(64, 1f), Note(62, 1f), Note(60, 3f),
            Note(67, 1.5f), Note(64, 1.5f), Note(67, 1.5f), Note(69, 1.5f),
            Note(67, 1f), Note(65, 1f), Note(64, 2f),
            Note(62, 2f), Note(60, 3f),
        ),
    )

    val luna = Melody(
        bpm = 82f,
        notes = listOf(
            Note(60, 1f), Note(64, 1f), Note(67, 1f), Note(64, 1f),
            Note(60, 1f), Note(64, 1f), Note(67, 2f),
            Note(69, 1f), Note(67, 1f), Note(64, 1f), Note(62, 1f),
            Note(60, 2f),
            Note(67, 1f), Note(69, 1f), Note(72, 1f), Note(69, 1f),
            Note(67, 1f), Note(64, 1f), Note(62, 2f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
        ),
    )

    val osito = Melody(
        bpm = 86f,
        notes = listOf(
            Note(64, 1f), Note(67, 1f), Note(64, 1f), Note(60, 1f),
            Note(62, 1f), Note(64, 1f), Note(67, 2f),
            Note(69, 1f), Note(67, 1f), Note(64, 1f), Note(62, 1f),
            Note(60, 2f), Note(64, 1f), Note(67, 1f),
            Note(72, 2f), Note(69, 1f), Note(67, 1f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
        ),
    )

    val duerme = Melody(
        bpm = 80f,
        notes = listOf(
            Note(67, 1.5f), Note(64, 1.5f), Note(60, 1.5f), Note(64, 1.5f),
            Note(67, 1f), Note(69, 1f), Note(67, 2f),
            Note(64, 1.5f), Note(62, 1.5f), Note(60, 2f),
            Note(67, 1.5f), Note(72, 1.5f), Note(69, 1.5f), Note(67, 1.5f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
        ),
    )

    val barquito = Melody(
        bpm = 90f,
        notes = listOf(
            Note(60, 1f), Note(60, 1f), Note(60, 1f), Note(62, 1f), Note(64, 2f),
            Note(64, 1f), Note(62, 1f), Note(64, 1f), Note(65, 1f), Note(67, 2f),
            Note(67, 0.5f), Note(69, 0.5f), Note(67, 0.5f), Note(65, 0.5f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
            Note(64, 1f), Note(67, 1f), Note(60, 2f),
        ),
    )

    val nube = Melody(
        bpm = 84f,
        notes = listOf(
            Note(64, 1f), Note(67, 1f), Note(69, 1f), Note(67, 1f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
            Note(64, 1f), Note(67, 1f), Note(69, 1f), Note(72, 1f),
            Note(67, 2f), Note(64, 1f), Note(62, 1f),
            Note(60, 1f), Note(62, 1f), Note(64, 1f), Note(60, 2f),
        ),
    )

    val cuna = Melody(
        bpm = 78f,
        notes = listOf(
            Note(67, 2f), Note(64, 1f), Note(60, 1f),
            Note(62, 2f), Note(64, 2f),
            Note(67, 1f), Note(69, 1f), Note(67, 1f), Note(64, 1f),
            Note(60, 2f),
            Note(72, 2f), Note(69, 1f), Note(67, 1f),
            Note(64, 2f), Note(62, 2f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
        ),
    )

    val farolito = Melody(
        bpm = 90f,
        notes = listOf(
            Note(60, 1f), Note(64, 1f), Note(67, 1f), Note(69, 1.5f), Note(67, 0.5f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
            Note(67, 1f), Note(69, 1f), Note(72, 1f), Note(69, 1f),
            Note(67, 1f), Note(64, 1f), Note(60, 2f),
            Note(64, 1f), Note(67, 1f), Note(69, 0.5f), Note(67, 0.5f), Note(64, 1f),
            Note(62, 1f), Note(59, 1f), Note(60, 2f),
        ),
    )

    val orilla = Melody(
        bpm = 88f,
        notes = listOf(
            Note(67, 1.5f), Note(64, 1.5f), Note(67, 1f), Note(69, 2f),
            Note(72, 1f), Note(69, 1f), Note(67, 2f),
            Note(64, 1f), Note(62, 1f), Note(60, 1f), Note(62, 1f),
            Note(64, 2f), Note(60, 2f),
            Note(67, 1f), Note(69, 1f), Note(67, 1f), Note(64, 1f),
            Note(62, 1f), Note(64, 1f), Note(60, 2f),
        ),
    )

    val camino = Melody(
        bpm = 92f,
        notes = listOf(
            Note(57, 1f), Note(60, 1f), Note(64, 1f), Note(65, 1f),
            Note(64, 1f), Note(60, 1f), Note(57, 2f),
            Note(55, 1f), Note(57, 1f), Note(60, 1f), Note(64, 1f),
            Note(62, 1f), Note(60, 1f), Note(57, 2f),
            Note(60, 1f), Note(64, 1f), Note(65, 0.5f), Note(64, 0.5f), Note(60, 1f),
            Note(57, 1f), Note(55, 1f), Note(57, 2f),
        ),
    )

    val luces = Melody(
        bpm = 86f,
        notes = listOf(
            Note(72, 1f), Note(69, 1f), Note(67, 1f), Note(64, 1f),
            Note(67, 1f), Note(69, 1f), Note(72, 2f),
            Note(69, 1f), Note(67, 1f), Note(64, 1f), Note(62, 1f),
            Note(60, 2f), Note(64, 1f), Note(67, 1f),
            Note(69, 1f), Note(67, 1f), Note(64, 1f), Note(60, 2f),
        ),
    )

    val nanaRadio = Melody(
        bpm = 94f,
        notes = listOf(
            Note(64, 1f), Note(64, 0.5f), Note(67, 0.5f), Note(69, 1f), Note(67, 1f),
            Note(64, 1f), Note(62, 1f), Note(60, 2f),
            Note(67, 1f), Note(69, 1f), Note(72, 1f), Note(69, 1f),
            Note(67, 1f), Note(64, 1f), Note(60, 2f),
            Note(64, 0.5f), Note(67, 0.5f), Note(69, 1f), Note(67, 1f), Note(64, 1f),
            Note(62, 1f), Note(59, 1f), Note(60, 2f),
        ),
    )

    val classic: List<Melody> = listOf(
        brahms, twinkle, frereJacques, arrorro, luna, osito, duerme, barquito, nube, cuna,
    )

    val modern: List<Melody> = listOf(
        farolito, orilla, camino, luces, nanaRadio,
    )

    val named: List<Melody> = classic + modern
}

class LullabyMelodySource(
    private val sampleRate: Int,
    initial: Melody,
    private val loop: Boolean = true,
    private val onCycle: (() -> Unit)? = null,
) : AudioSource {
    private val treble = VoicePool(sampleRate, 6)
    private val bass = VoicePool(sampleRate, 3)
    private var melody: Melody = initial
    private var noteIndex = 0
    private var samplesLeft = 0
    private var beatCursor = 0f
    private var samplesPerBeat = (sampleRate * 60f / melody.bpm).toInt().coerceAtLeast(1)

    init {
        startNote()
    }

    fun setMelody(next: Melody) {
        melody = next
        noteIndex = 0
        beatCursor = 0f
        samplesPerBeat = (sampleRate * 60f / melody.bpm).toInt().coerceAtLeast(1)
        startNote()
    }

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            var hops = 0
            while (samplesLeft <= 0 && hops < 8) {
                advance()
                hops++
            }
            if (samplesLeft <= 0) {
                samplesLeft = samplesPerBeat
            }
            buffer[i] = treble.next() + bass.next() * 0.42f
            samplesLeft--
        }
    }

    private fun advance() {
        noteIndex++
        if (noteIndex >= melody.notes.size) {
            noteIndex = 0
            beatCursor = 0f
            if (!loop) {
                onCycle?.invoke()
                if (samplesLeft > 0) return
            }
        }
        startNote()
    }

    private fun startNote() {
        if (melody.notes.isEmpty()) {
            samplesLeft = samplesPerBeat
            return
        }
        var skipped = 0
        while (skipped < melody.notes.size && melody.notes[noteIndex].midi <= 0) {
            beatCursor += melody.notes[noteIndex].beats
            noteIndex++
            skipped++
            if (noteIndex >= melody.notes.size) {
                noteIndex = 0
                beatCursor = 0f
                if (!loop) {
                    onCycle?.invoke()
                    if (samplesLeft > 0) return
                }
            }
        }
        val note = melody.notes[noteIndex]
        samplesLeft = (note.beats * samplesPerBeat).toInt().coerceAtLeast(1)
        if (note.midi > 0) {
            treble.pluck(note.midi, 1f)
            if (beatCursor % 3f < 0.01f || noteIndex == 0) {
                bass.pluck(bassMidi(note.midi), 0.7f)
            }
        }
        beatCursor += note.beats
    }

    private fun bassMidi(melodyMidi: Int): Int {
        val pc = Math.floorMod(melodyMidi, 12)
        return when (pc) {
            0, 4, 9 -> 48
            2, 7, 11 -> 43
            else -> 48
        }
    }
}

class LullabyMixSource(
    sampleRate: Int,
    private val songs: List<Melody> = Melodies.named,
) : AudioSource {
    private val remaining = songs.shuffled().toMutableList()
    private val player = LullabyMelodySource(
        sampleRate = sampleRate,
        initial = remaining.removeFirst(),
        loop = false,
        onCycle = ::playNext,
    )

    override fun fill(buffer: FloatArray) {
        player.fill(buffer)
    }

    private fun playNext() {
        if (remaining.isEmpty()) {
            remaining.addAll(songs.shuffled())
        }
        player.setMelody(remaining.removeFirst())
    }
}

class GenerativeLullabySource(private val sampleRate: Int) : AudioSource {
    private val treble = VoicePool(sampleRate, 6)
    private val bass = VoicePool(sampleRate, 3)
    private val bpm = 86f
    private val beat = (sampleRate * 60f / bpm).toInt().coerceAtLeast(1)
    private var samplesLeft = 0
    private var beatInBar = 0
    private var phrase = nextPhrase()
    private var phraseIndex = 0

    init {
        triggerBeat()
    }

    override fun fill(buffer: FloatArray) {
        for (i in buffer.indices) {
            if (samplesLeft <= 0) triggerBeat()
            buffer[i] = treble.next() + bass.next() * 0.4f
            samplesLeft--
        }
    }

    private fun triggerBeat() {
        val midi = phrase[phraseIndex]
        treble.pluck(midi, if (beatInBar == 0) 1f else 0.82f)
        when (beatInBar) {
            0 -> bass.pluck(48, 0.75f)
            2 -> bass.pluck(55, 0.55f)
        }
        samplesLeft = beat
        phraseIndex++
        beatInBar = (beatInBar + 1) % 3
        if (phraseIndex >= phrase.size) {
            phrase = nextPhrase()
            phraseIndex = 0
        }
    }

    private fun nextPhrase(): IntArray {
        val motif = motifs.random()
        val shift = listOf(0, 0, 0, 7, -5).random()
        val transposed = IntArray(motif.size) { index ->
            (motif[index] + shift).coerceIn(55, 79)
        }
        if (Random.nextFloat() < 0.35f) {
            transposed[transposed.lastIndex] = 60
        }
        return transposed
    }

    private companion object {
        val motifs = listOf(
            intArrayOf(64, 67, 64, 60, 62, 64),
            intArrayOf(67, 69, 67, 64, 62, 60),
            intArrayOf(60, 64, 67, 64, 60, 64),
            intArrayOf(72, 71, 69, 67, 64, 60),
            intArrayOf(64, 64, 67, 64, 64, 67),
            intArrayOf(67, 64, 67, 69, 67, 64),
            intArrayOf(60, 62, 64, 65, 64, 62),
            intArrayOf(69, 67, 65, 64, 62, 60),
        )
    }
}

private class VoicePool(
    sampleRate: Int,
    size: Int,
) {
    private val voices = Array(size) { MusicBoxVoice(sampleRate) }
    private var cursor = 0

    fun pluck(midi: Int, velocity: Float) {
        if (midi <= 0) return
        voices[cursor].pluck(midi, velocity)
        cursor = (cursor + 1) % voices.size
    }

    fun next(): Float {
        var mix = 0f
        for (voice in voices) {
            mix += voice.next()
        }
        return mix
    }
}

private class MusicBoxVoice(private val sampleRate: Int) {
    private var phase = 0.0
    private var phase2 = 0.0
    private var phase3 = 0.0
    private var freq = 0.0
    private var age = 0
    private var velocity = 1f
    private var active = false
    private val twoPi = PI * 2.0
    private val attack = (sampleRate * 0.006).toInt().coerceAtLeast(1)

    fun pluck(midi: Int, vel: Float) {
        freq = 440.0 * 2.0.pow((midi - 69) / 12.0)
        velocity = vel
        age = 0
        active = true
        phase = 0.0
        phase2 = 0.0
        phase3 = 0.0
    }

    fun next(): Float {
        if (!active) return 0f
        val t = age.toFloat() / sampleRate
        val rise = (age.toFloat() / attack).coerceAtMost(1f)
        val decay = exp(-t * 1.45).toFloat()
        val env = rise * decay * velocity
        age++
        if (env < 0.0005f && age > attack) {
            active = false
            return 0f
        }
        val tone = sin(phase) * 0.72 + sin(phase2) * 0.2 + sin(phase3) * 0.08
        val step = freq * twoPi / sampleRate
        phase += step
        phase2 += step * 2.004
        phase3 += step * 3.01
        if (phase > twoPi) phase -= twoPi
        if (phase2 > twoPi) phase2 -= twoPi
        if (phase3 > twoPi) phase3 -= twoPi
        return (tone * 0.16 * env).toFloat()
    }
}
