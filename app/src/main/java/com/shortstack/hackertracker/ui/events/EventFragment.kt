package com.shortstack.hackertracker.ui.events

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
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
        analytics.log("Viewing event ${event.title}")

        collapsing_toolbar.title = event.title

        val body = event.description

        contents.adapter = adapter
        val gridLayoutManager = contents.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return adapter.getSpanSize(position, gridLayoutManager.spanCount)
                }
            }
        adapter.setElements(listOf("Links") + event.urls + "Speakers" + event.speakers)



        if (body.isNotBlank()) {
            empty.visibility = View.GONE
            description.text = body
        } else {
            empty.visibility = View.VISIBLE
        }

        val url = event.link
        if (url.isBlank()) {
            link.visibility = View.GONE
        } else {
            link.visibility = View.VISIBLE

            link.setOnClickListener {
                onLinkClick(url)
                analytics.onEventAction(Analytics.EVENT_OPEN_URL, event)
            }
        }

        share.setOnClickListener {
            onShareClick(event)
            analytics.onEventAction(Analytics.EVENT_SHARE, event)
        }

        star.setOnClickListener {
            onBookmarkClick(event)
        }

        if (event.location.hotel == null) {
            map.visibility = View.GONE
        } else {
            map.visibility = View.VISIBLE
            map.setOnClickListener {
                onMapClick(event)
            }
        }

        displayDescription(event)

        displayTypes(event)

        displayBookmark(event)


        analytics.onEventAction(Analytics.EVENT_VIEW, event)
    }

    private fun onMapClick(event: Event) {
        (context as? MainActivity)?.showMap(event.location)
    }

    private fun onLinkClick(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
        context?.startActivity(intent)
    }

    private fun onShareClick(event: Event) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, getDetailsDescription(event))
        intent.type = "text/plain"

        context?.startActivity(intent)
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

        displayBookmark(event)
    }

    private fun getDetailsDescription(event: Event): String {
        val context = context ?: return ""
        return "Attending ${event.title} at ${getFullTimeStamp(
            context,
            event
        )} in ${event.location.name} #hackertracker"
    }

    private fun displayBookmark(event: Event) {

        val context = context ?: return

        val isBookmarked = event.isBookmarked
        val drawable = if (isBookmarked) {
            R.drawable.ic_star_accent_24dp
        } else {
            R.drawable.ic_star_border_white_24dp
        }

        val image = ContextCompat.getDrawable(context, drawable)?.mutate()

        star.setImageDrawable(image)
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
        val context = context ?: return

        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.category_tint, value, true)
        val id = value.resourceId

        val color = if (id > 0) {
            ContextCompat.getColor(context, id)
        } else {
            Color.parseColor(type.color)
        }

        val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
        drawable?.setTint(color)
        category_dot.background = drawable

        category_text.text = type.name
    }
}