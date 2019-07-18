package com.shortstack.hackertracker.ui.schedule.list

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.schedule.DayViewHolder
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.ui.schedule.TimeViewHolder
import com.shortstack.hackertracker.utils.StickyHeaderInterface
import com.shortstack.hackertracker.utils.TimeUtil
import kotlinx.android.synthetic.main.row_header_time.view.*
import java.util.*
import kotlin.collections.ArrayList

class ScheduleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderInterface{

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        val item = collection[itemPosition]
        return when(item) {
            is Time -> -1
            is Day -> -2
            is Event -> collection.indexOf(collection.subList(0, itemPosition).last { it is Time })
            else -> TODO()
        }
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.row_time_container
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
//        (header as TextView).text = TimeUtil.getDateStamp(collection[headerPosition] as Time)

//        header.text = TimeUtil.getTimeStamp(context, date)
    }

    override fun isHeader(itemPosition: Int) = collection[itemPosition] is Time

    companion object {
        private const val EVENT = 0
        private const val DAY = 1
        private const val TIME = 2
    }

    private val collection = ArrayList<Any>()

    var state: Status = Status.NOT_INITIALIZED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EVENT -> EventViewHolder.inflate(parent)
            DAY -> DayViewHolder.inflate(parent)
            TIME -> TimeViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType.")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = collection[position]

        when (holder) {
            is EventViewHolder -> holder.render(item as Event)
            is DayViewHolder -> holder.render(item as Day)
            is TimeViewHolder -> holder.render(item as Time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (collection[position]) {
            is Event -> EVENT
            is Day -> DAY
            is Time -> TIME
            else -> throw IllegalStateException("Unknown viewType ${collection[position].javaClass}")
        }
    }

    private fun getFormattedElements(elements: List<Event>): ArrayList<Any> {
        val result = ArrayList<Any>()
        
        elements.groupBy { it.date }.toSortedMap().forEach {
            result.add(Day(it.key))

            it.value.groupBy { it.start }.toSortedMap().forEach {
//                result.add(Time(it.key))

                if (it.value.isNotEmpty()) {
                    val group = it.value.sortedWith(compareBy({ it.type.name }, { it.location.name }))
                    result.addAll(group)
                }
            }
        }

        return result
    }

    fun setSchedule(list: List<Event>?): ArrayList<Any> {
        if (list == null) {
            val size = collection.size
            collection.clear()
            notifyItemRangeRemoved(0, size)
            return collection
        }

        val elements = getFormattedElements(list)

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val left = collection[oldItemPosition]
                val right = elements[newItemPosition]

                if (left is Event && right is Event) {
                    return left.id == right.id
                } else if (left is Day && right is Day) {
                    return left.time == right.time
                } else if (left is Time && right is Time) {
                    return left.time == right.time
                }
                return false
            }

            override fun getOldListSize() = collection.size

            override fun getNewListSize() = elements.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val left = collection[oldItemPosition]
                val right = elements[newItemPosition]

                if (left is Event && right is Event) {
                    return left.updated == right.updated && left.isBookmarked == right.isBookmarked
                            && left.title == right.title && left.location.name == right.location.name
                } else if (left is Day && right is Day) {
                    return left.time == right.time
                } else if (left is Time && right is Time) {
                    return left.time == right.time
                }
                return false
            }

        })

        result.dispatchUpdatesTo(this)

        collection.clear()
        collection.addAll(elements)

        return collection
    }

    fun isEmpty() = state == Status.SUCCESS && collection.isEmpty()

    fun clearAndNotify() {
        collection.clear()
        notifyDataSetChanged()
    }

    fun getDatePosition(date: Date): Int {
        return collection.indexOfFirst { it is Day && it.time == date.time }
    }

    fun getDateOfPosition(index: Int): Date {
        val obj = collection[index]
        return when(obj) {
            is Event -> obj.start
            is Time -> Date(obj.time)
            is Day -> Date(obj.time)
            else -> TODO()
        }
    }
}
