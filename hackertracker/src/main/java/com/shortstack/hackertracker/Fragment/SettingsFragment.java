package com.shortstack.hackertracker.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.View;

import com.google.common.eventbus.EventBus;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;

/**
 * Created by Whitney Champion on 6/3/16.
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // add xml
        addPreferencesFromResource(R.xml.settings);

        // set listener for "clear schedule" preference
        Preference clearSchedule = findPreference("clearSchedule");
        clearSchedule.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                // show clear dialog
                DialogUtil.clearScheduleDialog(getContext()).show();

                return true;
            }

        });

        // set listener for "allow notifications" preference
        Preference allowNotifications = findPreference("allowNotifications");
        allowNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                if (preference.isEnabled()) {
                    // enable notifications
                    SharedPreferencesUtil.allowPushNotifications(true);
                } else {
                    // disable notifications
                    SharedPreferencesUtil.allowPushNotifications(false);
                }

                return true;
            }

        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
