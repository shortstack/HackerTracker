package com.shortstack.hackertracker.ui.schedule.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.firebase.FirebaseEvent
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.ui.schedule.DayViewHolder
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.ui.schedule.TimeViewHolder

class ScheduleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            is EventViewHolder -> holder.render(item as FirebaseEvent)
            is DayViewHolder -> holder.render(item as Day)
            is TimeViewHolder -> holder.render(item as Time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (collection[position]) {
            is FirebaseEvent -> EVENT
            is Day -> DAY
            is Time -> TIME
            else -> throw IllegalStateException("Unknown viewType ${collection[position].javaClass}")
        }
    }

    private fun getFormattedElements(elements: List<FirebaseEvent>): ArrayList<Any> {
        val result = ArrayList<Any>()
        
        elements.groupBy { it.date }.toSortedMap().forEach {
            result.add(Day(it.key))

            it.value.groupBy { it.start }.toSortedMap().forEach {
                result.add(Time(it.key))

                if (it.value.isNotEmpty()) {
                    val group = it.value.sortedWith(compareBy({ it.type.name }, { it.location.name }))
                    result.addAll(group)
                }
            }
        }

        return result
    }

    fun notifyTimeChanged() {
        if (collection.isEmpty())
            return

        val list = collection.toList()

        list.forEach {
            if (it is FirebaseEvent && it.hasFinished) {
                removeAndNotify(it)
            }
        }


        if (list.size != this.collection.size) {

            for (i in this.collection.size - 1 downTo 1) {
                val any = this.collection[i]
                val any1 = this.collection[i - 1]
                if ((any is Day && any1 is Day)
                        || (any is Time && any1 is Time)
                        || (any is Day && any1 is Time)) {
                    removeAndNotify(any1)
                }
            }

            // If no events and only headers remain.
            if (collection.size == 2) {
                val size = list.size
                collection.clear()
                notifyItemRangeRemoved(0, size)
            }
        }
    }

    private fun removeAndNotify(item: Any) {
        val index = collection.indexOf(item)
        if (index != -1) {
            collection.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun setSchedule(list: List<FirebaseEvent>?) {
        if (list == null) {
            val size = collection.size
            collection.clear()
            notifyItemRangeRemoved(0, size)
            return
        }

        val elements = getFormattedElements(list)

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val left = collection[oldItemPosition]
                val right = elements[newItemPosition]

                if (left is FirebaseEvent && right is FirebaseEvent) {
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

                if (left is FirebaseEvent && right is FirebaseEvent) {
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
    }

    fun isEmpty() = state == Status.SUCCESS && collection.isEmpty()
    fun clearAndNotify() {
        collection.clear()
        notifyDataSetChanged()
    }
}
