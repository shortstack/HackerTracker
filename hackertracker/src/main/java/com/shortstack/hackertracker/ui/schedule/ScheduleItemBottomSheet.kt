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
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.ItemViewModel
import com.shortstack.hackertracker.utils.MaterialAlert
import com.shortstack.hackertracker.view.ItemView
import kotlinx.android.synthetic.main.bottom_sheet_schedule_item.view.*
import kotlinx.android.synthetic.main.empty_text.view.*
import javax.inject.Inject

class ScheduleItemBottomSheet : android.support.design.widget.BottomSheetDialogFragment() {

    @Inject
    lateinit var analytics: AnalyticsController

    @Inject
    lateinit var database: DatabaseManager

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        App.application.myComponent.inject(this)

        val view = View.inflate(context, R.layout.bottom_sheet_schedule_item, null)
        dialog.setContentView(view)

        val obj = ItemViewModel(content)

        analytics.tagItemEvent(AnalyticsController.Analytics.EVENT_VIEW, content)

        view.item!!.setItem(obj.item)

        displaySpeakers(obj, view.speakers)

        displayDescription(obj, view.description, view.empty, view.link, view.star)

        view.star.setOnClickListener { onStarClick(view.item, view.star) }
        view.share.setOnClickListener { onShareClick(view.item) }
        view.link.setOnClickListener { onLinkClick() }

        view.tool.visibility = obj.toolsVisibility
        view.exploit.visibility = obj.exploitVisibility
        view.demo.visibility = obj.demoVisibility

    }


    private fun displaySpeakers(obj: ItemViewModel, speakers: LinearLayoutCompat) {
//        val context = context ?: return
//
//        obj.speakers?.iterator()?.forEach {
//            speakers.addView(SpeakerView(context, database.getSpeaker(it)))
//        }
    }

    private val content: Event
        get() = arguments!!.getSerializable(ARG_EVENT) as Event

    private fun displayDescription(obj: ItemViewModel, description: TextView, empty: View, link: View, star: ImageView) {
        val hasDescription = obj.hasDescription()

        if (hasDescription)
            description.text = obj.description
        empty.visibility = if (hasDescription) View.GONE else View.VISIBLE

        link.visibility = if (obj.hasUrl()) View.VISIBLE else View.GONE

        updateStarIcon(star)
    }

    private fun updateStarIcon(star: ImageView) {
        star.setImageDrawable(resources.getDrawable(if (content.isBookmarked) R.drawable.ic_star_white_24dp else R.drawable.ic_star_border_white_24dp))
    }

    fun onStarClick(item: ItemView, star: ImageView) {
        if (content.isBookmarked) {
            analytics.tagItemEvent(AnalyticsController.Analytics.EVENT_UNBOOKMARK, content)
        } else {
            analytics.tagItemEvent(AnalyticsController.Analytics.EVENT_BOOKMARK, content)
        }
        item.onBookmarkClick()
        updateStarIcon(star)
    }

    private fun onShareClick(item: ItemView) {
        analytics.tagItemEvent(AnalyticsController.Analytics.EVENT_SHARE, content)
        item.onShareClick()
    }

    private fun onLinkClick() {
        val context = context ?: return

        analytics.tagItemEvent(AnalyticsController.Analytics.EVENT_LINK, content)

        MaterialAlert.create(context)
                .setTitle(R.string.link_warning)
                .setMessage(String.format(context.getString(R.string.link_message), content.url?.toLowerCase()))
                .setPositiveButton(R.string.open_link, DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content.url))
                    context.startActivity(intent)
                }).setBasicNegativeButton()
                .show()
    }

    companion object {

        private const val ARG_EVENT = "ARG_EVENT"

        fun newInstance(obj: Event): ScheduleItemBottomSheet {
            val fragment = ScheduleItemBottomSheet()

            val bundle = Bundle()
            bundle.putSerializable(ARG_EVENT, obj)
            fragment.arguments = bundle

            return fragment
        }
    }
}
