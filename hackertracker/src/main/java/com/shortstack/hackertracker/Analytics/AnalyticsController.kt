package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import javax.inject.Inject


class AnalyticsController @Inject constructor(private val storage: SharedPreferencesUtil) {

    companion object {
        const val EVENT_VIEW = "Event - View"
        const val EVENT_OPEN_URL = "Event - Open URL"
        const val EVENT_BOOKMARK = "Event - Bookmark"
        const val EVENT_UNBOOKMARK = "Event - Unbookmark"
        const val EVENT_SHARE = "Event - Share"

        const val SETTINGS_ANALYTICS = "Settings - Analytics"
        const val SETTINGS_NOTIFICATIONS = "Settings - Notifications"
        const val SETTINGS_EXPIRED_EVENTS = "Settings - Expired Events"
    }

    fun onEventAction(action: String, event: Event) {
        logCustom(EventCustomEvent(action, event))
    }

    fun onSettingsChanged(setting: String, enabled: Boolean) {
        logCustom(SettingsEvent(setting, enabled))
    }

    private fun logCustom(event: CustomEvent) {
        if (BuildConfig.DEBUG) return

        // Bypass to track if they're turning analytics off
        if (!storage.allowAnalytics && !event.toString().contains(SETTINGS_ANALYTICS)) {
            return
        }

        Answers.getInstance().logCustom(event)
    }
}
