package com.shortstack.hackertracker.ui.schedule

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.utils.MaterialAlert
import com.shortstack.hackertracker.views.EventView
import kotlinx.android.synthetic.main.bottom_sheet_schedule_event.view.*
import kotlinx.android.synthetic.main.empty_text.view.*
import javax.inject.Inject

class EventBottomSheet : android.support.design.widget.BottomSheetDialogFragment() {

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

        view.event.setEvent(obj.event)

        displaySpeakers(obj, view.speakers)

        displayDescription(obj, view.description, view.empty, view.link, view.star)

        view.star.setOnClickListener { onStarClick(view.event, view.star) }
        view.share.setOnClickListener { onShareClick(view.event) }
        view.link.setOnClickListener { onLinkClick() }

        view.tool.visibility = obj.toolsVisibility
        view.exploit.visibility = obj.exploitVisibility
        view.demo.visibility = obj.demoVisibility

    }


    private fun displaySpeakers(obj: EventViewModel, speakers: LinearLayoutCompat) {
//        val context = context ?: return
//
//        obj.speakers?.iterator()?.forEach {
//            speakers.addView(SpeakerView(context, database.getSpeaker(it)))
//        }
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
        star.setImageDrawable(resources.getDrawable(if (content.event.isBookmarked) R.drawable.ic_star_white_24dp else R.drawable.ic_star_border_white_24dp))
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
