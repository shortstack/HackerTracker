package com.shortstack.hackertracker.ui.information.info.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_code_of_conduct.view.*


class CodeOfConductView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_code_of_conduct, this)
    }

    fun setText(conduct: String?) {
        content.text = conduct?.replace("\\n", "\n")
    }

}