package com.sleeperbaby.app.library

import android.content.Context
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DailyStoryStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun today(): Story = StoryCatalog.stories[todayIndex()]

    fun todayIndex(): Int {
        ensureFirstOpen()
        val first = LocalDate.parse(prefs.getString(KEY_FIRST_OPEN, LocalDate.now().toString())!!)
        val days = ChronoUnit.DAYS.between(first, LocalDate.now()).toInt().coerceAtLeast(0)
        return days.mod(StoryCatalog.stories.size)
    }

    fun unlockedCount(): Int = 1

    fun isUnlocked(story: Story): Boolean = story.id == today().id

    fun daysUntilUnlock(story: Story): Int {
        val index = StoryCatalog.stories.indexOfFirst { it.id == story.id }
        if (index < 0) return 0
        return (index - todayIndex()).mod(StoryCatalog.stories.size)
    }

    fun shouldShowPopup(): Boolean {
        val today = LocalDate.now().toString()
        return prefs.getString(KEY_POPUP_DATE, null) != today
    }

    fun markPopupShown() {
        prefs.edit().putString(KEY_POPUP_DATE, LocalDate.now().toString()).apply()
    }

    private fun ensureFirstOpen() {
        if (prefs.getString(KEY_FIRST_OPEN, null) == null) {
            prefs.edit().putString(KEY_FIRST_OPEN, LocalDate.now().toString()).apply()
        }
    }

    private companion object {
        const val PREFS = "sleeper_library"
        const val KEY_POPUP_DATE = "popup_date"
        const val KEY_FIRST_OPEN = "first_open"
    }
}
