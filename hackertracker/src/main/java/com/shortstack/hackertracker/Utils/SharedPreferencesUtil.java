package com.shortstack.hackertracker.Utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shortstack.hackertracker.Application.HackerTrackerApplication;

/**
 * Created by Whitney Champion on 7/11/14.
 */
public class SharedPreferencesUtil {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferencesUtil instance = null;

    protected SharedPreferencesUtil() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HackerTrackerApplication.getAppContext());
    }

    public static SharedPreferencesUtil getInstance() {

        if(instance == null) {
            instance = new SharedPreferencesUtil();
        }

        return instance;
    }

    public static void clearSharedPrefs() {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.clear();
        sharedPreferencesEditor.commit();
    }

    public static void saveLastUpdated(String date) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("lastUpdated", date);
        sharedPreferencesEditor.commit();
    }

    public static String getLastUpdated() {
        return sharedPreferences.getString("lastUpdated",null);
    }

    public static void showSuggestions(boolean show) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("showSuggestions",show);
        sharedPreferencesEditor.commit();
    }

    public static boolean showSuggestions() {
        return sharedPreferences.getBoolean("showSuggestions",false);
    }

    public static void allowPushNotifications(boolean show) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("allowPushNotifications",show);
        sharedPreferencesEditor.commit();
    }

    public static boolean allowPushNotifications() {
        return sharedPreferences.getBoolean("allowPushNotifications",true);
    }

}
