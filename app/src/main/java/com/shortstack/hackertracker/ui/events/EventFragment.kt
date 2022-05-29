package com.shortstack.hackertracker.ui.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.database.ReminderManager
import com.shortstack.hackertracker.databinding.FragmentEventBinding
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.utilities.TimeUtil
import org.koin.android.ext.android.inject
import kotlin.math.max


class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private val analytics: Analytics by inject()
    private val database: DatabaseManager by inject()
    private val reminder: ReminderManager by inject()

    private val viewModel: HackerTrackerViewModel by lazy { ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java] }

    private val adapter = EventDetailsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments?.getLong(EXTRA_EVENT) ?: error("id must not be null")

        viewModel.events.observe(viewLifecycleOwner) {
            val target = it.data?.find { it.id == id }
            if (target != null) {
                showEvent(target)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showEvent(event: Event) {
        analytics.log("Viewing event: ${event.title}")

        binding.toolbar.title = event.title

        val body = event.description

        binding.contents.adapter = adapter
        val gridLayoutManager = binding.contents.layoutManager as GridLayoutManager

        val displayMetrics = requireContext().resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        gridLayoutManager.spanCount = max(2f, dpWidth / 200f).toInt()
        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return adapter.getSpanSize(position, gridLayoutManager.spanCount)
                }
            }
        adapter.setElements(event.urls.sortedBy { it.label.length }, event.speakers)

        if (body.isNotBlank()) {
            // todo: binding.empty.visibility = View.GONE
            binding.description.text = body
        } else {
            // todo: binding.empty.visibility = View.VISIBLE
        }

        binding.toolbar.setOnMenuItemClickListener {
            onBookmarkClick(event)
            true
        }

        displayDescription(event)

        displayTypes(event)

        displayBookmark(event)


        analytics.onEventAction(Analytics.EVENT_VIEW, event)
    }

    private fun onBookmarkClick(event: Event) {
        event.isBookmarked = !event.isBookmarked

        database.updateBookmark(event)
        if (event.isBookmarked) {
            reminder.setReminder(event)
        } else {
            reminder.cancel(event)
        }

        val action =
            if (event.isBookmarked) Analytics.EVENT_BOOKMARK else Analytics.EVENT_UNBOOKMARK
        analytics.onEventAction(action, event)

        binding.toolbar.invalidate()
    }

    private fun displayBookmark(event: Event) {
        val isBookmarked = event.isBookmarked
        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(if (isBookmarked) R.menu.event_bookmarked else R.menu.event_unbookmarked)
    }

    private fun displayDescription(event: Event) {

        val context = context ?: return

        binding.toolbar.subtitle = getFullTimeStamp(context, event)
        binding.location.text = event.location.name
    }


    private fun getFullTimeStamp(context: Context, event: Event): String {
        val (begin, end) = getTimeStamp(context, event)
        val timestamp = TimeUtil.getDateStamp(event.start)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }

    private fun getTimeStamp(context: Context, event: Event): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.start)
        val end = TimeUtil.getTimeStamp(context, event.end)
        return Pair(begin, end)
    }


    private fun displayTypes(event: Event) {
        val type = event.types.first()
        binding.type1.render(type)
        binding.type1.visibility = View.VISIBLE

        if (event.types.size > 1) {
            binding.type2.render(event.types.last())
            binding.type2.visibility = View.VISIBLE
        } else {
            binding.type2.visibility = View.GONE
        }
    }

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: Long): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putLong(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }
}