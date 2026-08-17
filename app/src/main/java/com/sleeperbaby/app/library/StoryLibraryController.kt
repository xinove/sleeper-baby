package com.sleeperbaby.app.library

import android.content.Context
import com.sleeperbaby.app.playback.SleepRadioController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object StoryLibraryController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var store: DailyStoryStore
    private var popupJob: Job? = null

    private val mutableOpenStory = MutableStateFlow<Story?>(null)
    private val mutableShowPopup = MutableStateFlow(false)
    private val mutableShowLibrary = MutableStateFlow(false)
    private val mutableLockedHint = MutableStateFlow<String?>(null)

    val openStory: StateFlow<Story?> = mutableOpenStory.asStateFlow()
    val showDailyPopup: StateFlow<Boolean> = mutableShowPopup.asStateFlow()
    val showLibrary: StateFlow<Boolean> = mutableShowLibrary.asStateFlow()
    val lockedHint: StateFlow<String?> = mutableLockedHint.asStateFlow()

    fun init(context: Context) {
        if (::store.isInitialized) return
        store = DailyStoryStore(context)
    }

    fun scheduleDailyPopup() {
        if (!::store.isInitialized) return
        popupJob?.cancel()
        popupJob = scope.launch {
            delay(1_000)
            if (mutableOpenStory.value != null || mutableShowLibrary.value) return@launch
            mutableShowPopup.value = true
        }
    }

    fun today(): Story {
        if (!::store.isInitialized) {
            return StoryCatalog.stories.first()
        }
        return store.today()
    }

    fun unlockedCount(): Int =
        if (::store.isInitialized) store.unlockedCount() else 1

    fun isUnlocked(story: Story): Boolean =
        if (::store.isInitialized) store.isUnlocked(story) else story.id == StoryCatalog.stories.first().id

    fun unlockedStories(): List<Story> = listOf(today())

    fun openTodayGift() {
        open(today())
    }

    fun openLibrary() {
        mutableShowLibrary.value = true
    }

    fun closeLibrary() {
        mutableShowLibrary.value = false
        mutableLockedHint.value = null
    }

    fun open(story: Story) {
        if (!isUnlocked(story)) {
            showLockedHint(story)
            return
        }
        mutableOpenStory.value = story
        popupJob?.cancel()
        mutableShowPopup.value = false
    }

    fun openAndListen(story: Story) {
        if (!isUnlocked(story)) {
            showLockedHint(story)
            return
        }
        open(story)
        SleepRadioController.playStory(story)
    }

    fun closeReader() {
        mutableOpenStory.value = null
    }

    fun dismissPopup() {
        popupJob?.cancel()
        mutableShowPopup.value = false
    }

    fun readToday() {
        open(today())
    }

    fun clearLockedHint() {
        mutableLockedHint.value = null
    }

    private fun showLockedHint(story: Story) {
        val days = if (::store.isInitialized) store.daysUntilUnlock(story) else 1
        mutableLockedHint.value = when {
            days <= 1 -> "Mañana toca otro cuento"
            else -> "Este cuento vuelve en $days días"
        }
    }
}
