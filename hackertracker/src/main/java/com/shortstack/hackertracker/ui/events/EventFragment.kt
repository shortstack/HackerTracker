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
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utils.TimeUtil
import com.shortstack.hackertracker.views.SpeakerView
import com.shortstack.hackertracker.views.StatusBarSpacer
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_event.*
import org.koin.android.ext.android.inject

class EventFragment : Fragment() {

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: FirebaseEvent): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val database: DatabaseManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val event = arguments?.getParcelable(EXTRA_EVENT) as? FirebaseEvent


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

            AnalyticsController.log("Viewing event ${event.title}")

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
                    AnalyticsController.onEventAction(AnalyticsController.EVENT_OPEN_URL, event)
                }
            }

            share.setOnClickListener {
                onShareClick(event)
                AnalyticsController.onEventAction(AnalyticsController.EVENT_SHARE, event)
            }

            star.setOnClickListener {
                onBookmarkClick(event)
            }

            displayDescription(event)

            displayTypes(event)

            displayBookmark(event)


            val speakers = displaySpeakers(event)
//            displayRelatedEvents(it, speakers)

            AnalyticsController.onEventAction(AnalyticsController.EVENT_VIEW, event)
        }
    }

    private fun onLinkClick(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
        context?.startActivity(intent)
    }

    private fun onShareClick(event: FirebaseEvent) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, getDetailsDescription(event))
        intent.type = "text/plain"

        context?.startActivity(intent)
    }

    private fun onBookmarkClick(event: FirebaseEvent) {
//        event.event.isBookmarked = !event.isBookmarked

//        displayBookmark(event)
//
//        Single.fromCallable {
//            database.updateBookmark(event.event)
//        }.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe()

    }

    private fun getDetailsDescription(event: FirebaseEvent): String {
//        val context = context ?:
        return ""

//        return "Attending ${event.title} at ${getFullTimeStamp(context, event)} in ${event.location.firstOrNull()?.name} #hackertracker"
    }

    private fun displayBookmark(event: FirebaseEvent) {

        val context = context ?: return

        val isBookmarked = event.isBookmarked
        val drawable = if (isBookmarked) {
            R.drawable.ic_star_accent_24dp
        } else {
            R.drawable.ic_star_border_white_24dp
        }

        val image = ContextCompat.getDrawable(context, drawable)?.mutate()

        if (isBookmarked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            event.type.firstOrNull()?.let {
//                val color = Color.parseColor(it.color)
//                image?.setTint(color)
//            }
        }

        star.setImageDrawable(image)
    }

    private fun displayDescription(event: FirebaseEvent) {

        val context = context ?: return

        collapsing_toolbar.subtitle = getFullTimeStamp(context, event)
//        location.text = event.location.firstOrNull()?.name ?: "Unknown Location"
    }


    fun getFullTimeStamp(context: Context, event: FirebaseEvent): String {
        val (begin, end) = getTimeStamp(context, event)
        val timestamp = TimeUtil.getRelativeDateStamp(context, event.start)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }

    fun getTimeStamp(context: Context, event: FirebaseEvent): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.start)
        val end = TimeUtil.getTimeStamp(context, event.finish)
        return Pair(begin, end)
    }


    private fun displayTypes(event: FirebaseEvent) {

        val type = database.getTypeForEvent(event)
        val context = context ?: return

        val color = Color.parseColor(type?.color ?: "#FFF")
        app_bar.setBackgroundColor(color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
            drawable?.setTint(color)
            category_text.background = drawable
        }

        category_text.text = type?.name

    }

    private fun displaySpeakers(event: FirebaseEvent) {
        val context = context ?: return

        val list = database.getSpeakers(event)

        if (list.isEmpty()) {
            speakers_header.visibility = View.GONE
        } else {
            speakers_header.visibility = View.VISIBLE

            list.forEach { speaker ->
                speakers.addView(SpeakerView(context, speaker), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }
        }
    }


    private fun displayRelatedEvents(event: FirebaseEvent, speakers: List<FirebaseSpeaker>) {
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