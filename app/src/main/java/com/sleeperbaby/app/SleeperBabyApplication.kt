package com.sleeperbaby.app

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import com.sleeperbaby.app.ads.AdMobInitializer
import com.sleeperbaby.app.library.StoryLibraryController
import com.sleeperbaby.app.library.StoryTtsController
import com.sleeperbaby.app.playback.SleepRadioController
import com.sleeperbaby.app.service.SleepRadioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SleeperBabyApplication : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        StoryLibraryController.init(this)
        StoryTtsController.init(this)
        AdMobInitializer.initialize(this)
        scope.launch {
            SleepRadioController.state
                .map { it.isActive }
                .distinctUntilChanged()
                .collect { active ->
                    if (active) {
                        val intent = Intent(this@SleeperBabyApplication, SleepRadioService::class.java)
                        ContextCompat.startForegroundService(this@SleeperBabyApplication, intent)
                    }
                }
        }
    }
}
