package com.sleeperbaby.app.data

import com.sleeperbaby.app.R
import com.sleeperbaby.app.audio.AudioSource
import com.sleeperbaby.app.audio.BrownNoiseSource
import com.sleeperbaby.app.audio.FanNoiseSource
import com.sleeperbaby.app.audio.FrequencyPadSource
import com.sleeperbaby.app.audio.GenerativeLullabySource
import com.sleeperbaby.app.audio.HeartbeatSource
import com.sleeperbaby.app.audio.LullabyMelodySource
import com.sleeperbaby.app.audio.LullabyMixSource
import com.sleeperbaby.app.audio.MarimbaOstinatoSource
import com.sleeperbaby.app.audio.Melodies
import com.sleeperbaby.app.audio.OceanNoiseSource
import com.sleeperbaby.app.audio.PinkNoiseSource
import com.sleeperbaby.app.audio.PulseRhythmSource
import com.sleeperbaby.app.audio.RainNoiseSource
import com.sleeperbaby.app.audio.RockingRhythmSource
import com.sleeperbaby.app.audio.WarmToneSource
import com.sleeperbaby.app.audio.WhiteNoiseSource

enum class StationKind {
    SoftNoise,
    Lullaby,
    Frequency,
    CradleRhythm,
}

data class Channel(
    val id: String,
    val label: String,
    val hint: String,
    val iconRes: Int,
    val section: String? = null,
    val createSource: (sampleRate: Int) -> AudioSource,
)

data class Station(
    val kind: StationKind,
    val title: String,
    val subtitle: String,
    val leadingArt: Int,
    val trailingArt: Int? = null,
    val channels: List<Channel>,
)

object Catalog {
    val stations: List<Station> = listOf(
        Station(
            kind = StationKind.SoftNoise,
            title = "Ruidos suaves",
            subtitle = "Blanco, rosa, lluvia y mar en bucle",
            leadingArt = R.drawable.ic_station_noise,
            trailingArt = R.drawable.ill_koala,
            channels = listOf(
                Channel("noise_white", "Blanco", "Cubre ruidos de casa", R.drawable.ic_channel_white) { _ -> WhiteNoiseSource() },
                Channel("noise_pink", "Rosa", "Más cálido para dormir", R.drawable.ic_channel_pink) { _ -> PinkNoiseSource() },
                Channel("noise_brown", "Marrón", "Grave y envolvente", R.drawable.ic_channel_brown) { _ -> BrownNoiseSource() },
                Channel("noise_rain", "Lluvia", "Gotas suaves sin fin", R.drawable.ic_channel_rain) { sampleRate ->
                    RainNoiseSource(sampleRate)
                },
                Channel("noise_ocean", "Océano", "Olas lentas", R.drawable.ic_channel_ocean) { sampleRate ->
                    OceanNoiseSource(sampleRate)
                },
                Channel("noise_fan", "Ventilador", "Zumbido constante", R.drawable.ic_channel_fan) { sampleRate ->
                    FanNoiseSource(sampleRate)
                },
            ),
        ),
        Station(
            kind = StationKind.Lullaby,
            title = "Nanas infantiles",
            subtitle = "Clásicas de cuna y modernas suaves",
            leadingArt = R.drawable.ic_station_lullaby,
            trailingArt = R.drawable.ill_fox,
            channels = listOf(
                Channel("lullaby_mix", "Todas", "Mezcla de clásicas y modernas", R.drawable.ic_channel_mix, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate)
                },
                Channel("lullaby_mix_classic", "Mix clásicas", "Solo nanas de siempre", R.drawable.ic_channel_brahms, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate, Melodies.classic)
                },
                Channel("lullaby_mix_modern", "Mix modernas", "Solo nanas de ahora", R.drawable.ic_channel_mix, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate, Melodies.modern)
                },
                Channel("lullaby_brahms", "Brahms", "Canción de cuna clásica", R.drawable.ic_channel_brahms, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.brahms)
                },
                Channel("lullaby_twinkle", "Estrellita", "La de siempre, en bucle", R.drawable.ic_channel_twinkle, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.twinkle)
                },
                Channel("lullaby_frere", "Frère Jacques", "Ronda clásica", R.drawable.ic_channel_frere, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.frereJacques)
                },
                Channel("lullaby_arrorro", "Arrorró", "Nana tradicional", R.drawable.ic_channel_arrorro, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.arrorro)
                },
                Channel("lullaby_luna", "Nana de luna", "Para mirar la ventana", R.drawable.ic_channel_pad, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.luna)
                },
                Channel("lullaby_osito", "Osito", "Pasos suaves a la cama", R.drawable.ic_channel_heart, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.osito)
                },
                Channel("lullaby_duerme", "Duerme, cielo", "Nana despacio", R.drawable.ic_channel_twinkle, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.duerme)
                },
                Channel("lullaby_barquito", "Barquito", "Meciendo en el agua", R.drawable.ic_channel_ocean, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.barquito)
                },
                Channel("lullaby_nube", "Nube blanda", "Flotando un poquito", R.drawable.ic_channel_white, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.nube)
                },
                Channel("lullaby_cuna", "Cuna suave", "Vaivén de buenas noches", R.drawable.ic_channel_rocking, "Clásicas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.cuna)
                },
                Channel("lullaby_infinite", "Nana infinita", "Caja de música que no acaba", R.drawable.ic_channel_infinite, "Clásicas") { sampleRate ->
                    GenerativeLullabySource(sampleRate)
                },
                Channel("lullaby_farolito", "Farolito", "Nana de puerto, suave", R.drawable.ic_channel_ocean, "Modernas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.farolito)
                },
                Channel("lullaby_orilla", "Orilla", "Olas y cuna", R.drawable.ic_channel_ocean, "Modernas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.orilla)
                },
                Channel("lullaby_camino", "Camino", "Canción de carretera a la cama", R.drawable.ic_channel_heart, "Modernas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.camino)
                },
                Channel("lullaby_luces", "Luces", "Pueblo apagándose", R.drawable.ic_channel_twinkle, "Modernas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.luces)
                },
                Channel("lullaby_radio", "Nana de radio", "Pop suave de buenas noches", R.drawable.ic_channel_pad, "Modernas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.nanaRadio)
                },
            ),
        ),
        Station(
            kind = StationKind.Frequency,
            title = "Frecuencia 432",
            subtitle = "La que suele recordarse como 432 Hz",
            leadingArt = R.drawable.ic_station_frequency,
            trailingArt = R.drawable.ill_sloth,
            channels = listOf(
                Channel("freq_432", "432 Hz", "Tono puro y continuo", R.drawable.ic_channel_432) { sampleRate ->
                    WarmToneSource(sampleRate, 432.0, warmth = 0.12f)
                },
                Channel("freq_432_warm", "432 cálida", "Con octava grave", R.drawable.ic_channel_432_warm) { sampleRate ->
                    WarmToneSource(sampleRate, 432.0, warmth = 0.35f)
                },
                Channel("freq_528", "528 Hz", "Otra frecuencia suave", R.drawable.ic_channel_528) { sampleRate ->
                    WarmToneSource(sampleRate, 528.0, warmth = 0.18f)
                },
                Channel("freq_pad", "Drone de cuna", "Almohadilla lenta", R.drawable.ic_channel_pad) { sampleRate ->
                    FrequencyPadSource(sampleRate)
                },
            ),
        ),
        Station(
            kind = StationKind.CradleRhythm,
            title = "Ritmos de cuna",
            subtitle = "Patrones originales, sin bandas sonoras",
            leadingArt = R.drawable.ic_station_rhythm,
            trailingArt = R.drawable.ill_seahorses,
            channels = listOf(
                Channel("rhythm_heart", "Latido", "Pulso lento y sordo", R.drawable.ic_channel_heart) { sampleRate ->
                    HeartbeatSource(sampleRate)
                },
                Channel("rhythm_rock", "Mecedora", "Compás 6/8 de cuna", R.drawable.ic_channel_rocking) { sampleRate ->
                    RockingRhythmSource(sampleRate)
                },
                Channel("rhythm_marimba", "Marimba", "Ostinato pentatónico", R.drawable.ic_channel_marimba) { sampleRate ->
                    MarimbaOstinatoSource(sampleRate)
                },
                Channel("rhythm_pulse", "Pulso nocturno", "Acento suave continuo", R.drawable.ic_channel_pulse) { sampleRate ->
                    PulseRhythmSource(sampleRate)
                },
            ),
        ),
    )

    fun station(kind: StationKind): Station =
        stations.first { it.kind == kind }

    fun channel(id: String): Channel? =
        stations.asSequence()
            .flatMap { it.channels.asSequence() }
            .firstOrNull { it.id == id }

    fun stationFor(channelId: String): Station? =
        stations.firstOrNull { station ->
            station.channels.any { it.id == channelId }
        }
}

fun StationKind.title(): String = when (this) {
    StationKind.SoftNoise -> "Ruidos suaves"
    StationKind.Lullaby -> "Nanas infantiles"
    StationKind.Frequency -> "Frecuencia 432"
    StationKind.CradleRhythm -> "Ritmos de cuna"
}
