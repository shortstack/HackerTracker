package com.shortstack.hackertracker.Analytics

import com.crashlytics.android.answers.CustomEvent

class SettingsEvent(event : AnalyticsController.Analytics, enabled : Boolean) : CustomEvent(event.toString()) {

    init {
        putCustomAttribute("Enabled", enabled.toString())
    }
}
