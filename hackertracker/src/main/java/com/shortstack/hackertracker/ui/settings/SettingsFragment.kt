package com.shortstack.hackertracker.ui.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.MaterialAlert
import com.shortstack.hackertracker.utilities.Storage
import com.shortstack.hackertracker.ui.themes.ThemeContainer
import com.shortstack.hackertracker.ui.themes.ThemesManager
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject


class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val database: DatabaseManager by inject()
    private val storage: Storage by inject()

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
    }

    private fun showChangeConferenceDialog() {
        val context = context ?: return

        val conferences = database.conferences.value ?: emptyList()
        val items = conferences.map { MaterialAlert.Item(it.name) }

        MaterialAlert(context)
                .setTitle(getString(R.string.change_conference))
                .setItems(items, DialogInterface.OnClickListener { _, which ->
                    database.changeConference(conferences[which].id)
                }).show()
    }

    private fun showChangeThemeDialog() {
        val context = context ?: return

        val list = ThemesManager.getThemes()

        val items = list.map { MaterialAlert.Item(it.label) }

        MaterialAlert(context)
                .setTitle(getString(R.string.change_theme))
                .setItems(items, DialogInterface.OnClickListener { _, which ->
                    storage.theme = list[which].id
                    (context as MainActivity).recreate()
                }).show()
    }
}
