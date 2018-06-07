package com.shortstack.hackertracker.ui.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.State
import androidx.work.WorkManager
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.events.BusProvider
import com.shortstack.hackertracker.events.RefreshTimerEvent
import com.shortstack.hackertracker.network.task.SyncWorker
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.ui.schedule.list.ListViewsInterface
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ScheduleFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, ListViewsInterface {

    lateinit var adapter: ScheduleAdapter

    private var subscription: Disposable? = null

    @Inject
    lateinit var database: DatabaseManager

    private val dispatcher: FirebaseJobDispatcher by lazy { FirebaseJobDispatcher(GooglePlayDriver(context)) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup

        rootView.swipe_refresh.setOnRefreshListener(this)
        rootView.swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        adapter = ScheduleAdapter(rootView.list)
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


        App.application.component.inject(this)


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
        val time = currentDate.time

        subscription = Observable.interval(time % Constants.TIMER_INTERVAL, Constants.TIMER_INTERVAL, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onTimeEvent()
                })
    }

    private fun onTimeEvent() {
        BusProvider.bus.post(RefreshTimerEvent())
        adapter.notifyTimeChanged()
    }

    override fun onPause() {
        super.onPause()
        subscription?.dispose()
        subscription = null
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
        val refresh = OneTimeWorkRequestBuilder<SyncWorker>()
                .build()

        val instance = WorkManager.getInstance()
        instance.enqueue(refresh)
        instance.getStatusById(refresh.id).observe(this, Observer {
            when (it?.state) {
                State.SUCCEEDED -> {
                    val rowsUpdated = it.outputData.getInt(SyncWorker.KEY_ROWS_UPDATED, 0)
                    if (rowsUpdated == 0) {
                        Toast.makeText(context, context?.getString(R.string.msg_up_to_date), Toast.LENGTH_SHORT).show()
                    }
                    swipe_refresh.isRefreshing = false
                }
                State.FAILED -> {
                    swipe_refresh.isRefreshing = false
                    Toast.makeText(context, context?.getString(R.string.error_unable_to_sync), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {

        fun newInstance() = ScheduleFragment()

    }
}