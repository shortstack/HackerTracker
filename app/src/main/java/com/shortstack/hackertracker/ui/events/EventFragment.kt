package com.shortstack.hackertracker.ui.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.database.ReminderManager
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.utilities.TimeUtil
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_event.*
import org.koin.android.ext.android.inject
import kotlin.math.max


class EventFragment : Fragment() {

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: Int): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putInt(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val analytics: Analytics by inject()
    private val database: DatabaseManager by inject()
    private val reminder: ReminderManager by inject()

    private val viewModel: HackerTrackerViewModel by lazy { ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java] }

    private val adapter = EventDetailsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments?.getInt(EXTRA_EVENT)

        viewModel.events.observe(viewLifecycleOwner, Observer {
            val target = it.data?.find { it.id == id }
            if (target != null) {
                showEvent(target)
            }
        })

        val drawable = ContextCompat.getDrawable(
            context
                ?: return, R.drawable.ic_arrow_back_white_24dp
        )
        toolbar.navigationIcon = drawable

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }

    }

    private fun showEvent(event: Event) {
        analytics.log("Viewing event: ${event.title}")

        collapsing_toolbar.title = event.title

        val body = event.description

        contents.adapter = adapter
        val gridLayoutManager = contents.layoutManager as GridLayoutManager

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
            empty.visibility = View.GONE
            description.text = body
        } else {
            empty.visibility = View.VISIBLE
        }

        toolbar.setOnMenuItemClickListener {
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

        toolbar.invalidate()
    }

    private fun displayBookmark(event: Event) {
        val isBookmarked = event.isBookmarked
        toolbar.menu.clear()
        toolbar.inflateMenu(if (isBookmarked) R.menu.event_bookmarked else R.menu.event_unbookmarked)
    }

    private fun displayDescription(event: Event) {

        val context = context ?: return

        collapsing_toolbar.subtitle = getFullTimeStamp(context, event)
        location.text = event.location.name
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
        type_1.render(type)
        type_1.visibility = View.VISIBLE

        if (event.types.size > 1) {
            type_2.render(event.types.last())
            type_2.visibility = View.VISIBLE
        } else {
            type_2.visibility = View.GONE
        }
    }
}