package com.shortstack.hackertracker.Analytics;

import com.crashlytics.android.answers.CustomEvent;
import com.shortstack.hackertracker.Model.Item;

public class ItemEvent extends CustomEvent {

    public ItemEvent(AnalyticsController.Analytics event, Item item) {
        super(event.toString());
        putCustomAttribute("Id", item.getId());
        putCustomAttribute("Title", item.getTitle());
    }
}
