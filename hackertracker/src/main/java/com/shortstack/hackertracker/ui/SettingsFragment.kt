package com.shortstack.hackertracker.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.analytics.AnalyticsController.Analytics
import com.shortstack.hackertracker.event.BusProvider
import com.shortstack.hackertracker.event.UpdateListContentsEvent
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var analytics: AnalyticsController

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.application.myComponent.inject(this)
    }

    inline fun Preference.setOnClick(crossinline func: Preference.() -> Unit) {
        this.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            func()
            false
        }
    }

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.settings)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        val event: Analytics

        when (key) {
            "user_analytics" -> event = Analytics.SETTINGS_ANALYTICS

            "user_allow_push_notifications" -> event = Analytics.SETTINGS_NOTIFICATIONS

            "user_use_military_time" -> {
                event = Analytics.SETTINGS_MILITARY_TIME
                BusProvider.bus.post(UpdateListContentsEvent())
            }

            "user_show_expired_events" -> {
                event = Analytics.SETTINGS_EXPIRED_EVENT
                BusProvider.bus.post(UpdateListContentsEvent())
            }

            "sync_interval" -> {
                App.application.scheduleSync()
                return
            }

            else ->
                // We're not tracking these events, ignore.
                return
        }

        val value = sharedPreferences.getBoolean(key, false)
        analytics.tagSettingsEvent(event, value)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}
