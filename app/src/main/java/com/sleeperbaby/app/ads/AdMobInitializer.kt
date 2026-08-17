package com.sleeperbaby.app.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.sleeperbaby.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdMobInitializer {
    private val readyState = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = readyState.asStateFlow()

    fun initialize(context: Context) {
        if (readyState.value) return
        if (BuildConfig.DEBUG) {
            val testConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(testConfig)
        }
        MobileAds.initialize(context.applicationContext) {
            readyState.value = true
        }
    }
}
