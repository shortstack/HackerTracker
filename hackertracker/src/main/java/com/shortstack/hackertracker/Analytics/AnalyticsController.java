package com.shortstack.hackertracker.Analytics;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.shortstack.hackertracker.Model.Item;

public class AnalyticsController {

    public enum Analytics {

        EVENT_VIEW("Event - View"),
        EVENT_BOOKMARK("Event - Bookmark"),
        EVENT_UNBOOKMARK("Event - Unbookmark"),
        EVENT_SHARE("Event - Share"),
        EVENT_LINK("Event - Link"),

        SETTINGS_ANALYTICS("Settings - Analytics"),
        SETTINGS_NOTIFICATIONS("Settings - Notifications"),
        SETTINGS_EXPIRED_EVENT("Settings - Expired Events"),
        SETTINGS_MILITARY_TIME("Settings - 24 Time Mode"),

        UBER("Uber"),

        VIEW_MAP("View Map"),
        VIEW_FAQ("View FAQ"),
        VIEW_VENDORS("View Vendors"),

        SCHEDULE_FILTERS("Schedule - Filters");

        private final String name;

        Analytics(String key) {
            this.name = key;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    public AnalyticsController() {

    }


    public void tagItemEvent(Analytics event, Item item) {
        logCustom(new ItemEvent(event, item));
    }

    public void tagSettingsEvent(Analytics event, boolean enabled ) {
        logCustom(new SettingsEvent(event, enabled));
    }

    public void tagCustomEvent(Analytics event) {
        logCustom(new CustomEvent(event.toString()));
    }

    private void logCustom( CustomEvent event ) {
        try {
            Answers.getInstance().logCustom(event);
        } catch (IllegalStateException ex) {
            // Fabric is not initialized - debug build.
        }
    }
}
