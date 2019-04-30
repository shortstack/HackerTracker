package com.shortstack.hackertracker.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.utils.TimeUtil
import kotlinx.android.synthetic.main.row_time_container.view.*

class TimeViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_time_container, parent, false)
    }

    fun render(time: Time) {
        view.time_item.setContent(time)
    }
}

class DayViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.row_header, parent, false)
    }

    fun render(day: Day) {
        (view as TextView).text = TimeUtil.getRelativeDateStamp(view.context, day)
    }
}
