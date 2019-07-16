package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utils.TimeUtil
import kotlinx.android.synthetic.main.row_header_time.view.*
import org.koin.standalone.KoinComponent
import java.util.*

class TimeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), KoinComponent {

    private lateinit var date: Date

    init {
        inflate(context, R.layout.row_header_time, this)
        orientation = LinearLayout.VERTICAL
    }

    fun setContent(content: Date) {
        date = content
        render()
    }

    fun render() {
        header.text = TimeUtil.getTimeStamp(context, date)
    }

}
