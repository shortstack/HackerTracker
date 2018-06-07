package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.CustomEvent

class SettingsEvent(setting : String, enabled : Boolean) : CustomEvent(setting) {

    init {
        putCustomAttribute("Enabled", enabled.toString())
    }
}
