package com.shortstack.hackertracker.ui.schedule.list

import androidx.recyclerview.widget.DiffUtil
import com.orhanobut.logger.Logger
import com.pedrogomez.renderers.RendererAdapter
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ScheduleAdapter : RendererAdapter<Any>(ScheduleBuilder().rendererBuilder) {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var storage: SharedPreferencesUtil

    var state: Status = Status.NOT_INITIALIZED

    init {
        App.application.component.inject(this)
    }

    private fun getFormattedElements(elements: List<FirebaseEvent>): ArrayList<Any> {
        val result = ArrayList<Any>()

        val previous = collection.filterIsInstance<FirebaseEvent>().lastOrNull()
        val prevDay = previous?.date
        val prevTime = previous?.start

        elements.groupBy { it.date }.toSortedMap().forEach {
            if (prevDay != it.key) {
                val day = Day(it.key)
                Logger.d("adding day $day")
                result.add(day)
            }

            it.value.groupBy { it.start }.toSortedMap().forEach {
                if (prevTime != it.key) {
                    val time = Time(it.key)
                    Logger.d("Added time $time")
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
            if (it is DatabaseEvent && it.event.hasFinished) {
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

                if (left is DatabaseEvent && right is DatabaseEvent) {
                    return left.event.id == right.event.id
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

                if (left is DatabaseEvent && right is DatabaseEvent) {
                    return left.event.updatedAt == right.event.updatedAt && left.event.isBookmarked == right.event.isBookmarked
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
