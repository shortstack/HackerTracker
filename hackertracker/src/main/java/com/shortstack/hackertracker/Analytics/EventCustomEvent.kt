package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Item

class EventCustomEvent(title : AnalyticsController.Analytics, event : Event) : CustomEvent(title.toString()) {

    init {
        putCustomAttribute("Id", event.index)
        putCustomAttribute("Title", event.title)
    }
}
