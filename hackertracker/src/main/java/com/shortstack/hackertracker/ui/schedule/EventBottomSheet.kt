package com.shortstack.hackertracker.ui.schedule

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.models.ItemViewModel
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.view.ItemView
import kotlinx.android.synthetic.main.bottom_sheet_schedule_item.view.*
import kotlinx.android.synthetic.main.empty_text.view.*

class EventBottomSheet : android.support.design.widget.BottomSheetDialogFragment() {

    override fun setupDialog(dialog : Dialog, style : Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_schedule_item, null)
        dialog.setContentView(view)

        val obj = ItemViewModel(content)

//        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_VIEW, content)

        view.item!!.setItem(obj.item)
//
//        displaySpeakers(obj, view.speakers)

        displayDescription(obj, view.description, view.empty, view.link, view.star)
//
        view.star.setOnClickListener { onStarClick(view.item, view.star) }
//        view.share.setOnClickListener { onShareClick(view.item) }
//        view.link.setOnClickListener { onLinkClick() }
//
//        view.tool.visibility = obj.toolsVisibility
//        view.exploit.visibility = obj.exploitVisibility
//        view.demo.visibility = obj.demoVisibility


//        initViewPager(obj, view)

    }


    private fun displaySpeakers(obj : ItemViewModel, speakers : LinearLayoutCompat) {
        val context = context ?: return

//        obj.speakers.iterator().forEach {
//            speakers.addView(SpeakerView(context, App.application.databaseController.getSpeaker(it)))
//        }
    }

    private val content : Event
        get() = arguments?.getSerializable(ARG_OBJ) as Event

    private fun displayDescription(obj : ItemViewModel, description : TextView, empty : View, link : View, star : ImageView) {
        val hasDescription = obj.hasDescription()

        if (hasDescription)
            description.text = obj.description
        empty.visibility = if (hasDescription) View.GONE else View.VISIBLE

        link.visibility = if (obj.hasUrl()) View.VISIBLE else View.GONE

        updateStarIcon(star)
    }

    private fun updateStarIcon(star : ImageView) {
//        star.setImageDrawable(resources.getDrawable(if (content.isBookmarked()) R.drawable.ic_star_white_24dp else R.drawable.ic_star_border_white_24dp))
    }

    fun onStarClick(item : ItemView, star : ImageView) {
        val isBookmarking = !content.isBookmarked

        if (isBookmarking) {
            //            App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_BOOKMARK, content)
        } else {
            //            App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_UNBOOKMARK, content)
        }
        item.onBookmarkClick()
        updateStarIcon(star)
    }

    fun onShareClick(item : ItemView) {
//        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_SHARE, content)
        item.onShareClick()
    }

    fun onLinkClick() {
        val context = context ?: return

//        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_LINK, content)

//        MaterialAlert.create(context)
//                .setTitle(R.string.link_warning)
//                .setMessage(String.format(context.getString(R.string.link_message), content.link?.toLowerCase()))
//                .setPositiveButton(R.string.open_link, DialogInterface.OnClickListener { dialogInterface, i ->
//                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content.link))
//                    context.startActivity(intent)
//                }).setBasicNegativeButton()
//                .show()
    }

    companion object {


        val ARG_OBJ = "ARG_EVENT"


        fun newInstance(obj : Event) : EventBottomSheet {
            val fragment = EventBottomSheet()

            val bundle = Bundle()
            bundle.putSerializable(ARG_OBJ, obj)
            fragment.arguments = bundle

            return fragment
        }
    }

    class PagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position : Int) : Fragment {
            return InformationFragment.newInstance()
        }

        override fun getCount() : Int {
            return 2
        }

    }
}
