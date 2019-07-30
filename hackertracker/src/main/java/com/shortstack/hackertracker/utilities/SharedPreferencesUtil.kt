package com.shortstack.hackertracker.utilities

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPreferencesUtil(context: Context) {

    companion object {
        private const val USER_THEME = "user_theme"
        private const val USER_CONFERENCE = "user_conference"

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


    var preferredConference: Int
        get() = preferences.getInt(USER_CONFERENCE, -1)
        set(value) {
            preferences.edit().putInt(USER_CONFERENCE, value).apply()
        }

    fun setPreference(key: String, isChecked: Boolean) {
        when (key) {
            USER_ANALYTICS -> allowAnalytics = isChecked
            else -> throw IllegalArgumentException("Unknown key: $key")
        }
    }
}
