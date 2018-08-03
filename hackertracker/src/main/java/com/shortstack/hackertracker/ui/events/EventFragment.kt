package com.shortstack.hackertracker.ui.events

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Speaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utils.TimeUtil
import com.shortstack.hackertracker.views.EventView
import com.shortstack.hackertracker.views.SpeakerView
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_event.*
import javax.inject.Inject

/**
 * Created by Chris on 7/31/2018.
 */
class EventFragment : Fragment() {

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: DatabaseEvent): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }

    @Inject
    lateinit var database: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        App.application.component.inject(this)

        val event = arguments?.getParcelable(EXTRA_EVENT) as? DatabaseEvent


        val drawable = ContextCompat.getDrawable(context
                ?: return, R.drawable.ic_arrow_back_white_24dp)
        toolbar.navigationIcon = drawable

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }


        event?.let {

            collapsing_toolbar.title = it.event.title

            val body = it.event.description

            if (body.isNotBlank()) {
                empty.visibility = View.GONE
                description.text = body
            } else {
                empty.visibility = View.VISIBLE
            }

            displayDescription(it)

            displayTypes(it)

            val speakers = displaySpeakers(it)
            displayRelatedEvents(it, speakers)
        }
    }

    private fun displayDescription(event: DatabaseEvent) {

        val context = context ?: return

        collapsing_toolbar.subtitle = getFullTimeStamp(context, event)
        location.text = event.location.first().name
    }


    fun getFullTimeStamp(context: Context, event: DatabaseEvent): String {
        val (begin, end) = getTimeStamp(context, event)
        val timestamp = TimeUtil.getRelativeDateStamp(context, event.event.begin)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }

    fun getTimeStamp(context: Context, event: DatabaseEvent): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.event.begin)
        val end = TimeUtil.getTimeStamp(context, event.event.end)
        return Pair(begin, end)
    }


    private fun displayTypes(event: DatabaseEvent) {
        val type = event.type.firstOrNull()

        type?.let {
            val color = Color.parseColor(type.color)
            app_bar.setBackgroundColor(color)

            val context = context ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
                drawable?.setTint(color)
                category_text.background = drawable
            }

            category_text.text = it.name

            activity?.window?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                    statusBarColor = color
                }
            }
        }
    }

    private fun displaySpeakers(event: DatabaseEvent): List<Speaker> {
        val context = context ?: return emptyList()

        val speakersForEvent = database.getSpeakers(event.id)
        if (speakersForEvent.isNotEmpty()) {
            speakers_header.visibility = View.VISIBLE
            speakersForEvent.forEach {
                speakers.addView(SpeakerView(context, it), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }
        } else {
            speakers_header.visibility = View.GONE
        }
        return speakersForEvent
    }


    private fun displayRelatedEvents(event: DatabaseEvent, speakers: List<Speaker>) {
        val context = context ?: return

        val relatedEvents = database.getRelatedEvents(event.id, event.type, speakers)

        if (relatedEvents.isNotEmpty()) {
            related_events_header.visibility = View.VISIBLE
            relatedEvents.forEach {
                related_events.addView(EventView(context, it))
            }
        } else {
            related_events_header.visibility = View.GONE
        }
    }

}