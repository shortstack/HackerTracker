package com.shortstack.hackertracker.utilities

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Storage(context: Context) {

    companion object {
        private const val USER_THEME = "user_theme"
        private const val USER_CONFERENCE = "user_conference"

        private const val NAV_DRAWER_ON_BACK = "nav_drawer_on_back"
        private const val FORCE_TIME_ZONE = "force_time_zone"

        private const val USER_ALLOW_PUSH = "user_allow_push_notifications"
        private const val USER_ANALYTICS = "user_analytics"
    }

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val allowPushNotification: Boolean
        get() = preferences.getBoolean(USER_ALLOW_PUSH, true)

    var allowAnalytics: Boolean
        get() = preferences.getBoolean(USER_ANALYTICS, true)
        set(value) {
            preferences.edit().putBoolean(USER_ANALYTICS, value).apply()
        }

    var navDrawerOnBack: Boolean
        get() = preferences.getBoolean(NAV_DRAWER_ON_BACK, false)
        set(value) {
            preferences.edit().putBoolean(NAV_DRAWER_ON_BACK, value).apply()
        }

    var forceTimeZone: Boolean
        get() = preferences.getBoolean(FORCE_TIME_ZONE, true)
        set(value) {
            preferences.edit().putBoolean(FORCE_TIME_ZONE, value).apply()
        }


    var preferredConference: Int
        get() = preferences.getInt(USER_CONFERENCE, -1)
        set(value) {
            preferences.edit().putInt(USER_CONFERENCE, value).apply()
        }

    fun setPreference(key: String, isChecked: Boolean) {
        when (key) {
            USER_ANALYTICS -> allowAnalytics = isChecked
            NAV_DRAWER_ON_BACK -> navDrawerOnBack = isChecked
            FORCE_TIME_ZONE -> forceTimeZone = isChecked
            else -> throw IllegalArgumentException("Unknown key: $key")
        }
    }
}
