package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import kotlinx.android.synthetic.main.view_settings_switch.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SettingsSwitchView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), KoinComponent {

    private val storage: SharedPreferencesUtil by inject()
    private val analytics: AnalyticsController by inject()

    init {
        inflate(context, R.layout.view_settings_switch, this)

        context?.theme?.obtainStyledAttributes(
                attrs,
                R.styleable.SettingsSwitchView,
                0, 0)?.apply {
            try {
                label.text = getString(R.styleable.SettingsSwitchView_switchText)
                control.tag = getString(R.styleable.SettingsSwitchView_switchKey)
                control.isChecked = getBoolean(R.styleable.SettingsSwitchView_switchDefaultValue, true)
            } finally {
                recycle()
            }
        }

        control_overlay.setOnClickListener {
            onClick()
        }
    }


    override fun setOnClickListener(listener: OnClickListener) {
        control_overlay.setOnClickListener {
            listener.onClick(it)
            onClick()
        }
    }

    private fun onClick() {
        control.isChecked = !control.isChecked
        storage.setPreference(control.tag as String, control.isChecked)

        val event = when (control.tag as String) {
            "user_analytics" -> AnalyticsController.SETTINGS_ANALYTICS
            "user_allow_push_notifications" -> AnalyticsController.SETTINGS_NOTIFICATIONS
            "user_show_expired_events" -> AnalyticsController.SETTINGS_EXPIRED_EVENTS
            // We're not tracking these events, ignore.
            else -> return
        }

        analytics.onSettingsChanged(event, control.isChecked)
    }
}