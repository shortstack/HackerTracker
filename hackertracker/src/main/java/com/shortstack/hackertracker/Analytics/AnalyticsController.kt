package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.models.Filter
import com.shortstack.hackertracker.models.Item
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import javax.inject.Inject


class AnalyticsController @Inject constructor(private val storage: SharedPreferencesUtil) {

    enum class Analytics(private val tag: String) {

        EVENT_VIEW("Event - View"),
        EVENT_BOOKMARK("Event - Bookmark"),
        EVENT_UNBOOKMARK("Event - Unbookmark"),
        EVENT_SHARE("Event - Share"),
        EVENT_LINK("Event - Link"),

        SETTINGS_ANALYTICS("Settings - Analytics"),
        SETTINGS_NOTIFICATIONS("Settings - Notifications"),
        SETTINGS_EXPIRED_EVENT("Settings - Expired Events"),
        SETTINGS_MILITARY_TIME("Settings - 24 Time Mode"),
        SETTINGS_SYNC_AUTO("Settings - Sync Auto"),

        UBER("Uber"),

        FRAGMENT_HOME("View Home"),
        FRAGMENT_SCHEDULE("View Schedule"),
        FRAGMENT_MAP("View Map"),
        FRAGMENT_INFO("View Information"),
        FRAGMENT_COMPANIES("View Companies"),
        FRAGMENT_SETTINGS("View Settings"),
        FRAGMENT_CHANGE_CON("View Change Con"),

        SCHEDULE_FILTERS("Schedule - Filters");

        override fun toString(): String {
            return tag
        }
    }


    fun tagItemEvent(event: Analytics, item: Item) {
        logCustom(ItemEvent(event, item))
    }

    fun tagSettingsEvent(event: Analytics, enabled: Boolean) {
        logCustom(SettingsEvent(event, enabled))
    }

    fun tagCustomEvent(event: Analytics) {
        logCustom(CustomEvent(event.toString()))
    }

    fun tagFiltersEvent(filter: Filter) {
        logCustom(FilterEvent(filter))
    }

    private fun logCustom(event: CustomEvent) {
        // Bypass to track if they're turning analytics off
        if (!storage.isTrackingAnalytics && !event.toString().contains(Analytics.SETTINGS_ANALYTICS.toString())) {
            return
        }

        try {
            Answers.getInstance().logCustom(event)
        } catch (ex: IllegalStateException) {
            // Fabric is not initialized - debug build.
        }

    }
}
