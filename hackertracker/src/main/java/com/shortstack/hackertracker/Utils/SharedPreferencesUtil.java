package com.shortstack.hackertracker.Utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Filter;

import java.util.HashSet;

/**
 * Created by Whitney Champion on 7/11/14.
 */
public class SharedPreferencesUtil {

    public enum Key {

        USER_FILTER("user_filter"),
        USER_ALLOW_PUSH("user_allow_push_notifications"),
        USER_MILITARY_TIME("user_use_military_time"),
        USER_SEEN_ONBOARDING("user_seen_onboarding"),

        APP_LAST_UPDATED("app_last_updated"),
        ;


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
        mPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
    }

    private SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }

    public void saveLastUpdated(String date) {
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

    public boolean allowPushNotifications() {
        return mPreferences.getBoolean(Key.USER_ALLOW_PUSH.toString(), true);
    }

    public boolean shouldShowMilitaryTime() {
        return mPreferences.getBoolean(Key.USER_MILITARY_TIME.toString(), false);
    }

    public void saveFilter(Filter filter) {
        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(Key.USER_FILTER.toString(), filter.getTypesSet());
        editor.apply();
    }

    public Filter getFilter() {
        return new Filter(mPreferences.getStringSet(Key.USER_FILTER.toString(), new HashSet<String>()));
    }

    public boolean seenOnboarding() {
        return mPreferences.getBoolean(Key.USER_SEEN_ONBOARDING.toString(), false);
    }

    public void markOnboardingSeen() {
        getEditor().putBoolean(Key.USER_SEEN_ONBOARDING.toString(), true).apply();
    }
}
