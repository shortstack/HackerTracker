package com.shortstack.hackertracker.Analytics;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Filter;
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
        SETTINGS_SYNC_AUTO("Settings - Sync Auto"),

        UBER("Uber"),

        FRAGMENT_HOME("View Home"),
        FRAGMENT_SCHEDULE("View Schedule"),
        FRAGMENT_MAP("View Map"),
        FRAGMENT_INFO("View Information"),
        FRAGMENT_COMPANIES("View Companies"),
        FRAGMENT_SETTINGS("View Settings"),
        FRAGMENT_CHANGE_CON("View Change Con"),

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

    public void tagSettingsEvent(Analytics event, boolean enabled) {
        logCustom(new SettingsEvent(event, enabled));
    }

    public void tagCustomEvent(Analytics event) {
        logCustom(new CustomEvent(event.toString()));
    }

    public void tagFiltersEvent(Filter filter) {
        logCustom(new FilterEvent(filter));
    }

    private void logCustom(CustomEvent event) {
        // Bypass to track if they're turning analytics off
        if( !App.application.getStorage().isTrackingAnalytics() && !event.toString().contains(Analytics.SETTINGS_ANALYTICS.toString()) ) {
            return;
        }

        try {
            Answers.getInstance().logCustom(event);
        } catch (IllegalStateException ex) {
            // Fabric is not initialized - debug build.
        }
    }
}
