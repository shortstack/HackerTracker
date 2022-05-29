package com.shortstack.hackertracker.ui.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.RowHeaderBinding
import com.shortstack.hackertracker.databinding.RowTimeContainerBinding
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.utilities.TimeUtil

class TimeViewHolder(private val binding: RowTimeContainerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun inflate(parent: ViewGroup): TimeViewHolder {
            val binding =
                RowTimeContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TimeViewHolder(binding)
        }
    }

    fun render(time: Time?) {
        binding.timeItem.render(time)
    }
}

class DayViewHolder(private val binding: RowHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun inflate(parent: ViewGroup): DayViewHolder {
            val binding =
                RowHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DayViewHolder(binding)
        }
    }

    fun render(day: Day) {
        binding.root.text = TimeUtil.getDateStamp(day)
    }
}
