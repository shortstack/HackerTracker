package com.shortstack.hackertracker.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import android.view.View
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var analytics: AnalyticsController

    @Inject
    lateinit var database: DatabaseManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.application.component.inject(this)

        if (BuildConfig.DEBUG) {
            findPreference("dev_clear").setOnPreferenceClickListener {
                database.clear()
                return@setOnPreferenceClickListener true
            }
        }
    }

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        if (BuildConfig.DEBUG) addPreferencesFromResource(R.xml.dev_settings)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        val event = when (key) {
            "user_analytics" -> AnalyticsController.SETTINGS_ANALYTICS
            "user_allow_push_notifications" -> AnalyticsController.SETTINGS_NOTIFICATIONS
            "user_show_expired_events" -> AnalyticsController.SETTINGS_EXPIRED_EVENTS

            "sync_interval" -> {
                App.application.scheduleSyncTask()
                return
            }

        // We're not tracking these events, ignore.
            else -> return
        }

        val value = sharedPreferences.getBoolean(key, false)
        analytics.onSettingsChanged(event, value)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    companion object {

        fun newInstance() = SettingsFragment()

    }
}
