package com.shortstack.hackertracker.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.local.*
import com.shortstack.hackertracker.ui.information.faq.FAQViewHolder
import com.shortstack.hackertracker.ui.information.speakers.SpeakerViewHolder
import com.shortstack.hackertracker.ui.information.vendors.VendorViewHolder
import com.shortstack.hackertracker.ui.schedule.DayViewHolder
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.ui.search.LocationViewHolder
import com.shortstack.hackertracker.views.EventView

class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val EVENT = 0
        private const val LOCATION = 1
        private const val SPEAKER = 2
        private const val DAY = 3
        private const val VENDOR = 5
        private const val FAQ = 6
    }

    val collection = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EVENT -> EventViewHolder.inflate(parent, EventView.DISPLAY_MODE_MIN)
            SPEAKER -> SpeakerViewHolder.inflate(parent)
            LOCATION -> LocationViewHolder.inflate(parent)
            DAY -> DayViewHolder.inflate(parent)
            VENDOR -> VendorViewHolder.inflate(parent)
            FAQ -> FAQViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType.")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = collection[position]

        when (holder) {
            is EventViewHolder -> holder.render(item as Event)
            is SpeakerViewHolder -> holder.render(item as Speaker)
            is LocationViewHolder -> holder.render(item as Location)
            is DayViewHolder -> holder.render(item as Day)
            is VendorViewHolder -> holder.render(item as Vendor)
            is FAQViewHolder -> holder.render(item as FAQ)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (collection[position]) {
            is Speaker -> SPEAKER
            is Location -> LOCATION
            is Event -> EVENT
            is Day -> DAY
            is Vendor -> VENDOR
            is FAQ -> FAQ
            else -> throw java.lang.IllegalStateException("Unknown viewType ${collection[position].javaClass}")
        }
    }

    fun clearAndNotify() {
        val size = collection.size
        collection.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAllAndNotify(list: List<Any>) {
        // TODO: Use DiffUtil
        collection.clear()
        collection.addAll(list)
        notifyDataSetChanged()
    }
}