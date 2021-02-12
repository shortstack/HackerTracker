package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_settings_action.view.*

class SettingsActionView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_settings_action, this)

        context?.theme?.obtainStyledAttributes(
                attrs,
                R.styleable.SettingsActionView,
                0, 0)?.apply {
            try {
                label.text = getString(R.styleable.SettingsActionView_actionText)
            } finally {
                recycle()
            }
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        control_overlay.setOnClickListener {
            listener?.onClick(it)
        }
    }
}