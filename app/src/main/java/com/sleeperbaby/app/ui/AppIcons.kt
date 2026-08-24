package com.sleeperbaby.app.ui

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.sleeperbaby.app.R
import com.sleeperbaby.app.data.StationKind
import com.sleeperbaby.app.playback.NightLightMode

/**
 * Iconos de la interfaz. Para sustituir uno, pon un PNG con el mismo nombre
 * en drawable-xxhdpi/ (si hay un XML en drawable/ con ese nombre, bórralo).
 *
 * Izquierda de cada tarjeta:
 *   ic_station_noise.png     koala en una nube
 *   ic_station_lullaby.png   nota musical
 *   ic_station_frequency.png círculos concéntricos
 *   ic_station_rhythm.png    nube / ritmo
 *   ic_library.png           libro abierto
 *   ic_gift.png              caja de regalo (popup diario)
 *   ic_lock.png              candado
 *   ic_play.png              triángulo de play
 *   ill_shush.png            cara de bebé (luz nocturna)
 *
 * Ilustraciones a la derecha (ya están): ill_koala, ill_fox, ill_sloth, ill_seahorses.
 */
object AppIcons {
    fun station(kind: StationKind): Int = when (kind) {
        StationKind.SoftNoise -> R.drawable.ic_station_noise
        StationKind.Lullaby -> R.drawable.ic_station_lullaby
        StationKind.CradleRhythm -> R.drawable.ic_station_rhythm
    }

    fun nightLight(mode: NightLightMode): Int = when (mode) {
        NightLightMode.Off -> R.drawable.ic_night_light
        NightLightMode.WarmLamp -> R.drawable.ic_night_lamp
        NightLightMode.Animals -> R.drawable.ic_night_animals
        NightLightMode.Stars -> R.drawable.ic_night_stars
    }

    val nightLight = R.drawable.ic_night_light
    val play = R.drawable.ic_play
    val playBaby = R.drawable.ic_play_baby
    val shush = R.drawable.ill_shush
    val pause = R.drawable.ic_pause
    val stop = R.drawable.ic_stop
    val previous = R.drawable.ic_prev
    val next = R.drawable.ic_next
    val close = R.drawable.ic_close
    val volumeStar = R.drawable.ic_night_stars
    val chevronDown = R.drawable.ic_chevron_down
    val library = R.drawable.ic_library
    val listen = R.drawable.ic_listen
    val lock = R.drawable.ic_lock
    val gift = R.drawable.ic_gift
}

@Composable
fun AppIcon(
    @DrawableRes resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    Icon(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}
