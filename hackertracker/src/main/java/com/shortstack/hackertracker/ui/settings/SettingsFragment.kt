package com.shortstack.hackertracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject


class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val database: DatabaseManager by inject()
    private val storage: Storage by inject()
    private val themes: ThemesManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }


        change_theme.setOnClickListener {
            showChangeThemeDialog()
        }

        change_conference.setOnClickListener {
            showChangeConferenceDialog()
        }

        database.conference.observe(this, Observer {
            if (it != null) {
                force_time_zone.text = getString(R.string.setting_time_zone, it.timezone)
            }
        })

        version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
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
