package com.shortstack.hackertracker.ui.schedule.list

import androidx.recyclerview.widget.DiffUtil
import com.pedrogomez.renderers.RendererAdapter
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.Time

class ScheduleAdapter : RendererAdapter<Any>(ScheduleBuilder().rendererBuilder) {

    var state: Status = Status.NOT_INITIALIZED

    private fun getFormattedElements(elements: List<FirebaseEvent>): ArrayList<Any> {
        val result = ArrayList<Any>()

        val previous = collection.filterIsInstance<FirebaseEvent>().lastOrNull()
        val prevDay = previous?.date
        val prevTime = previous?.start

        elements.groupBy { it.date }.toSortedMap().forEach {
            if (prevDay != it.key) {
                val day = Day(it.key)
                result.add(day)
            }

            it.value.groupBy { it.start }.toSortedMap().forEach {
                if (prevTime != it.key) {
                    val time = Time(it.key)
                    result.add(time)
                }

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

        val collection = collection.toList()

        collection.forEach {
            if (it is FirebaseEvent && it.hasFinished) {
                removeAndNotify(it)
            }
        }


        if (collection.size != this.collection.size) {

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
            if (this.collection.size == 2) {
                clearAndNotify()
            }
        }
    }

    fun setSchedule(list: List<FirebaseEvent>?) {
        if (list == null) {
            clearAndNotify()
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
//
                if (left is FirebaseEvent && right is FirebaseEvent) {
                    return left.updated == right.updated && left.isBookmarked == right.isBookmarked
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
}
