package com.advice.schedule.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.advice.schedule.utilities.TimeUtil
import com.shortstack.hackertracker.databinding.RowHeaderTimeBinding
import java.util.*

class TimeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val binding = RowHeaderTimeBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
    }

    fun render(date: Date?) {
        binding.header.text = if (date != null) {
            TimeUtil.getTimeStamp(context, date)
        } else {
            null
        }
    }
}
