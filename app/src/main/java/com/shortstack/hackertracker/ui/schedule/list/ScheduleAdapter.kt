package com.shortstack.hackertracker.ui.schedule.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.advice.timehop.StickyRecyclerHeadersAdapter
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.schedule.DayViewHolder
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.ui.schedule.TimeViewHolder
import com.shortstack.hackertracker.views.EventView
import java.util.*
import kotlin.collections.ArrayList

class ScheduleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    companion object {
        private const val EVENT = 0
        private const val DAY = 1
    }

    private val collection = ArrayList<Any>()

    var state: Status = Status.NOT_INITIALIZED

    override fun getHeaderId(position: Int): Long {
        return when (val obj = collection[position]) {
            // (time - 1) to make it different than the first Event if they both start at 12:00:00
            is Day -> obj.time - 1
            is Event -> obj.key
            else -> throw java.lang.IllegalStateException("Unhandled object type ${obj.javaClass}")
        }
    }

    override fun getItemId(position: Int): Long {
        when (val obj = collection[position]) {
            is Event -> return obj.key
        }
        return super.getItemId(position)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TimeViewHolder.inflate(parent)
    }

    override fun onBindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val event = (collection[position] as? Event)

        if (viewHolder is TimeViewHolder) {
            if (event != null) {
                viewHolder.render(Time(Date(event.key)))
            } else {
                viewHolder.render(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EVENT -> EventViewHolder.inflate(parent, EventView.DISPLAY_MODE_FULL)
            DAY -> DayViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType.")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = collection[position]

        when (holder) {
            is EventViewHolder -> holder.render(item as Event)
            is DayViewHolder -> holder.render(item as Day)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (collection[position]) {
            is Event -> EVENT
            is Day -> DAY
            else -> throw IllegalStateException("Unknown viewType ${collection[position].javaClass}")
        }
    }

    private fun getFormattedElements(elements: List<Event>): ArrayList<Any> {
        val result = ArrayList<Any>()

        elements.groupBy { it.date }.toSortedMap().forEach {
            result.add(Day(it.key))

            it.value.groupBy { it.start }.toSortedMap().forEach {
                if (it.value.isNotEmpty()) {
                    val group = it.value.sortedWith(compareBy({ it.types.first().name }, { it.location.name }))

                    group.forEach { event -> event.key = it.key.time }

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
        return when (val obj = collection[index]) {
            is Event -> obj.start
            is Day -> Date(obj.time)
            else -> TODO()
        }
    }
}
