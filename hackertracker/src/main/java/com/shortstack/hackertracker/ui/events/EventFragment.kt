package com.shortstack.hackertracker.ui.events

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.TimeUtil
import com.shortstack.hackertracker.views.SpeakerView
import com.shortstack.hackertracker.views.StatusBarSpacer
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_event.*
import org.koin.android.ext.android.inject

class EventFragment : Fragment() {

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: Event): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val database: DatabaseManager by inject()
    private val analytics: Analytics by inject()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val event = arguments?.getParcelable(EXTRA_EVENT) as? Event


        val drawable = ContextCompat.getDrawable(context
                ?: return, R.drawable.ic_arrow_back_white_24dp)
        toolbar.navigationIcon = drawable

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val context = context ?: return
            val height = StatusBarSpacer.getStatusBarHeight(context, app_bar)
            app_bar.setPadding(0, height, 0, 0)
        }

        event?.let { event ->

            analytics.log("Viewing event ${event.title}")

            collapsing_toolbar.title = event.title

            val body = event.description

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

            displayDescription(event)

            displayTypes(event)

            displayBookmark(event)


            val speakers = displaySpeakers(event)
//            displayRelatedEvents(it, speakers)

            analytics.onEventAction(Analytics.EVENT_VIEW, event)
        }
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
        val action = if (event.isBookmarked) Analytics.EVENT_BOOKMARK else Analytics.EVENT_UNBOOKMARK
        analytics.onEventAction(action, event)

        displayBookmark(event)
    }

    private fun getDetailsDescription(event: Event): String {
//        val context = context ?:
        return ""

//        return "Attending ${event.title} at ${getFullTimeStamp(context, event)} in ${event.location.firstOrNull()?.name} #hackertracker"
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

        if (isBookmarked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            event.type.let {
                val color = Color.parseColor(it.color)
                image?.setTint(color)
            }
        }

        star.setImageDrawable(image)
    }

    private fun displayDescription(event: Event) {

        val context = context ?: return

        collapsing_toolbar.subtitle = getFullTimeStamp(context, event)
        location.text = event.location.name
    }


    fun getFullTimeStamp(context: Context, event: Event): String {
        val (begin, end) = getTimeStamp(context, event)
        val timestamp = TimeUtil.getDateStamp(event.start)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }

    fun getTimeStamp(context: Context, event: Event): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.start)
        val end = TimeUtil.getTimeStamp(context, event.end)
        return Pair(begin, end)
    }


    private fun displayTypes(event: Event) {

        val type = event.type
        val context = context ?: return

        val color = Color.parseColor(type.color)
        app_bar.setBackgroundColor(color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
            drawable?.setTint(color)
            category_dot.background = drawable
        }

        category_text.text = type.name

    }

    private fun displaySpeakers(event: Event) {
        val context = context ?: return

        val list = event.speakers

        if (list.isEmpty()) {
            speakers_header.visibility = View.GONE
        } else {
            speakers_header.visibility = View.VISIBLE

            list.forEach { speaker ->
                speakers.addView(SpeakerView(context, speaker), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }
        }
    }


    private fun displayRelatedEvents(event: Event, speakers: List<Speaker>) {
        val context = context ?: return

//        val relatedEvents = database.getRelatedEvents(event.id, event.types, speakers)
//
//        if (relatedEvents.isNotEmpty()) {
//            related_events_header.visibility = View.VISIBLE
//            relatedEvents.forEach {
//                related_events.addView(EventView(context, it))
//            }
//        } else {
        related_events_header.visibility = View.GONE
//        }
    }
}