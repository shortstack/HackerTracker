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
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.Storage
import com.shortstack.hackertracker.utilities.now
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject
import java.util.*


class SettingsFragment : PreferenceFragmentCompat() {


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
                key = Storage.FORCE_TIME_ZONE_KEY
            })

            // Back Button
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_back_button_drawer)
                key = Storage.NAV_DRAWER_ON_BACK_KEY
            })

            // Easter Eggs
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_easter_eggs)
                summary = getString(R.string.setting_easter_eggs_summary)
                key = Storage.EASTER_EGGS_ENABLED_KEY
            })

            // Analytics
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_user_analytics)
                key = Storage.USER_ANALYTICS_KEY
            })

            val calendar = Calendar.getInstance()
            calendar.time = MyClock().now()

            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            if (dayOfYear >= 219 && storage.getPreference(Storage.EASTER_EGGS_ENABLED_KEY, false)) {
                // Safe Mode
                addPreference(Preference(context).apply {
                    title = getString(R.string.settings_reboot)
                    summary = getString(R.string.settings_safe_mode_summary)
                    key = SAFE_MODE_KEY
                    setOnPreferenceClickListener {
                        storage.theme = ThemesManager.Theme.SafeMode
                        storage.setPreference(Storage.SAFE_MODE_ENABLED, true)
                        requireActivity().recreate()
                        true
                    }
                })
            }
        }

        preferenceScreen = screen
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // todo:
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

        var index = 0
        version.setOnClickListener {
            if (index++ == 10) {
                storage.setPreference(Storage.DEVELOPER_THEME_UNLOCKED, true)
            }
        }
    }

    private fun updateConference(conference: Conference) {
        val preference = preferenceScreen.findPreference<Preference>(CHANGE_CONFERENCE_KEY)
        preference?.summary = conference.name
    }

    private fun updateTimezonePreference(conference: Conference) {
        val preference =
            preferenceScreen.findPreference<SwitchPreference>(Storage.FORCE_TIME_ZONE_KEY)
        preference?.title = getString(R.string.setting_time_zone, conference.timezone.toUpperCase())
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

    companion object {
        fun newInstance() = SettingsFragment()

        private const val CHANGE_THEME_KEY = "change_theme"
        private const val CHANGE_CONFERENCE_KEY = "change_conference"
        private const val SAFE_MODE_KEY = "safe_mode"
    }
}
