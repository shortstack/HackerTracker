package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.FirebaseEvent

class EventCustomEvent : CustomEvent {

    constructor(action: String, event: Event) : super(action) {
        putCustomAttribute("Title", event.title)
    }

    constructor(action: String, event: FirebaseEvent) : super(action) {
        putCustomAttribute("Title", event.title)
    }
}
