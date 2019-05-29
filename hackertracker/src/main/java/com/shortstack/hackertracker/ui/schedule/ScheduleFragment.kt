package com.shortstack.hackertracker.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.view_empty.view.*
import org.koin.android.ext.android.inject

class ScheduleFragment : Fragment() {

    private val adapter: ScheduleAdapter = ScheduleAdapter()

    private val timer: TickTimer by inject()

    private var disposable: Disposable? = null

    private var shouldScroll = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shouldScroll = true
        list.adapter = adapter

        val scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        scheduleViewModel.schedule.observe(this, Observer {
            hideViews()

            if (it != null) {
                adapter.state = it.status

                when (it.status) {
                    Status.SUCCESS -> {
                        val list = adapter.setSchedule(it.data)
                        if (adapter.isEmpty()) {
                            showEmptyView()
                        }

                        scrollToCurrentPosition(list)
                    }
                    Status.ERROR -> {
                        showErrorView(it.message)
                    }
                    Status.LOADING -> {
                        adapter.clearAndNotify()
                        showProgress()
                    }
                    Status.NOT_INITIALIZED -> {
                        showEmptyView()
                    }
                }
            }
        })
    }

    private fun scrollToCurrentPosition(data: ArrayList<Any>) {
        val manager = list.layoutManager ?: return
        val first = data.filterIsInstance<Event>().firstOrNull { !it.hasFinished } ?: return

        if (shouldScroll) {
            shouldScroll = false
            val index = getScrollIndex(data, first)
            manager.scrollToPosition(index)
        }
    }

    private fun getScrollIndex(data: ArrayList<Any>, first: Event): Int {
        val event = data.indexOf(first)
        val index = data.indexOf(data.subList(0, event).filterIsInstance<Time>().last())
        if (index > 1) {
            if (data[index - 1] is Day) {
                return index - 1
            }
            return index
        }
        return event
    }


    override fun onResume() {
        super.onResume()

        disposable = timer.observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // adapter.notifyTimeChanged()
                    if (adapter.isEmpty()) {
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

    private fun hideViews() {
        empty.visibility = View.GONE
        loading_progress.visibility = View.GONE
    }

    private fun showEmptyView() {
        empty.visibility = View.VISIBLE
    }

    private fun showErrorView(message: String?) {
        empty.title.text = message
        empty.visibility = View.VISIBLE
    }

    companion object {

        fun newInstance() = ScheduleFragment()

    }
}