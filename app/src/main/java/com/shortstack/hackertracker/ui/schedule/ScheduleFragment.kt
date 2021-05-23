package com.shortstack.hackertracker.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.advice.timehop.StickyRecyclerHeadersDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.databinding.FragmentScheduleBinding
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.schedule.list.ScheduleAdapter
import com.shortstack.hackertracker.views.DaySelectorView
import java.util.*


class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val adapter: ScheduleAdapter = ScheduleAdapter()

    private var shouldScroll = true

    private lateinit var bottomSheet: BottomSheetBehavior<View>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getParcelable<Type>(EXTRA_TYPE)
        if (type != null) {
            binding.toolbar.title = type.shortName
            binding.filter.visibility = View.GONE
        }

        binding.toolbar.inflateMenu(R.menu.schedule)
        binding.toolbar.setOnMenuItemClickListener {
            if (it?.itemId == R.id.search) {
                (context as MainActivity).showSearch()
                return@setOnMenuItemClickListener true
            }
            false
        }

        shouldScroll = true
        binding.list.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }


        val decoration = StickyRecyclerHeadersDecoration(adapter)
        binding.list.addItemDecoration(decoration)

        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = binding.list.layoutManager as? LinearLayoutManager
                if (manager != null) {
                    val first = manager.findFirstVisibleItemPosition()
                    val last = manager.findLastVisibleItemPosition()

                    if (first == -1 || last == -1)
                        return

                    binding.daySelector.onScroll(
                        adapter.getDateOfPosition(first),
                        adapter.getDateOfPosition(last)
                    )
                }
            }
        })

        binding.daySelector.addOnDaySelectedListener(object :
            DaySelectorView.OnDaySelectedListener {
            override fun onDaySelected(day: Date) {
                scrollToDate(day)
            }
        })


        val scheduleViewModel =
            ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]
        scheduleViewModel.schedule.observe(viewLifecycleOwner, Observer {
            hideViews()

            if (it != null) {
                adapter.state = it.status

                when (it.status) {
                    Status.SUCCESS -> {
                        val list = adapter.setSchedule(it.data)
                        val days = list.filterIsInstance<Day>()
                        binding.daySelector.setDays(days)

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

        scheduleViewModel.types.observe(viewLifecycleOwner, Observer {
            binding.filters.setTypes(it.data)
        })


        bottomSheet = BottomSheetBehavior.from(binding.filters)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        binding.filter.setOnClickListener { expandFilters() }
        // todo: binding.filters.close.setOnClickListener { hideFilters() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> (context as MainActivity).showSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun scrollToCurrentPosition(data: ArrayList<Any>) {
        val manager = binding.list.layoutManager ?: return
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

        val x = data.subList(index, event).filterIsInstance<Event>()
            .firstOrNull { it.start.time != first.start.time } == null
        if (!x) {
            return event
        }


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
            binding.list.layoutManager?.startSmoothScroll(scroller)
        }
    }

    private fun showProgress() {
        binding.loadingProgress.visibility = View.VISIBLE
    }

    private fun hideViews() {
        binding.empty.visibility = View.GONE
        binding.loadingProgress.visibility = View.GONE
    }

    private fun showEmptyView() {
        binding.empty.visibility = View.VISIBLE
    }

    private fun showErrorView(message: String?) {
        binding.empty.showError(message)
        binding.empty.visibility = View.VISIBLE
    }

    private fun expandFilters() {
        bottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun hideFilters() {
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }


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
}