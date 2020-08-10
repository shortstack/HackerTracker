package com.shortstack.hackertracker.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.utilities.TimeUtil
import kotlinx.android.synthetic.main.row_time_container.view.*

class TimeViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): TimeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_time_container, parent, false)
            return TimeViewHolder(view)
        }
    }

    fun render(time: Time?) {
        view.time_item.render(time)
    }
}

class DayViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): DayViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_header, parent, false)
            return DayViewHolder(view)
        }
    }

    fun render(day: Day) {
        (view as TextView).text = TimeUtil.getDateStamp(day)
    }
}
