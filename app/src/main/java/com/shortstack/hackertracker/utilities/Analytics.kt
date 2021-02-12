package com.shortstack.hackertracker.utilities


import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Speaker

class Analytics(context: Context, private val storage: Storage) {

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

    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

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

        analytics.logEvent(event.event, event.extras)
    }

    fun log(message: String) {
        //Crashlytics.getInstance().core.log(message)
    }


    @Deprecated("Replace with Firebase default solution.")
    class CustomEvent(val event: String, val extras: Bundle = Bundle()) {

        fun putCustomAttribute(key: String, value: String) {
            extras.putString(key, value)
        }
    }

}
