package com.shortstack.hackertracker.ui.schedule

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import com.shortstack.hackertracker.views.DaySelectorView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.view_empty.view.*
import java.util.*


class ScheduleFragment : Fragment() {

    companion object {
        private const val EXTRA_TYPE = "type"

        fun newInstance(type: Type? = null): ScheduleFragment {
            val fragment = ScheduleFragment()

            if (type != null) {
                val bundle = Bundle()
                bundle.putParcelable(EXTRA_TYPE, type)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    private val adapter: ScheduleAdapter = ScheduleAdapter()

    private var shouldScroll = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getParcelable<Type>(EXTRA_TYPE)
        if (type != null) {
            toolbar.title = type.name
        }

        shouldScroll = true
        list.adapter = adapter


        val theme = (context as MainActivity).theme
        val outValue = TypedValue()
        theme.resolveAttribute(R.attr.dark_mode, outValue, true)



        if ("dark" == outValue.string) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp)
        }

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }


        val decoration = StickyRecyclerHeadersDecoration(adapter)
        list.addItemDecoration(decoration)

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = list.layoutManager as? LinearLayoutManager
                if (manager != null) {
                    val first = manager.findFirstVisibleItemPosition()
                    val last = manager.findLastVisibleItemPosition()

                    if (first == -1 || last == -1)
                        return

                    day_selector.onScroll(adapter.getDateOfPosition(first), adapter.getDateOfPosition(last))
                }
            }
        })

        day_selector.addOnDaySelectedListener(object : DaySelectorView.OnDaySelectedListener {
            override fun onDaySelected(day: Date) {
                scrollToDate(day)
            }
        })


        val factory = ScheduleViewModelFactory(type)

        val scheduleViewModel = ViewModelProviders.of(this, factory).get(ScheduleViewModel::class.java)
        scheduleViewModel.schedule.observe(this, Observer {
            hideViews()

            if (it != null) {
                adapter.state = it.status

                when (it.status) {
                    Status.SUCCESS -> {
                        val list = adapter.setSchedule(it.data)
                        val days = list.filterIsInstance<Day>()
                        day_selector.setDays(days)

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
        val element = data.subList(0, event).filterIsInstance<Day>().last()
        val index = data.indexOf(element)
        if (index != -1) {
            return index
        }
        return event
    }

    private fun scrollToDate(date: Date) {
        val index = adapter.getDatePosition(date)
        if (index != -1) {
            val scroller = object : LinearSmoothScroller(context) {

                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
            scroller.targetPosition = index
            list.layoutManager?.startSmoothScroll(scroller)
        }
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
}