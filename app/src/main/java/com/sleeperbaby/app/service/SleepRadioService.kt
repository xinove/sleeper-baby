package com.sleeperbaby.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.sleeperbaby.app.MainActivity
import com.sleeperbaby.app.R
import com.sleeperbaby.app.data.Catalog
import com.sleeperbaby.app.library.StoryCatalog
import com.sleeperbaby.app.playback.PlaybackKind
import com.sleeperbaby.app.playback.PlayerState
import com.sleeperbaby.app.playback.SleepRadioController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SleepRadioService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var collectJob: Job? = null
    private var mediaSession: MediaSessionCompat? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private val audioManager by lazy { getSystemService(AudioManager::class.java) }

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            -> SleepRadioController.pause()
            AudioManager.AUDIOFOCUS_GAIN,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK,
            -> Unit
            else -> Unit
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        mediaSession = MediaSessionCompat(this, "SleeperBaby").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS,
            )
            setSessionActivity(openAppIntent())
            setMediaButtonReceiver(mediaButtonIntent())
            setCallback(sessionCallback)
            isActive = true
        }
        val initial = SleepRadioController.state.value
        updateMetadata(initial)
        updatePlaybackState(initial.isPlaying)
        startInForeground(buildNotification(initial))
        collectJob = scope.launch {
            SleepRadioController.state.collect { state ->
                mediaSession?.isActive = state.isActive
                updatePlaybackState(state.isPlaying)
                updateMetadata(state)
                if (state.isPlaying) requestAudioFocus() else abandonAudioFocus()
                if (state.isActive) {
                    startInForeground(buildNotification(state))
                } else {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == Intent.ACTION_MEDIA_BUTTON) {
            MediaButtonReceiver.handleIntent(mediaSession, intent)
            return START_STICKY
        }
        when (intent?.action) {
            ACTION_PLAY -> SleepRadioController.play()
            ACTION_PAUSE -> SleepRadioController.pause()
            ACTION_TOGGLE -> SleepRadioController.toggle()
            ACTION_NEXT -> SleepRadioController.cycleChannel(1)
            ACTION_PREV -> SleepRadioController.cycleChannel(-1)
            ACTION_STOP -> SleepRadioController.stop()
            else -> {
                val state = SleepRadioController.state.value
                startInForeground(buildNotification(state))
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        collectJob?.cancel()
        scope.cancel()
        abandonAudioFocus()
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private val sessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            SleepRadioController.play()
        }

        override fun onPause() {
            SleepRadioController.pause()
        }

        override fun onStop() {
            SleepRadioController.stop()
        }

        override fun onSkipToNext() {
            SleepRadioController.cycleChannel(1)
        }

        override fun onSkipToPrevious() {
            SleepRadioController.cycleChannel(-1)
        }
    }

    private fun startInForeground(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateMetadata(state: PlayerState) {
        val title: String
        val artist: String
        when (state.kind) {
            PlaybackKind.Radio -> {
                val channel = Catalog.channel(state.channelId)
                val station = Catalog.stationFor(state.channelId)
                title = channel?.label ?: "Radio de cuna"
                artist = station?.title ?: getString(R.string.app_name)
            }
            PlaybackKind.Story -> {
                val story = state.storyId?.let(StoryCatalog::story)
                title = story?.title ?: "Cuento"
                artist = "Biblioteca"
            }
        }
        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, getString(R.string.app_name))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, -1L)
                .build(),
        )
    }

    private fun updatePlaybackState(playing: Boolean) {
        val state = if (playing) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }
        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_STOP or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS,
                )
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, if (playing) 1f else 0f)
                .build(),
        )
    }

    private fun buildNotification(state: PlayerState): Notification {
        val title: String
        val text: String
        when (state.kind) {
            PlaybackKind.Radio -> {
                val channel = Catalog.channel(state.channelId)
                val station = Catalog.stationFor(state.channelId)
                title = station?.title ?: getString(R.string.app_name)
                text = channel?.label ?: "Radio de cuna"
            }
            PlaybackKind.Story -> {
                val story = state.storyId?.let(StoryCatalog::story)
                title = "Biblioteca"
                text = story?.title ?: "Cuento"
            }
        }
        val playing = state.isPlaying
        val playPauseIcon = if (playing) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        val playPauseLabel = if (playing) "Pausar" else "Reproducir"
        val playPauseAction = if (playing) ACTION_PAUSE else ACTION_PLAY
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setSubText(if (playing) "Reproduciendo" else "En pausa")
            .setContentIntent(openAppIntent())
            .setDeleteIntent(serviceIntent(ACTION_STOP, 20))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setOngoing(playing)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(serviceIntent(ACTION_STOP, 21)),
            )
            .addAction(android.R.drawable.ic_media_previous, "Anterior", serviceIntent(ACTION_PREV, 11))
            .addAction(playPauseIcon, playPauseLabel, serviceIntent(playPauseAction, 12))
            .addAction(android.R.drawable.ic_media_next, "Siguiente", serviceIntent(ACTION_NEXT, 13))
            .addAction(R.drawable.ic_stop, "Detener", serviceIntent(ACTION_STOP, 14))
            .build()
    }

    private fun openAppIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun mediaButtonIntent(): PendingIntent {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON).setClass(this, MediaButtonReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
        return PendingIntent.getBroadcast(this, 0, intent, flags)
    }

    private fun serviceIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, SleepRadioService::class.java).setAction(action)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, requestCode, intent, flags)
        } else {
            PendingIntent.getService(this, requestCode, intent, flags)
        }
    }

    private fun requestAudioFocus() {
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setOnAudioFocusChangeListener(focusChangeListener)
            .setAcceptsDelayedFocusGain(false)
            .build()
        audioFocusRequest = request
        audioManager.requestAudioFocus(request)
    }

    private fun abandonAudioFocus() {
        val request = audioFocusRequest ?: return
        audioManager.abandonAudioFocusRequest(request)
        audioFocusRequest = null
    }

    private fun createChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.notification_channel_desc)
            setSound(null, null)
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "sleeper_radio"
        const val NOTIFICATION_ID = 42
        const val ACTION_PLAY = "com.sleeperbaby.app.PLAY"
        const val ACTION_PAUSE = "com.sleeperbaby.app.PAUSE"
        const val ACTION_TOGGLE = "com.sleeperbaby.app.TOGGLE"
        const val ACTION_STOP = "com.sleeperbaby.app.STOP"
        const val ACTION_NEXT = "com.sleeperbaby.app.NEXT"
        const val ACTION_PREV = "com.sleeperbaby.app.PREV"
    }
}
