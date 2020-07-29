package com.shortstack.hackertracker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Conference
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject


class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingsFragment()

        private const val CHANGE_THEME_KEY = "change_theme"
        private const val CHANGE_CONFERENCE_KEY = "change_conference"

        private const val FORCE_TIMEZONE_KEY = "force_time_zone"
        private const val USER_ANALYTICS_KEY = "user_analytics"
        private const val EASTER_EGG_KEY = "easter_eggs"
        private const val BACK_BUTTON_DRAWER_KEY = "nav_drawer_on_back"
    }

    private val database: DatabaseManager by inject()
    private val storage: Storage by inject()
    private val themes: ThemesManager by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        screen.apply {

            // Change Theme
            addPreference(Preference(context).apply {
                title = getString(R.string.setting_change_theme)
                summary = storage.theme?.label
                key = CHANGE_THEME_KEY

                setOnPreferenceClickListener {
                    showChangeThemeDialog()
                    true
                }
            })

            // Change Conference
            addPreference(Preference(context).apply {
                title = getString(R.string.setting_change_conference)
                summary = "DEF CON 28"
                key = CHANGE_CONFERENCE_KEY

                setOnPreferenceClickListener {
                    showChangeConferenceDialog()
                    true
                }
            })

            // Timezone
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_time_zone)
                summaryOn = getString(R.string.setting_time_zone_summary_on)
                summaryOff = getString(R.string.setting_time_zone_summary_off)
                key = FORCE_TIMEZONE_KEY
            })

            // Back Button
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_back_button_drawer)
                key = BACK_BUTTON_DRAWER_KEY
            })

            // Easter Eggs
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_easter_eggs)
                summary = getString(R.string.setting_easter_eggs_summary)
                key = EASTER_EGG_KEY
            })

            // Analytics
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_user_analytics)
                key = USER_ANALYTICS_KEY
            })
        }

        preferenceScreen = screen
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }

        database.conference.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                updateConference(it)
                updateTimezonePreference(it)
            }
        })

        version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    private fun updateConference(conference: Conference) {
        val preference = preferenceScreen.findPreference<Preference>(CHANGE_CONFERENCE_KEY)
        preference?.summary = conference.name
    }

    private fun updateTimezonePreference(conference: Conference) {
        val preference = preferenceScreen.findPreference<SwitchPreference>(FORCE_TIMEZONE_KEY)
        preference?.title = getString(R.string.setting_time_zone, conference.timezone)
    }


    private fun showChangeConferenceDialog() {
        val context = context ?: return

        val conferences = database.conferences.value ?: emptyList()
        val selected = conferences.indexOf(database.conference.value)

        val items = conferences.map { it.name }.toTypedArray()

        AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
            .setTitle(getString(R.string.choose_conference))
            .setSingleChoiceItems(items, selected) { dialog, which ->
                database.changeConference(conferences[which].id)
                dialog.dismiss()
            }.show()
    }

    private fun showChangeThemeDialog() {
        val context = context ?: return

        val list = themes.getThemes()
        val selected = list.indexOf(storage.theme)

        val items = list.map { it.label }.toTypedArray()

        AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
            .setTitle(getString(R.string.choose_theme))
            .setSingleChoiceItems(items, selected) { dialog, which ->
                storage.theme = list[which]
                dialog.dismiss()
                (context as MainActivity).recreate()
            }.show()
    }
}
