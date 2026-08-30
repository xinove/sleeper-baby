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
    /** Temporal: quitar o poner a false antes de publicar. */
    private const val UNLOCK_ALL_STORIES = true

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

    fun todayTale(): Story {
        if (!::store.isInitialized) {
            return StoryCatalog.tales.first()
        }
        return store.todayTale()
    }

    fun todayAdventure(): Story {
        if (!::store.isInitialized) {
            return StoryCatalog.adventures.first()
        }
        return store.todayAdventure()
    }

    fun todayStories(): List<Story> = listOf(todayTale(), todayAdventure())

    fun unlockedCount(): Int =
        if (UNLOCK_ALL_STORIES) {
            StoryCatalog.stories.size
        } else if (::store.isInitialized) {
            store.unlockedCount()
        } else {
            2
        }

    fun isUnlocked(story: Story): Boolean {
        if (UNLOCK_ALL_STORIES) return true
        return if (::store.isInitialized) {
            store.isUnlocked(story)
        } else {
            story.id == StoryCatalog.tales.first().id ||
                story.id == StoryCatalog.adventures.first().id
        }
    }

    fun unlockedStories(): List<Story> =
        if (UNLOCK_ALL_STORIES) StoryCatalog.stories else todayStories()

    fun openTodayGift() {
        mutableShowPopup.value = true
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

    fun openAndListen(story: Story, nodeId: String? = null) {
        if (!isUnlocked(story)) {
            showLockedHint(story)
            return
        }
        open(story)
        SleepRadioController.playStory(story, nodeId)
    }

    fun closeReader() {
        mutableOpenStory.value = null
    }

    fun dismissPopup() {
        popupJob?.cancel()
        mutableShowPopup.value = false
    }

    fun clearLockedHint() {
        mutableLockedHint.value = null
    }

    private fun showLockedHint(story: Story) {
        val days = if (::store.isInitialized) store.daysUntilUnlock(story) else 1
        mutableLockedHint.value = when {
            days <= 1 && story.isAdventure() -> "Mañana toca otra aventura"
            days <= 1 -> "Mañana toca otro cuento"
            story.isAdventure() -> "Esta aventura vuelve en $days días"
            else -> "Este cuento vuelve en $days días"
        }
    }
}
