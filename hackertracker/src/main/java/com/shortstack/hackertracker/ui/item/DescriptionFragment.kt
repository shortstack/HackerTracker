package com.shortstack.hackertracker.ui.item

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.Item
import com.shortstack.hackertracker.models.ItemViewModel
import com.shortstack.hackertracker.utils.MaterialAlert
import com.shortstack.hackertracker.view.ItemView
import kotlinx.android.synthetic.main.empty_text.view.*
import kotlinx.android.synthetic.main.fragment_item_description.view.*

class DescriptionFragment : Fragment() {

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        return inflater.inflate(R.layout.fragment_item_description, container, false);
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = ItemViewModel(content)

        displayDescription(obj, view.description, view.empty, view.link, view.star)

        view.star.setOnClickListener {
            TODO()
        }
        //onStarClick(view.item, view.star) }
        view.share.setOnClickListener {
            TODO()
            //onShareClick(view.item)
        }
        view.link.setOnClickListener { onLinkClick() }

        view.tool.visibility = obj.toolsVisibility
        view.exploit.visibility = obj.exploitVisibility
        view.demo.visibility = obj.demoVisibility


    }

    private fun displayDescription(obj : ItemViewModel, description : TextView, empty : View, link : View, star : ImageView) {
        val hasDescription = obj.hasDescription()

        if (hasDescription)
            description.text = obj.description
        empty.visibility = if (hasDescription) View.GONE else View.VISIBLE

        link.visibility = if (obj.hasUrl()) View.VISIBLE else View.GONE

        updateStarIcon(star)
    }

    private fun updateStarIcon(star : ImageView) {
        star.setImageDrawable(resources.getDrawable(if (content.isBookmarked()) R.drawable.ic_star_white_24dp else R.drawable.ic_star_border_white_24dp))
    }

    fun onStarClick(item : ItemView, star : ImageView) {
        if (content.isBookmarked()) {
            App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_UNBOOKMARK, content)
        } else {
            App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_BOOKMARK, content)
        }
        item.onBookmarkClick()
        updateStarIcon(star)
    }

    fun onShareClick(item : ItemView) {
        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_SHARE, content)
        item.onShareClick()
    }

    fun onLinkClick() {
        val context = context ?: return

        App.application.analyticsController.tagItemEvent(AnalyticsController.Analytics.EVENT_LINK, content)

        MaterialAlert.create(context)
                .setTitle(R.string.link_warning)
                .setMessage(String.format(context.getString(R.string.link_message), content.link?.toLowerCase()))
                .setPositiveButton(R.string.open_link, DialogInterface.OnClickListener { dialogInterface, i ->
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(content.link))
                    context.startActivity(intent)
                }).setBasicNegativeButton()
                .show()
    }

    private val content : Item
        get() = arguments?.getSerializable(ARG_ITEM) as Item


    companion object {
        val ARG_ITEM = "ARG_ITEM"

        fun newInstance(obj : Item) : DescriptionFragment {
            val fragment = DescriptionFragment()

            val bundle = Bundle()
            bundle.putSerializable(ARG_ITEM, obj)
            fragment.arguments = bundle

            return fragment
        }
    }

}