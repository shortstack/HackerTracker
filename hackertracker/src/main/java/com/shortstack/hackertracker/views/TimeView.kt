package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utilities.TimeUtil
import kotlinx.android.synthetic.main.row_header_time.view.*
import java.util.*

class TimeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        inflate(context, R.layout.row_header_time, this)
        orientation = VERTICAL
    }

    fun render(date: Date?) {
        if(date == null) {
            header.text = null
        } else {
            header.text = TimeUtil.getTimeStamp(context, date)
        }
    }

}
