package com.shortstack.hackertracker.Utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.Filter;

import java.util.HashSet;

public class SharedPreferencesUtil {


    private static final int DEFAULT_DAYS_TO_LOAD = 0;

    public enum Key {

        USER_FILTER("user_filter"),
        USER_ALLOW_PUSH("user_allow_push_notifications"),
        USER_MILITARY_TIME("user_use_military_time"),
        USER_EXPIRED_EVENTS("user_show_expired_events"),
        USER_ANALYTICS("user_analytics"),

        APP_SYNC_SCHEDULED("app_sync_scheduled"),
        APP_LAST_SYNC_VERSION("app_last_sync_version"),
        APP_UPDATED_EVENTS("app_updated_events"),
        APP_LAST_REFRESH("app_last_refresh"),
        APP_LAST_UPDATED("app_last_updated"),
        APP_VIEW_PAGER_POSITION("app_view_pager_position"),
        SCHEDULE_DAY_VIEW("app_day_view");


        private final String name;

        Key(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private SharedPreferences mPreferences;

    public SharedPreferencesUtil() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(App.Companion.getApplication().getAppContext());

        // Reset to only show the current day.
        setScheduleDay(DEFAULT_DAYS_TO_LOAD);
    }

    private SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }

    public void setLastUpdated(String date) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(Key.APP_LAST_UPDATED.toString(), date);
        editor.apply();
    }

    public String getLastUpdated() {
        return mPreferences.getString(Key.APP_LAST_UPDATED.toString(), null);
    }

    public void allowPushNotifications(boolean show) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(Key.USER_ALLOW_PUSH.toString(), show);
        editor.apply();
    }

    public Integer getLastSyncVersion() {
        return mPreferences.getInt(Key.APP_LAST_SYNC_VERSION.toString(), 0);
    }

    public void setLastSyncVersion(int version) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(Key.APP_LAST_SYNC_VERSION.toString(), version);
        editor.apply();
    }

    public void setScheduleDay(int pos) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(Key.SCHEDULE_DAY_VIEW.toString(), pos);
        editor.apply();
    }

    public int getScheduleDay() {
        return mPreferences.getInt(Key.SCHEDULE_DAY_VIEW.toString(), 0);
    }

    public boolean allowPushNotifications() {
        return mPreferences.getBoolean(Key.USER_ALLOW_PUSH.toString(), true);
    }

    public boolean shouldShowMilitaryTime() {
        return mPreferences.getBoolean(Key.USER_MILITARY_TIME.toString(), false);
    }

    public boolean showExpiredEvents() {
        return mPreferences.getBoolean(Key.USER_EXPIRED_EVENTS.toString(), false);
    }

    public boolean showActiveEventsOnly() {
        return !showExpiredEvents();
    }

    public void saveFilter(Filter filter) {
        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(Key.USER_FILTER.toString(), filter.getTypesSet());
        editor.apply();
    }

    public Filter getFilter() {
        return new Filter(mPreferences.getStringSet(Key.USER_FILTER.toString(), new HashSet<String>()));
    }

    public void setLastRefresh(long time) {
        getEditor().putLong(Key.APP_LAST_REFRESH.toString(), time).apply();
    }

    public long getLastRefresh() {
        return mPreferences.getLong(Key.APP_LAST_REFRESH.toString(), 0);
    }

    public boolean shouldRefresh(long time) {
        return time - mPreferences.getLong(Key.APP_LAST_REFRESH.toString(), 0) > Constants.TIMER_INTERVAL;
    }

    public void setViewPagerPosition(int index) {
        getEditor().putInt(Key.APP_VIEW_PAGER_POSITION.toString(), index).commit();
    }

    public int getViewPagerPosition() {
        return mPreferences.getInt(Key.APP_VIEW_PAGER_POSITION.toString(), 0);
    }

    public boolean isTrackingAnalytics() {
        return mPreferences.getBoolean(Key.USER_ANALYTICS.toString(), true);
    }

    public boolean isSyncScheduled() {
        return mPreferences.getBoolean(Key.APP_SYNC_SCHEDULED.toString(), false);
    }

    public void setSyncScheduled() {
        getEditor().putBoolean(Key.APP_SYNC_SCHEDULED.toString(), true).apply();
    }
}
