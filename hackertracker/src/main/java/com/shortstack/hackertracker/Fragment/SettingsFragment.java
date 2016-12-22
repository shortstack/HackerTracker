package com.shortstack.hackertracker.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Event.UpdateListContentsEvent;
import com.shortstack.hackertracker.R;

import static com.shortstack.hackertracker.Analytics.AnalyticsController.Analytics;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static SettingsFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Analytics event = Analytics.SETTINGS_EXPIRED_EVENT;

        switch (key) {
            case "user_analytics":
                event = Analytics.SETTINGS_ANALYTICS;
                break;

            case "user_allow_push_notifications":
                event = Analytics.SETTINGS_NOTIFICATIONS;
                break;

            case "user_use_military_time":
                event = Analytics.SETTINGS_MILITARY_TIME;
                App.getApplication().postBusEvent(new UpdateListContentsEvent());
                break;

            case "user_show_expired_events":
                event = Analytics.SETTINGS_EXPIRED_EVENT;
                App.getApplication().postBusEvent(new UpdateListContentsEvent());
                break;

            default:
                // We're not tracking these events, ignore.
                return;
        }

        boolean value = sharedPreferences.getBoolean(key, false);
        App.getApplication().getAnalyticsController().tagSettingsEvent(event, value);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
