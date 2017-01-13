package com.shortstack.hackertracker.Analytics;

import com.crashlytics.android.answers.CustomEvent;

public class SettingsEvent extends CustomEvent {

    public SettingsEvent(AnalyticsController.Analytics event, boolean enabled ) {
        super(event.toString());
        putCustomAttribute("Enabled", String.valueOf(enabled));
    }
}
