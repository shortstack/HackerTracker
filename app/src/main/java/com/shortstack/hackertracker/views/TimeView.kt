package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.shortstack.hackertracker.databinding.RowHeaderTimeBinding
import com.shortstack.hackertracker.utilities.TimeUtil
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
