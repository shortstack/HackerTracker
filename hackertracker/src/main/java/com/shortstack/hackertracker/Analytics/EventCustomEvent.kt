package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.models.Event

class EventCustomEvent(action: String, event: Event) : CustomEvent(action) {

    init {
        putCustomAttribute("Id", event.id)
        putCustomAttribute("Title", event.title)
    }
}
