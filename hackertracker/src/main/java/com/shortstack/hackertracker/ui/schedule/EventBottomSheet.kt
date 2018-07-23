package com.shortstack.hackertracker.ui.schedule

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.utils.MaterialAlert
import com.shortstack.hackertracker.views.EventView
import com.shortstack.hackertracker.views.SpeakerView
import kotlinx.android.synthetic.main.bottom_sheet_schedule_event.view.*
import kotlinx.android.synthetic.main.empty_text.view.*
import kotlinx.android.synthetic.main.row_event.view.*
import javax.inject.Inject

class EventBottomSheet : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    @Inject
    lateinit var analytics: AnalyticsController

    @Inject
    lateinit var database: DatabaseManager

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        App.application.component.inject(this)

        val view = View.inflate(context, R.layout.bottom_sheet_schedule_event, null)
        dialog.setContentView(view)

        val obj = EventViewModel(content)

        analytics.onEventAction(AnalyticsController.EVENT_VIEW, content.event)

        view.event.setContent(obj.event)

        displaySpeakers(obj, view.speakers, view.related_events)

        displayDescription(obj, view.description, view.empty, view.link, view.star)

        view.star.setOnClickListener { onStarClick(view.event, view.star) }
        view.share.setOnClickListener { onShareClick(view.event) }
        view.link.setOnClickListener { onLinkClick() }


    }


    private fun displaySpeakers(obj: EventViewModel, layout: LinearLayoutCompat, related_events: LinearLayout) {
        val speakers = database.getSpeakers(obj.event.event.id)


        val context = context ?: return

        speakers.forEach {
            layout.addView(SpeakerView(context, it))
        }

        val events = database.getRelatedEvents(speakers.first())

        events.forEach {
            val view = LayoutInflater.from(context).inflate(R.layout.row_event, related_events, false)
            view.title.text = it.event.title

            related_events.addView(view)
        }
    }

    private val content: DatabaseEvent
        get() = arguments!!.getParcelable(ARG_EVENT)

    private fun displayDescription(obj: EventViewModel, description: TextView, empty: View, link: View, star: ImageView) {
        val hasDescription = obj.hasDescription()

        if (hasDescription)
            description.text = obj.description
        empty.visibility = if (hasDescription) View.GONE else View.VISIBLE

        link.visibility = if (obj.hasUrl()) View.VISIBLE else View.GONE

        updateStarIcon(star)
    }

    private fun updateStarIcon(star: ImageView) {
        star.setImageDrawable(resources.getDrawable(if (content.event.isBookmarked) R.drawable.ic_star_accent_24dp else R.drawable.ic_star_border_white_24dp))
    }

    fun onStarClick(item: EventView, star: ImageView) {
        if (content.event.isBookmarked) {
            analytics.onEventAction(AnalyticsController.EVENT_UNBOOKMARK, content.event)
        } else {
            analytics.onEventAction(AnalyticsController.EVENT_BOOKMARK, content.event)
        }
        item.onBookmarkClick()
        updateStarIcon(star)
    }

    private fun onShareClick(item: EventView) {
        analytics.onEventAction(AnalyticsController.EVENT_SHARE, content.event)
        item.onShareClick()
    }

    private fun onLinkClick() {
        val context = context ?: return

        analytics.onEventAction(AnalyticsController.EVENT_OPEN_URL, content.event)

        MaterialAlert.create(context)
                .setTitle(R.string.link_warning)
                .setMessage(String.format(context.getString(R.string.link_message), content.event.url?.toLowerCase()))
                .setPositiveButton(R.string.open_link, DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content.event.url))
                    context.startActivity(intent)
                }).setBasicNegativeButton()
                .show()
    }

    companion object {

        private const val ARG_EVENT = "ARG_EVENT"

        fun newInstance(obj: DatabaseEvent): EventBottomSheet {
            val fragment = EventBottomSheet()

            val bundle = Bundle()
            bundle.putParcelable(ARG_EVENT, obj)
            fragment.arguments = bundle

            return fragment
        }
    }
}
