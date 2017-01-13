package com.shortstack.hackertracker.Analytics;

import com.crashlytics.android.answers.CustomEvent;
import com.shortstack.hackertracker.Model.Filter;

public class FilterEvent extends CustomEvent {

    public FilterEvent( Filter filter ) {
        super(AnalyticsController.Analytics.SCHEDULE_FILTERS.toString());
        String[] array = filter.getTypesArray();

        for (int i = 0; i < array.length; i++) {
            putCustomAttribute(array[i], String.valueOf(true));
        }
    }
}
