package com.shortstack.hackertracker.ui.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.events.BusProvider
import com.shortstack.hackertracker.events.RefreshTimerEvent
import com.shortstack.hackertracker.network.FullResponse
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.ui.schedule.list.ListViewsInterface
import com.shortstack.hackertracker.ui.schedule.list.ScheduleItemAdapter
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import java.util.*
import javax.inject.Inject


class ScheduleFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, ListViewsInterface {

    lateinit var adapter: ScheduleItemAdapter

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            BusProvider.bus.post(RefreshTimerEvent())
            adapter?.notifyTimeChanged()
        }
    }
    private var timer: Timer? = null

    @Inject
    lateinit var database: DatabaseManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup

        rootView.swipe_refresh.setOnRefreshListener(this)
        rootView.swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        adapter = ScheduleItemAdapter(rootView.list.layoutManager, rootView.list)
        rootView.list.adapter = adapter

//        rootView.list.addOnScrollListener(object : ScheduleInfiniteScrollListener(layout) {
//            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
//                adapter.load(page)
//            }
//        })

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        App.application.myComponent.inject(this)


        val scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        scheduleViewModel.schedule.observe(this, Observer {
            when {
                it?.isEmpty() == true -> {
                    adapter.clearAndNotify()
                    showEmptyView()
                }
                it?.isNotEmpty() == true -> {
                    Logger.d("Loaded first chunk " + (System.currentTimeMillis() - App.application.timeToLaunch))
                    adapter.clearAndNotify()
                    adapter.addAllAndNotify(it)
                    // TODO: Scroll to current time.
                    hideViews()
                }
                else -> {
                    adapter.clearAndNotify()
                    showErrorView()
                }
            }
        })

        // TODO: Remove, this is only for debugging.
        Logger.d("Created ScheduleFragment " + (System.currentTimeMillis() - App.application.timeToLaunch))
    }

    override fun onResume() {
        super.onResume()

        val currentDate = Date().now()
        var time = currentDate.time

//        if (storage.shouldRefresh(time)) {
        mHandler.obtainMessage(1).sendToTarget()
//        }

        time %= Constants.TIMER_INTERVAL


        timer = Timer().also {
            it.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    mHandler.obtainMessage(1).sendToTarget()
                }
            }, time, Constants.TIMER_INTERVAL)
        }
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer = null
    }

    override fun hideViews() {
        empty.visibility = View.GONE
    }

    override fun showEmptyView() {
        empty.visibility = View.VISIBLE
    }

    override fun showErrorView() {
        empty.visibility = View.VISIBLE
    }

    override fun onRefresh() {
//        val service = DatabaseService.create(database.databaseName)
//
//        val syncRepository = SyncRepository(service)
//        syncRepository.getSchedule()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    onRefreshUpdate(it)
//                }, {
//                    onRefreshError(it)
//                })

    }

    private fun onRefreshError(it: Throwable) {
        swipe_refresh?.isRefreshing = false
        val context = context ?: return

        Toast.makeText(context, context.getString(R.string.error_unable_to_sync), Toast.LENGTH_SHORT).show()

        Logger.e(it, "Could not refresh sync.")
    }

    private fun onRefreshUpdate(it: FullResponse) {
//        database.updateConference(response = it)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { rowsUpdated ->
//                    swipe_refresh?.isRefreshing = false
//
//                    if (rowsUpdated == 0)
//                        Toast.makeText(context, context?.getString(R.string.msg_up_to_date), Toast.LENGTH_SHORT).show()
//                    else if (rowsUpdated > 0) {
//                        BusProvider.bus.post(SyncResponseEvent(rowsUpdated))
//                        notifications.scheduleUpdateNotification(rowsUpdated)
//                        refreshContents()
//                    }
//                }
    }


    companion object {

        fun newInstance() = ScheduleFragment()

    }
}