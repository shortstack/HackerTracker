package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.utilities.Storage
import kotlinx.android.synthetic.main.view_settings_switch.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SettingsSwitchView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), KoinComponent {

    private val storage: Storage by inject()
    private val analytics: Analytics by inject()

    init {
        inflate(context, R.layout.view_settings_switch, this)

        context?.theme?.obtainStyledAttributes(
                attrs,
                R.styleable.SettingsSwitchView,
                0, 0)?.apply {
            try {

                val text = getString(R.styleable.SettingsSwitchView_switchText)
                val defaultValue = getBoolean(R.styleable.SettingsSwitchView_switchDefaultValue, true)
                val key = getString(R.styleable.SettingsSwitchView_switchKey)

                label.text = text
                control.tag = key
                control.id = key.hashCode()

                if (!isInEditMode) {
                    val isChecked = storage.getPreference(key, defaultValue)
                    control.isChecked = isChecked
                } else {
                    control.isChecked = defaultValue
                }
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
            "user_analytics" -> Analytics.SETTINGS_ANALYTICS
            "user_allow_push_notifications" -> Analytics.SETTINGS_NOTIFICATIONS
            "user_show_expired_events" -> Analytics.SETTINGS_EXPIRED_EVENTS
            // We're not tracking these events, ignore.
            else -> return
        }

        analytics.onSettingsChanged(event, control.isChecked)
    }
}