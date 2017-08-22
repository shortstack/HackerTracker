package com.shortstack.hackertracker.List

import android.support.v7.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.pedrogomez.renderers.RendererAdapter
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Model.Day
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.Time
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ScheduleItemAdapter(private val layout: RecyclerView.LayoutManager, val list: RecyclerView) : RendererAdapter<Any>(ScheduleItemBuilder()) {

    fun initContents() {
        load()
    }

    fun load(page: Int = 0) {
        val app = App.application
        val filter = app.storage.filter

        app.databaseController.getItems(*filter.typesArray, page = page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            addAllAndNotify(it)
                            if (app.storage.showExpiredEvents()) {
                                scrollToCurrentTime()
                            }
                        }, {
                    e ->
                    Logger.e(e, "Not success.")
                }
                )
    }

    private fun addAllAndNotify(elements: List<Item>) {

        if (elements.isEmpty())
            return

        val size = collection.size

        val first = elements.first()
        if (size == 0) {
            addDay(first)
            addTime(first)
        } else {
            val item = collection.last() as Item

            if (first.date != item.date) {
                addDay(first)
            }
            if (first.begin != item.begin) {
                addTime(first)
            }
        }

        for (i in 0..elements.size - 1 - 1) {
            val current = elements[i]

            add(current)

            val next = elements[i + 1]
            if (current.date != next.date) {
                addDay(next)
            }

            if (current.begin != next.begin) {
                addTime(next)
            }
        }

        add(elements[elements.size - 1])

        notifyItemRangeInserted(size, collection.size - size)
    }

    private fun addDay(item: Item) {
        add(Day(item.beginDateObject))
    }

    private fun addTime(item: Item) {
        add(Time(item.beginDateObject))
    }

    private fun scrollToCurrentTime() {
        layout.scrollToPosition(findCurrentPositionByTime())
    }

    private fun findCurrentPositionByTime(): Int {
        val currentDate = App.getCurrentDate()

        for (i in collection.indices) {
            val obj = collection[i]

            if (obj is Item) {

                val beginDateObject = obj.beginDateObject
                if (beginDateObject.after(currentDate)) {
                    for (i1 in i - 1 downTo 0) {
                        if (obj !is String) {
                            return i1
                        }
                    }
                    return i
                }
            }
        }

        return 0
    }

    fun notifyTimeChanged() {

        if (App.application.storage.showExpiredEvents())
            return

        val collection = collection
        var hasRemovedEvent = false

        for (i in collection.indices.reversed()) {
            if (collection[i] is Item) {
                val def = collection[i] as Item

                if (def.hasExpired()) {
                    collection.removeAt(i)
                    notifyItemRemoved(i)

                    hasRemovedEvent = true
                }
            }
        }


        if (hasRemovedEvent) {
            for (i in collection.size - 1 downTo 1) {
                if (collection[i] is Date && collection[i - 1] is Date
                        || collection[i] is String && collection[i - 1] is String
                        || collection[i] is String && collection[i - 1] is Date) {
                    collection.removeAt(i - 1)
                    notifyItemRemoved(i - 1)
                }
            }

            // If no events and only headers remain.
            if (collection.size == 2) {
                if (collection[0] is String && collection[1] is Date) {
                    collection.clear()
                    notifyItemRangeRemoved(0, 2)
                }
            }
        }
    }
}
