package com.shortstack.hackertracker.ui.schedule.list

import com.pedrogomez.renderers.RendererAdapter
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

class ScheduleAdapter : RendererAdapter<Any>(ScheduleBuilder()) {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var storage: SharedPreferencesUtil

    init {
        App.application.component.inject(this)
    }

    fun addAllAndNotify(elements: List<DatabaseEvent>) {
        if (elements.isEmpty())
            return

        val size = collection.size

        val previous = collection.filterIsInstance<Event>().lastOrNull()
        val prevDay = previous?.date
        val prevTime = previous?.begin

        elements.groupBy { it.event.date }.forEach {
            if (prevDay != it.key) {
                addDay(it.key)
            }

            it.value.groupBy { it.event.begin }.forEach {
                if (prevTime != it.key) {
                    addTime(it.key)
                }
                addAll(it.value)
            }
        }

        notifyItemRangeInserted(size, collection.size - size)
    }

    private fun addDay(day: Date) {
        add(Day(day))
    }

    private fun addTime(time: Date) {
        add(Time(time))
    }

    fun notifyTimeChanged() {
        if (storage.showExpiredEvents)
            return

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
}
