package com.shortstack.hackertracker.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.local.Event
import java.text.SimpleDateFormat
import java.util.*

class DayIndicatorAdapter : ListAdapter<Day, DayIndicatorAdapter.DayIndicatorViewHolder>(IndicatorDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayIndicatorViewHolder {
        return DayIndicatorViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: DayIndicatorViewHolder, position: Int) {
        holder.render(getItem(position))
    }

    fun getDatePosition(date: Date): Int {
        val collection = ArrayList<Day>()
        for(i in 0 until itemCount) {
            collection.add(getItem(i))
        }


        val calendar = Calendar.getInstance()

        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)


        val otherDay = Day(calendar.time)

        return collection.indexOfFirst { it is Day && it.time == otherDay.time }
    }

    fun getDateOfPosition(index: Int): Date {
        val collection = ArrayList<Day>()
        for(i in 0 until itemCount) {
            collection.add(getItem(i))
        }

        return when (val obj = collection[index]) {
            else -> Date(obj.time)
        }
    }

    fun getRange(begin: Date, end: Date): IntRange {
        val instance = Calendar.getInstance()

        instance.time = begin
        instance.set(Calendar.HOUR_OF_DAY, 0)
        instance.set(Calendar.MINUTE, 0)
        instance.set(Calendar.SECOND, 0)
        instance.set(Calendar.MILLISECOND, 0)

        val beginDay = instance.time

        instance.time = end
        instance.set(Calendar.HOUR_OF_DAY, 0)
        instance.set(Calendar.MINUTE, 0)
        instance.set(Calendar.SECOND, 0)
        instance.set(Calendar.MILLISECOND, 0)

        val endDay = instance.time

        val collection = ArrayList<Day>()
        for(i in 0 until itemCount) {
            collection.add(getItem(i))
        }

        val dates = collection.map { Date(it.time) }

        val first = dates.indexOfFirst { it.time == beginDay.time }
        val last = dates.indexOfFirst { it.time == endDay.time }

        Logger.d("Setting Range: $first .. $last")

        return first..last

    }

    class DayIndicatorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        companion object {

            fun inflate(parent: ViewGroup): DayIndicatorViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_day_indicator, parent, false)
                return DayIndicatorViewHolder(view)
            }
        }


        fun render(day: Day) {
            val format = SimpleDateFormat("MMM d")

            (view as TextView).text = format.format(day.time)
        }

    }

    object IndicatorDiff : DiffUtil.ItemCallback<Day>() {
        override fun areItemsTheSame(oldItem: Day, newItem: Day) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Day, newItem: Day) = oldItem.time == newItem.time

    }


}