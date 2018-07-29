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

        displaySpeakers(obj, view.speakers_header, view.speakers)
        displayRelatedEvents(obj, view.related_events)

        displayDescription(obj, view.description, view.empty, view.link)

        view.share.setOnClickListener { onShareClick(view.event) }
        view.link.setOnClickListener { onLinkClick() }


    }

    private fun displayRelatedEvents(obj: EventViewModel, related_events: LinearLayout) {
        val speakers = database.getSpeakers(obj.event.event.id)

        val events = database.getRelatedEvents(obj.id, obj.event.type, speakers)

        val context = context ?: return

        events.forEach {
            related_events.addView(EventView(context, it))
        }
    }

    private fun displaySpeakers(obj: EventViewModel, header: View, layout: LinearLayoutCompat) {
        val speakers = database.getSpeakers(obj.event.event.id)


        val context = context ?: return

        if (speakers.isEmpty()) {
            header.visibility = View.GONE
            layout.visibility = View.GONE
        } else {
            header.visibility = View.VISIBLE
            layout.visibility = View.VISIBLE
            speakers.forEach {
                layout.addView(SpeakerView(context, it))
            }
        }
    }


    private val content: DatabaseEvent
        get() = arguments!!.getParcelable(ARG_EVENT)

    private fun displayDescription(obj: EventViewModel, description: TextView, empty: View, link: View) {
        val hasDescription = obj.hasDescription()

        if (hasDescription)
            description.text = obj.description
        empty.visibility = if (hasDescription) View.GONE else View.VISIBLE

        link.visibility = if (obj.hasUrl()) View.VISIBLE else View.GONE
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
