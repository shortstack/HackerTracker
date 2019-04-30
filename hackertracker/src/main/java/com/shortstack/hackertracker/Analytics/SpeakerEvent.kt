package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.models.FirebaseSpeaker

class SpeakerEvent(action: String, speaker: FirebaseSpeaker): CustomEvent(action) {

    init {
        putCustomAttribute("Name", speaker.name)
    }
}