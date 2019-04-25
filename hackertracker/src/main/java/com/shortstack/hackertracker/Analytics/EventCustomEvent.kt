package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.models.FirebaseEvent

class EventCustomEvent(action: String, event: FirebaseEvent) : CustomEvent(action) {

    init {
        putCustomAttribute("Title", event.title)
    }
}
