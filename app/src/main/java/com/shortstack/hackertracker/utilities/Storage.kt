package com.shortstack.hackertracker.utilities

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.ui.themes.ThemesManager
import java.util.*

class Storage(context: Context, private val gson: Gson) {

    companion object {
        private const val USER_THEME = "user_theme"
        private const val USER_CONFERENCE = "user_conference"

        const val EASTER_EGGS_ENABLED_KEY = "easter_eggs_enabled"
        const val SAFE_MODE_ENABLED = "safe_mode_enabled"
        const val DEVELOPER_THEME_UNLOCKED = "developer_theme_unlocked"
        const val NAV_DRAWER_ON_BACK_KEY = "nav_drawer_on_back"
        const val FORCE_TIME_ZONE_KEY = "force_time_zone"
        const val USER_ANALYTICS_KEY = "user_analytics"
    }

    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    var allowAnalytics: Boolean
        get() = preferences.getBoolean(USER_ANALYTICS_KEY, true)
        set(value) {
            preferences.edit().putBoolean(USER_ANALYTICS_KEY, value).apply()
        }

    var navDrawerOnBack: Boolean
        get() = preferences.getBoolean(NAV_DRAWER_ON_BACK_KEY, false)
        set(value) {
            preferences.edit().putBoolean(NAV_DRAWER_ON_BACK_KEY, value).apply()
        }

    var forceTimeZone: Boolean
        get() = preferences.getBoolean(FORCE_TIME_ZONE_KEY, true)
        set(value) {
            preferences.edit().putBoolean(FORCE_TIME_ZONE_KEY, value).apply()
        }

    var easterEggs: Boolean
        get() = preferences.getBoolean(EASTER_EGGS_ENABLED_KEY, false)
        set(value) {
            preferences.edit().putBoolean(EASTER_EGGS_ENABLED_KEY, value).apply()
        }


    var preferredConference: Int
        get() = preferences.getInt(USER_CONFERENCE, -1)
        set(value) {
            preferences.edit().putInt(USER_CONFERENCE, value).apply()
        }

    var theme: ThemesManager.Theme?
        get() = gson.fromJson(
            preferences.getString(USER_THEME, ""),
            ThemesManager.Theme::class.java
        )
        set(value) {
            preferences.edit().putString(USER_THEME, gson.toJson(value)).apply()
        }

    fun setPreference(key: String, isChecked: Boolean) {
        when (key) {
            USER_ANALYTICS_KEY -> allowAnalytics = isChecked
            NAV_DRAWER_ON_BACK_KEY -> navDrawerOnBack = isChecked
            FORCE_TIME_ZONE_KEY -> forceTimeZone = isChecked
            EASTER_EGGS_ENABLED_KEY -> easterEggs = isChecked
            else -> preferences.edit().putBoolean(key, isChecked).apply()
        }
    }

    fun getPreference(key: String, defaultValue: Boolean): Boolean {
        return when (key) {
            USER_ANALYTICS_KEY -> allowAnalytics
            NAV_DRAWER_ON_BACK_KEY -> navDrawerOnBack
            FORCE_TIME_ZONE_KEY -> forceTimeZone
            EASTER_EGGS_ENABLED_KEY -> easterEggs
            else -> preferences.getBoolean(key, defaultValue)
        }
    }


    enum class CorruptionLevel {
        NONE,
        MINOR,
        MEDIUM,
        MAJOR
    }

    val corruption: CorruptionLevel
        get() {
            if (!getPreference(
                    EASTER_EGGS_ENABLED_KEY,
                    false
                ) || theme != ThemesManager.Theme.SafeMode
            ) {
                return CorruptionLevel.NONE
            }

            val calendar = Calendar.getInstance()
            calendar.time = MyClock().now()

            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            if (dayOfYear < 219)
                return CorruptionLevel.NONE

            return when (dayOfYear) {
                219 -> CorruptionLevel.MINOR
                220 -> CorruptionLevel.MEDIUM
                221 -> CorruptionLevel.MAJOR
                else -> CorruptionLevel.MEDIUM
            }
        }
}
