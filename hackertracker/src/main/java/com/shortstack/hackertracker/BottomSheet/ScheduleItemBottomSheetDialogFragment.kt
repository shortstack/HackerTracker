package com.shortstack.hackertracker.BottomSheet

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.shortstack.hackertracker.Alert.MaterialAlert
import com.shortstack.hackertracker.Analytics.AnalyticsController
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.ItemViewModel
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.View.ItemView
import com.shortstack.hackertracker.View.SpeakerView
import kotlinx.android.synthetic.main.bottom_sheet_schedule_item.view.*
import kotlinx.android.synthetic.main.empty_text.view.*

class ScheduleItemBottomSheetDialogFragment : android.support.design.widget.BottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_schedule_item, null)
        dialog.setContentView(view)

        val obj = ItemViewModel(content)

        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_VIEW, content)

        view.item!!.setItem(obj.item)

        displaySpeakers(obj, view.speakers )

        displayDescription(obj, view.description, view.empty, view.link, view.star )

        view.star.setOnClickListener { onStarClick(view.item, view.star) }
        view.share.setOnClickListener { onShareClick(view.item) }
        view.link.setOnClickListener { onLinkClick() }
    }

    private fun  displaySpeakers(obj: ItemViewModel, speakers: LinearLayoutCompat) {
        obj.speakers.iterator().forEach {
            speakers.addView(SpeakerView(context, App.application.databaseController.getSpeaker(it)))
        }
    }

    private val content: Item
        get() = arguments.getSerializable(ARG_OBJ) as Item

    private fun displayDescription(obj: ItemViewModel, description: TextView, empty: View, link: View, star: ImageView ) {
        val hasDescription = obj.hasDescription()

        if (hasDescription)
            description!!.text = obj.description
        empty!!.visibility = if (hasDescription) View.GONE else View.VISIBLE

        link!!.visibility = if (obj.hasUrl()) View.VISIBLE else View.GONE

        updateStarIcon(star)
    }

    private fun updateStarIcon( star: ImageView ) {
        star!!.setImageDrawable(resources.getDrawable(if (content.isBookmarked) R.drawable.ic_star_white_24dp else R.drawable.ic_star_border_white_24dp))
    }

    fun onStarClick( item: ItemView, star: ImageView ) {
        if (content.isBookmarked) {
            App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_UNBOOKMARK, content)
        } else {
            App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_BOOKMARK, content)
        }
        item.onBookmarkClick()
        updateStarIcon(star)
    }

    fun onShareClick( item: ItemView ) {
        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_SHARE, content)
        item.onShareClick()
    }

    fun onLinkClick() {
        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_LINK, content)

        MaterialAlert.create(context)
                .setTitle(R.string.link_warning)
                .setMessage(String.format(context.getString(R.string.link_message), content.link.toLowerCase()))
                .setPositiveButton(R.string.open_link) { dialogInterface, i ->
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content.link))
                    context.startActivity(intent)
                }.setBasicNegativeButton()
                .show()
    }

    companion object {


        val ARG_OBJ = "VENDOR"


        fun newInstance(obj: Item): ScheduleItemBottomSheetDialogFragment {
            val fragment = ScheduleItemBottomSheetDialogFragment()

            val bundle = Bundle()
            bundle.putSerializable(ARG_OBJ, obj)
            fragment.arguments = bundle

            return fragment
        }
    }
}
