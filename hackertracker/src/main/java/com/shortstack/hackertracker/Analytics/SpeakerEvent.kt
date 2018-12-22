package com.shortstack.hackertracker.analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.models.Speaker

/**
 * Created by Chris on 06/08/18.
 */
class SpeakerEvent(action: String, speaker: FirebaseSpeaker): CustomEvent(action) {

    init {
        putCustomAttribute("Name", speaker.name)
    }
}