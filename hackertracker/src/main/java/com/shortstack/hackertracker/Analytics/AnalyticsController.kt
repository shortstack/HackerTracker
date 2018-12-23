package com.shortstack.hackertracker.analytics

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.utils.SharedPreferencesUtil


object AnalyticsController {

    private val storage = SharedPreferencesUtil(App.application)

    const val EVENT_VIEW = "Event - View"
    const val EVENT_OPEN_URL = "Event - Open URL"
    const val EVENT_BOOKMARK = "Event - Bookmark"
    const val EVENT_UNBOOKMARK = "Event - Unbookmark"
    const val EVENT_SHARE = "Event - Share"

    const val SPEAKER_VIEW = "Speaker - View"
    const val SPEAKER_TWITTER = "Speaker - Open URL"

    const val FAQ_VIEW = "FAQ - View"

    const val MAP_VIEW = "Map - View"

    const val SCHEDULE_REFRESH = "Schedule - Pull to Refresh"

    const val SETTINGS_ANALYTICS = "Settings - Analytics"
    const val SETTINGS_NOTIFICATIONS = "Settings - Notifications"
    const val SETTINGS_EXPIRED_EVENTS = "Settings - Expired Events"


    fun onEventAction(action:String, event: FirebaseEvent) {
        logCustom(EventCustomEvent(action, event))
    }

    fun onSpeakerEvent(action: String, speaker: FirebaseSpeaker) {
        logCustom(SpeakerEvent(action, speaker))
    }

    fun onSettingsChanged(setting: String, enabled: Boolean) {
        logCustom(SettingsEvent(setting, enabled))
    }

    fun logCustom(event: CustomEvent) {
        if (BuildConfig.DEBUG) return

        // Bypass to track if they're turning analytics off
        if (!storage.allowAnalytics && !event.toString().contains(SETTINGS_ANALYTICS)) {
            return
        }

        Answers.getInstance().logCustom(event)
    }

    fun log(message: String) {
        if (BuildConfig.DEBUG) return

        Crashlytics.getInstance().core.log(message)
    }

}
