package com.sleeperbaby.app.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.sleeperbaby.app.BuildConfig
import com.sleeperbaby.app.ads.AdMobIds
import com.sleeperbaby.app.ads.AdMobInitializer
import com.sleeperbaby.app.ui.theme.NightNavy

private const val TAG = "BannerAd"
private const val LOAD_TAG = "sleeper_ad_loaded"

val AdBannerHeight: Dp = 60.dp

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobIds.bannerAdUnitId,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val adsReady by AdMobInitializer.ready.collectAsStateWithLifecycle()
    var loadFailed by remember(adUnitId) { mutableStateOf(false) }
    var adViewRef by remember(adUnitId) { mutableStateOf<AdView?>(null) }

    LaunchedEffect(Unit) {
        AdMobInitializer.initialize(context)
    }

    val bannerModifier = modifier
        .fillMaxWidth()
        .height(AdBannerHeight)
        .clipToBounds()
        .background(if (loadFailed) NightNavy else NightNavy.copy(alpha = 0.92f))

    if (activity == null) {
        Log.w(TAG, "No se encontró Activity; se reserva el hueco del banner.")
        Spacer(modifier = bannerModifier)
        return
    }

    AndroidView(
        modifier = bannerModifier,
        factory = { viewContext ->
            AdView(viewContext).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        loadFailed = false
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Anuncio cargado ($adUnitId)")
                        }
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        loadFailed = true
                        Log.w(
                            TAG,
                            "Error al cargar anuncio: ${error.message} (code=${error.code})",
                        )
                    }
                }
            }
        },
        update = { adView ->
            adViewRef = adView
            if (adsReady && adView.tag != LOAD_TAG) {
                adView.tag = LOAD_TAG
                adView.loadAd(AdRequest.Builder().build())
            }
        },
        onRelease = { adView ->
            adViewRef = null
            adView.destroy()
        },
    )

    DisposableEffect(lifecycleOwner, adUnitId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> adViewRef?.resume()
                Lifecycle.Event.ON_PAUSE -> adViewRef?.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
