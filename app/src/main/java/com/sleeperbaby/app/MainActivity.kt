package com.sleeperbaby.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sleeperbaby.app.playback.SleepRadioController
import com.sleeperbaby.app.ui.RadioScreen
import com.sleeperbaby.app.ui.theme.NightNavy
import com.sleeperbaby.app.ui.theme.SleeperBabyTheme

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* El servicio sigue sonando aunque se deniegue la notificación. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            SleeperBabyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = NightNavy) {
                    RadioScreen(
                        onPlayRequest = { SleepRadioController.toggle() },
                    )
                }
            }
        }
    }
}
