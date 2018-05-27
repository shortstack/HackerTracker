package com.shortstack.hackertracker.ui.schedule.list

import android.support.v7.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.pedrogomez.renderers.RendererAdapter
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.Item
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class ScheduleItemAdapter(private val listViews: ListViewsInterface,
                          private val layout: RecyclerView.LayoutManager,
                          val list: RecyclerView) : RendererAdapter<Any>(ScheduleItemBuilder()) {

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var storage: SharedPreferencesUtil

    init {
        App.application.myComponent.inject(this)
    }

    fun initContents() {
        clearAndNotify()
        listViews.hideViews()
        load()
    }

    fun load(page: Int = 0) {
        val filter = storage.filter

        // TODO: Add in the filter.
        database.getSchedule(/**filter.typesArray, page = page*/)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            addAllAndNotify(it)

                            // TODO: This solution does not work with pagination.
//                            if (storage.showExpiredEvents()) {
//                                scrollToCurrentTime()
//                            }
                            if (collection.isEmpty()) {
                                listViews.showEmptyView()
                            }
                            
                            // TODO: Remove, this is only for debugging.
                            if( page == 0 ) {
                                Logger.d("Loaded first chunk " + (System.currentTimeMillis() - App.application.timeToLaunch))
                            }
                        }, {
                    e ->
                    Logger.e(e, "Not success.")
                    listViews.showErrorView()
                })
    }


    private fun addAllAndNotify(elements: List<Event>) {
        if (elements.isEmpty())
            return

        val size = collection.size

        val previous = collection.filterIsInstance<Event>().lastOrNull()
        val prevDay = previous?.date
        val prevTime = previous?.begin

        elements.groupBy { it.date }.forEach {
            if (prevDay != it.value.first().date) addDay(it.value.first())

            it.value.groupBy { it.begin }.forEach {
                if (prevTime != it.value.first().begin) addTime(it.value.first())
                addAll(it.value)
            }
        }

        notifyItemRangeInserted(size, collection.size - size)
    }

    private fun addDay(item: Event) {
        add(Day(item.begin))
    }

    private fun addTime(item: Event) {
        add(Time(item.begin))
    }

    private fun scrollToCurrentTime() {
        val position = findCurrentPositionByTime()

        if (position != -1) {
            layout.scrollToPosition(position)
        }
    }

    private fun findCurrentPositionByTime(): Int {
        val currentDate = Date().now()

        val first = collection.filterIsInstance<Item>()
                .firstOrNull { it.beginDateObject.after(currentDate) } ?: return -1

        val indexOf = collection.indexOf(first)

        return indexOf - 1
    }

    fun notifyTimeChanged() {

        if (storage.showExpiredEvents())
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
