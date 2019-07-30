package com.shortstack.hackertracker.utilities

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Speaker

class Analytics(private val storage: Storage) {

    companion object {
        const val EVENT_VIEW = "Event - View"
        const val EVENT_OPEN_URL = "Event - Open URL"
        const val EVENT_BOOKMARK = "Event - Bookmark"
        const val EVENT_UNBOOKMARK = "Event - Unbookmark"
        const val EVENT_SHARE = "Event - Share"

        const val SPEAKER_VIEW = "Speaker - View"
        const val SPEAKER_TWITTER = "Speaker - Open URL"

        const val FAQ_VIEW = "FAQ - View"

        const val MAP_VIEW = "Map - View"

        const val SETTINGS_ANALYTICS = "Settings - Analytics"
        const val SETTINGS_NOTIFICATIONS = "Settings - Notifications"
        const val SETTINGS_EXPIRED_EVENTS = "Settings - Expired Events"
    }

    fun onEventAction(action: String, event: Event) {
        val event = CustomEvent(action).apply {
            putCustomAttribute("Title", event.title)
        }

        logCustom(event)
    }

    fun onSpeakerEvent(action: String, speaker: Speaker) {
        val event = CustomEvent(action).apply {
            putCustomAttribute("Name", speaker.name)
        }

        logCustom(event)
    }

    fun onSettingsChanged(setting: String, enabled: Boolean) {
        val event = CustomEvent(setting).apply {
            putCustomAttribute("Enabled", enabled.toString())
        }

        logCustom(event)
    }

    fun logCustom(event: CustomEvent) {
        // Bypass to track if they're turning analytics off
        if (!storage.allowAnalytics && !event.toString().contains(SETTINGS_ANALYTICS)) {
            return
        }

        Answers.getInstance().logCustom(event)
    }

    fun log(message: String) {
        Crashlytics.getInstance().core.log(message)
    }

}
