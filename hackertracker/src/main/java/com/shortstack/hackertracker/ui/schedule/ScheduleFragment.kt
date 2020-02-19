package com.shortstack.hackertracker.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.DayIndicator
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import com.shortstack.hackertracker.views.BubbleDecoration
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.list
import kotlinx.android.synthetic.main.view_empty.view.*
import kotlinx.android.synthetic.main.view_filter.*
import java.util.*
import kotlin.collections.ArrayList


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

    private var days = emptyList<Day>()
    private var cachedBubbleRange: IntRange? = null

    private lateinit var dayIndicatorItemDecoration: BubbleDecoration
    private lateinit var dayIndicatorAdapter: DayIndicatorAdapter

    private val scheduleAdapter: ScheduleAdapter = ScheduleAdapter()

    private var shouldScroll = true

    private lateinit var bottomSheet: BottomSheetBehavior<View>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false) as ViewGroup
    }

    var temp = Random().nextLong()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        val type = arguments?.getParcelable<Type>(EXTRA_TYPE)
        if (type != null) {
            toolbar.title = type.name
            filter.visibility = View.GONE
        }

        toolbar.inflateMenu(R.menu.schedule)
        toolbar.setOnMenuItemClickListener {
            if (it?.itemId == R.id.search) {
                (context as MainActivity).showSearch()
                true
            }
            false
        }


        list.apply {
            adapter = scheduleAdapter
            (itemAnimator as DefaultItemAnimator).run {
                supportsChangeAnimations = false
                addDuration = 160L
                moveDuration = 160L
                changeDuration = 160L
                removeDuration = 120L
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    onScheduleScrolled()
                }
            })
        }

        dayIndicatorItemDecoration = BubbleDecoration(view.context)
        day_selector.addItemDecoration(dayIndicatorItemDecoration)

        dayIndicatorAdapter = DayIndicatorAdapter()
        day_selector.adapter = dayIndicatorAdapter


        day_selector.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        shouldScroll = true

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }


        val decoration = StickyRecyclerHeadersDecoration(scheduleAdapter)
        list.addItemDecoration(decoration)


        val factory = ScheduleViewModelFactory(type)

        val scheduleViewModel = ViewModelProviders.of(this, factory).get(ScheduleViewModel::class.java)
        scheduleViewModel.schedule.observe(this, Observer {
            hideViews()

            if (it != null) {
                scheduleAdapter.state = it.status

                when (it.status) {
                    Status.SUCCESS -> {
                        val list = scheduleAdapter.setSchedule(it.data)

                        
                        days = list.filterIsInstance<Day>()
                        rebuildDayIndicators()


                        if (scheduleAdapter.isEmpty()) {
                            showEmptyView()
                        }

                        scrollToCurrentPosition(list)
                    }
                    Status.ERROR -> {
                        showErrorView(it.message)
                    }
                    Status.LOADING -> {
                        scheduleAdapter.clearAndNotify()
                        showProgress()
                    }
                    Status.NOT_INITIALIZED -> {
                        showEmptyView()
                    }
                }
            }
        })

        scheduleViewModel.types.observe(this, Observer {
            filters.setTypes(it)
        })


        bottomSheet = BottomSheetBehavior.from(filters)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        filter.setOnClickListener { expandFilters() }
        close.setOnClickListener { hideFilters() }

        ViewCompat.setTranslationZ(filters, 10f)
    }

    fun onScheduleScrolled() {
        val layoutManager = (list.layoutManager) as LinearLayoutManager
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()
        if (first < 0 || last < 0) {
            // When the list is empty, we get -1 for the positions.
            return
        }

        val highlightRange = dayIndicatorAdapter.getRange(scheduleAdapter.getDateOfPosition(first), scheduleAdapter.getDateOfPosition(last))

        if (highlightRange != cachedBubbleRange) {
            cachedBubbleRange = highlightRange
            rebuildDayIndicators()
        }
    }

    private fun rebuildDayIndicators() {
        // cachedBubbleRange will get set once we have scroll information, so wait until then.
        val bubbleRange = cachedBubbleRange ?: return
        val indicators = days.mapIndexed { index: Int, day: Day ->
                DayIndicator(day = day, checked = index in bubbleRange)
            }


        dayIndicatorAdapter.submitList(indicators)
        dayIndicatorItemDecoration.bubbleRange = bubbleRange
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search -> (context as MainActivity).showSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun scrollToCurrentPosition(data: ArrayList<Any>) {
        val manager = list.layoutManager ?: return
        val first = data.filterIsInstance<Event>().firstOrNull { !it.hasStarted } ?: return

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

        val x = data.subList(index, event).filterIsInstance<Event>().firstOrNull { it.start.time != first.start.time } == null
        if (!x) {
            return event
        }


        if (index != -1) {
            return index
        }
        return event
    }

    private fun scrollToDate(date: Date) {
        val index = scheduleAdapter.getDatePosition(date)
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

    private fun expandFilters() {
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideFilters() {
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onResume() {
        super.onResume()
        Logger.e("ScheduleFragment Resumed $temp")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.e("ScheduleFragment Destroyed $temp")
    }
}