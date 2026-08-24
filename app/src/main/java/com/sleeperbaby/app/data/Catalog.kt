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

enum class LullabyShelf(
    val title: String,
    val mixChannelId: String,
    val section: String?,
) {
    All("Todas", "lullaby_mix", null),
    Classic("Clásicas", "lullaby_mix_classic", "Clásicas"),
    Modern("Modernas", "lullaby_mix_modern", "Modernas"),
    Rock("Rock", "lullaby_mix_rock", "Rock"),
    Pop("Pop", "lullaby_mix_pop", "Pop"),
    Christmas("Navideñas", "lullaby_mix_xmas", "Navideñas"),
}

fun Channel.lullabyShelf(): LullabyShelf {
    if (id == "lullaby_infinite") return LullabyShelf.All
    LullabyShelf.entries.firstOrNull { it.mixChannelId == id }?.let { return it }
    return LullabyShelf.entries.firstOrNull { shelf ->
        shelf.section != null && shelf.section == section
    } ?: LullabyShelf.All
}

fun Station.lullabyMix(shelf: LullabyShelf): Channel? =
    channels.firstOrNull { it.id == shelf.mixChannelId }

fun Station.lullabyInfinite(): Channel? =
    channels.firstOrNull { it.id == "lullaby_infinite" }

fun Station.lullabySongs(shelf: LullabyShelf): List<Channel> {
    val section = shelf.section ?: return emptyList()
    return channels.filter { channel ->
        channel.section == section &&
            channel.id != shelf.mixChannelId &&
            channel.id != "lullaby_infinite"
    }
}

object Catalog {
    private val allStations: List<Station> = listOf(
        Station(
            kind = StationKind.SoftNoise,
            title = "Ruidos suaves",
            subtitle = "Blanco, lluvia, mar y tonos 432",
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
                Channel("freq_432", "Tono (432)", "Tono puro y continuo", R.drawable.ic_channel_432) { sampleRate ->
                    WarmToneSource(sampleRate, 432.0, warmth = 0.12f)
                },
                Channel("freq_432_warm", "Cálida (432)", "Con octava grave", R.drawable.ic_channel_432_warm) { sampleRate ->
                    WarmToneSource(sampleRate, 432.0, warmth = 0.35f)
                },
                Channel("freq_528", "Tono (528)", "Otra frecuencia suave", R.drawable.ic_channel_528) { sampleRate ->
                    WarmToneSource(sampleRate, 528.0, warmth = 0.18f)
                },
                Channel("freq_pad", "Drone (432)", "Almohadilla lenta", R.drawable.ic_channel_pad) { sampleRate ->
                    FrequencyPadSource(sampleRate)
                },
            ),
        ),
        Station(
            kind = StationKind.Lullaby,
            title = "Nanas infantiles",
            subtitle = "Clásicas, modernas, rock, pop y navideñas",
            leadingArt = R.drawable.ic_station_lullaby,
            trailingArt = R.drawable.ill_fox,
            channels = listOf(
                Channel("lullaby_mix", "Mezcla de todas", "Mezcla de todos los estilos", R.drawable.ic_channel_mix, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate)
                },
                Channel("lullaby_mix_classic", "Mix clásicas", "Solo nanas de siempre", R.drawable.ic_channel_brahms, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate, Melodies.classic)
                },
                Channel("lullaby_mix_modern", "Mix modernas", "Solo nanas de ahora", R.drawable.ic_channel_mix, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate, Melodies.modern)
                },
                Channel("lullaby_mix_rock", "Mix rock", "Riffs suaves de cuna", R.drawable.ic_channel_pulse, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate, Melodies.rock)
                },
                Channel("lullaby_mix_pop", "Mix pop", "Estribillos para dormir", R.drawable.ic_channel_mix, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate, Melodies.pop)
                },
                Channel("lullaby_mix_xmas", "Mix navideñas", "Noche en paz y copos", R.drawable.ic_channel_twinkle, "Mezclas") { sampleRate ->
                    LullabyMixSource(sampleRate, Melodies.christmas)
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
                Channel("lullaby_riff", "Riff de cuna", "Rock bajito, sin gritos", R.drawable.ic_channel_pulse, "Rock") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.riffCuna)
                },
                Channel("lullaby_motor", "Motor suave", "Carretera lenta a la cama", R.drawable.ic_channel_heart, "Rock") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.motorSuave)
                },
                Channel("lullaby_cielo", "Cielo eléctrico", "Rock de estrellas", R.drawable.ic_channel_infinite, "Rock") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.cieloElectrico)
                },
                Channel("lullaby_puerto", "Puerto de noche", "Rock de muelle, para arrullar", R.drawable.ic_channel_ocean, "Rock") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.puertoNoche)
                },
                Channel("lullaby_guardia", "Guardia de noche", "Balada de puerto, ritmo de cuna", R.drawable.ic_channel_ocean, "Rock") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.guardiaNoche)
                },
                Channel("lullaby_estribillo", "Estribillo", "La parte que se pega, suave", R.drawable.ic_channel_mix, "Pop") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.estribillo)
                },
                Channel("lullaby_brillo", "Brillo", "Pop de buenas noches", R.drawable.ic_channel_twinkle, "Pop") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.brillo)
                },
                Channel("lullaby_nanapop", "Nana pop", "Verso y estribillo de cuna", R.drawable.ic_channel_pad, "Pop") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.nanaPop)
                },
                Channel("lullaby_nochepaz", "Noche en paz", "Villancico clásico, muy despacio", R.drawable.ic_channel_twinkle, "Navideñas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.nochePaz)
                },
                Channel("lullaby_copos", "Copos", "Nieve cayendo en la cuna", R.drawable.ic_channel_white, "Navideñas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.copos)
                },
                Channel("lullaby_campanita", "Campanita", "Campanas lejanas", R.drawable.ic_channel_arrorro, "Navideñas") { sampleRate ->
                    LullabyMelodySource(sampleRate, Melodies.campanita)
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

    val stations: List<Station> = listOf(
        allStations.first { it.kind == StationKind.Lullaby },
        allStations.first { it.kind == StationKind.SoftNoise },
        allStations.first { it.kind == StationKind.CradleRhythm },
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
    StationKind.CradleRhythm -> "Ritmos de cuna"
}
