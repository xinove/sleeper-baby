package com.sleeperbaby.app.library

import android.content.Context
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DailyStoryStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun todayTale(): Story = StoryCatalog.taleOfTheDay(daysSinceFirstOpen())

    fun todayAdventure(): Story = StoryCatalog.adventureOfTheDay(daysSinceFirstOpen())

    fun todayStories(): List<Story> = listOf(todayTale(), todayAdventure())

    fun daysSinceFirstOpen(): Int {
        ensureFirstOpen()
        val first = LocalDate.parse(prefs.getString(KEY_FIRST_OPEN, LocalDate.now().toString())!!)
        return ChronoUnit.DAYS.between(first, LocalDate.now()).toInt().coerceAtLeast(0)
    }

    fun unlockedCount(): Int = 2

    fun isUnlocked(story: Story): Boolean = todayStories().any { it.id == story.id }

    fun daysUntilUnlock(story: Story): Int {
        val pool = if (story.isAdventure()) StoryCatalog.adventures else StoryCatalog.tales
        val index = pool.indexOfFirst { it.id == story.id }
        if (index < 0) return 0
        val today = daysSinceFirstOpen().mod(pool.size)
        return (index - today).mod(pool.size)
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
