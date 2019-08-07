package com.shortstack.hackertracker.ui.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.utilities.MaterialAlert
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
    private val analytics: Analytics by inject()

    private var index = 0

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

        version.text = getString(R.string.version, BuildConfig.VERSION_NAME)

        logo.setOnClickListener {
            if (storage.isHacker)
                return@setOnClickListener

            index++
            if (index in 4..9) {
                Toast.makeText(context, "${10 - index} clicks to become a hacker..", Toast.LENGTH_SHORT).show()
            }
            if (index == 10) {
                analytics.logCustom(CustomEvent("Theme - Hacker"))
                storage.isHacker = true
                Toast.makeText(context, "You're now a hacker!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showChangeConferenceDialog() {
        val context = context ?: return

        val conferences = database.conferences.value ?: emptyList()
        val current = database.conference.value
        val items = conferences.map { MaterialAlert.Item(it.name, it.code == current?.code) }

        MaterialAlert(context)
                .setTitle(getString(R.string.choose_conference))
                .setItems(items, DialogInterface.OnClickListener { _, which ->
                    database.changeConference(conferences[which].id)
                }).show()
    }

    private fun showChangeThemeDialog() {
        val context = context ?: return

        val list = themes.getThemes()

        val items = list.map { MaterialAlert.Item(it.label, it.label == storage.theme?.label) }

        MaterialAlert(context)
                .setTitle(getString(R.string.choose_theme))
                .setItems(items, DialogInterface.OnClickListener { _, which ->
                    storage.theme = list[which]
                    (context as MainActivity).recreate()
                }).show()
    }
}
