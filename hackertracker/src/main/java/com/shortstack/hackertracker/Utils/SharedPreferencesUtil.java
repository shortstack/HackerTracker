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

    public static void showSuggestions(boolean show) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("showSuggestions",show);
        sharedPreferencesEditor.commit();
    }

    public static boolean showSuggestions() {
        return sharedPreferences.getBoolean("showSuggestions",true);
    }

}
