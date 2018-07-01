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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.State
import androidx.work.WorkManager
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.network.task.SyncWorker
import com.shortstack.hackertracker.ui.schedule.list.ListViewsInterface
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.view_empty.view.*
import javax.inject.Inject


class ScheduleFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, ListViewsInterface {

    private val adapter: ScheduleAdapter = ScheduleAdapter()

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var timer: TickTimer

    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.application.component.inject(this)

        swipe_refresh.setOnRefreshListener(this)
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        list.adapter = adapter

        val scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        scheduleViewModel.schedule.observe(this, Observer {
            val resource = it

            hideViews()

            when (resource?.status) {
                Status.SUCCESS -> {
                    if (resource.data!!.isEmpty()) {
                        adapter.clearAndNotify()
                        showEmptyView()
                    } else {
                        adapter.clearAndNotify()
                        adapter.addAllAndNotify(resource.data)
                        hideViews()
                    }
                }
                Status.ERROR -> {
                    showErrorView(resource.message)
                }
                Status.LOADING -> {
                    adapter.clearAndNotify()
                    showProgress()
                }
                Status.NOT_INITIALIZED -> {
                    showEmptyView()
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()

        disposable = timer.observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.notifyTimeChanged()
                    if (adapter.collection.isEmpty()) {
                        showEmptyView()
                    } else {
                        hideViews()
                    }
                }
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    private fun showProgress() {
        loading_progress.visibility = View.VISIBLE
    }

    override fun hideViews() {
        empty.visibility = View.GONE
        loading_progress.visibility = View.GONE
    }

    override fun showEmptyView() {
        empty.visibility = View.VISIBLE
    }

    override fun showErrorView(message: String?) {
        empty.title.text = message
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