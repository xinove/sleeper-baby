package com.sleeperbaby.app.ads

import com.sleeperbaby.app.BuildConfig

/**
 * IDs de AdMob para Sleeper Baby.
 *
 * En depuración se usa el bloque de prueba de Google.
 * En release, el App ID y el banner reales de esta app.
 */
object AdMobIds {
    const val APP_ID_TEST = "ca-app-pub-3940256099942544~3347511713"
    const val BANNER_TEST = "ca-app-pub-3940256099942544/6300978111"

    const val APP_ID_PRODUCTION = "ca-app-pub-1500150166852996~1158458948"
    const val BANNER_PRODUCTION = "ca-app-pub-1500150166852996/9943721000"

    val bannerAdUnitId: String
        get() = if (BuildConfig.DEBUG) BANNER_TEST else BANNER_PRODUCTION
}
