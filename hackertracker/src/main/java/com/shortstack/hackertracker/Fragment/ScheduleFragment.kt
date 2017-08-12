package com.shortstack.hackertracker.Fragment

import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Alert.MaterialAlert
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Event.RefreshTimerEvent
import com.shortstack.hackertracker.Event.SyncResponseEvent
import com.shortstack.hackertracker.Event.UpdateListContentsEvent
import com.shortstack.hackertracker.List.ScheduleItemAdapter
import com.shortstack.hackertracker.Model.Filter
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Network.DatabaseService
import com.shortstack.hackertracker.Network.SyncRepository
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.View.FilterView
import com.squareup.otto.Subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class ScheduleFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var adapter: ScheduleItemAdapter


    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            App.application.postBusEvent(RefreshTimerEvent())
            if (adapter != null) {
                adapter!!.notifyTimeChanged()
                updateFeedErrors()
            }

        }
    }
    private var mTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.application.registerBusListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.application.unregisterBusListener(this)
    }


    override fun onResume() {
        super.onResume()

        val currentDate = App.getCurrentDate()
        var time = currentDate.time

        if (App.storage.shouldRefresh(time)) {
            mHandler.obtainMessage(1).sendToTarget()
        }

        time %= Constants.TIMER_INTERVAL


        mTimer = Timer()
        mTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                mHandler.obtainMessage(1).sendToTarget()
            }
        }, time, Constants.TIMER_INTERVAL)
    }

    override fun onPause() {
        super.onPause()
        mTimer!!.cancel()

    }

    @Subscribe
    fun handleUpdateListContentsEvent(event: UpdateListContentsEvent) {
        refreshContents()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(contentView, container, false) as ViewGroup
        setHasOptionsMenu(true)

        val layout = LinearLayoutManager(context)
        rootView.list!!.layoutManager = layout

        rootView.swipe_refresh!!.setOnRefreshListener(this)
        rootView.swipe_refresh!!.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        adapter = ScheduleItemAdapter()
        rootView.list!!.adapter = adapter


        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshContents()
    }

    private fun hasFilters(): Boolean {
        val filter = App.storage.filter
        return !filter.typesSet.isEmpty()
    }

    private fun hasScheduleItems(): Boolean {
        return !adapter!!.collection.isEmpty()
    }

    private fun updateFeedErrors() {

        tutorial!!.visibility = View.GONE
        empty!!.visibility = View.GONE

        //        if (!hasFilters()) {
        //            tutorial.setVisibility(View.VISIBLE);
        //        } else
        if (!hasScheduleItems()) {
            empty!!.visibility = View.VISIBLE
        }
    }

    private fun refreshContents() {
        adapter!!.clear()

        val filter = App.storage.filter
        val events = getEvents(filter)

        val objects = addTimeDividers(events)
        adapter!!.addAll(objects)

        updateFeedErrors()


        adapter!!.notifyDataSetChanged()

        if (App.storage.showExpiredEvents())
            scrollToCurrentTime()
    }

    private fun scrollToCurrentTime() {
        list!!.layoutManager.scrollToPosition(findCurrentPositionByTime())
    }

    private fun findCurrentPositionByTime(): Int {
        val collection = adapter!!.collection
        val currentDate = App.getCurrentDate()

        for (i in collection.indices) {
            val obj = collection[i]

            if (obj is Item) {

                val beginDateObject = obj.beginDateObject
                if (beginDateObject.after(currentDate)) {
                    for (i1 in i - 1 downTo 0) {
//                        if (obj !is String) {
//                            return i1
//                        }
                    }
                    return i
                }
            }
        }

        return 0
    }

    protected fun getEvents(filter: Filter): List<Item> {
        //        if( !hasFilters() ) {
        //            return Collections.emptyList();
        //        }

        try {
            val events: List<Item> = App.application.databaseController.getItemByDate(*filter.typesArray)
            return events
        } catch (ex: SQLiteException) {
            Logger.e(ex, "Could not open the database.")
            return emptyList()
        }

    }

    private fun addTimeDividers(events: List<Item>): List<Any> {
        val result = ArrayList<Any>()

        if (events.isEmpty())
            return result

        result.add(events[0].dateStamp)
        result.add(events[0].beginDateObject)

        for (i in 0..events.size - 1 - 1) {
            val current = events[i]

            result.add(current)

            val next = events[i + 1]
            if (current.date != next.date) {
                result.add(next.dateStamp)
            }

            if (current.begin != next.begin) {
                result.add(next.beginDateObject)
            }
        }

        result.add(events[events.size - 1])

        return result
    }

    protected val contentView: Int
        get() = R.layout.fragment_schedule

    fun showFilters() {
        val filter = App.storage.filter

        val view = FilterView(context, filter)
        MaterialAlert.create(context).setTitle(getString(R.string.alert_filter_title)).setView(view).setBasicNegativeButton().setPositiveButton(R.string.save) { _, _ ->
            val filter = view.save()
            App.application.analyticsController.tagFiltersEvent(filter)
            refreshContents()
        }.show()
    }


    override fun onRefresh() {
        val retrofit = Retrofit.Builder().baseUrl(Constants.API_URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        val service = retrofit.create(DatabaseService::class.java)

        val syncRepository = SyncRepository(service)
        syncRepository.getSchedule()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Logger.d("OnSuccess")
                            val rowsUpdated = App.application.databaseController.updateSchedule(response = it.syncResponse)
                            swipe_refresh!!.isRefreshing = false

                            if( rowsUpdated == 0 )
                                Toast.makeText(context, "Up to date!", Toast.LENGTH_SHORT).show()
                            else if (rowsUpdated > 0) {
                                App.application.postBusEvent(SyncResponseEvent(rowsUpdated))
                                App.application.notificationHelper.scheduleUpdateNotification(rowsUpdated)
                            }
                        },
                        {
                            Logger.d("OnError " + it.message)
                            swipe_refresh!!.isRefreshing = false
                            Toast.makeText(context, "Unable to sync.", Toast.LENGTH_SHORT).show()

                        })

    }

    companion object {

        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }
}