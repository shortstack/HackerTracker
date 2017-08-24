package com.shortstack.hackertracker.Analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.Model.Item

class ItemEvent(event : AnalyticsController.Analytics, item : Item) : CustomEvent(event.toString()) {

    init {
        putCustomAttribute("Id", item.index)
        putCustomAttribute("Title", item.title)
    }
}
