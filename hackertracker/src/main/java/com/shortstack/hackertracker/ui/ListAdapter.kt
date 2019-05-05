package com.shortstack.hackertracker.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.*
import com.shortstack.hackertracker.models.firebase.FirebaseEvent
import com.shortstack.hackertracker.models.firebase.FirebaseLocation
import com.shortstack.hackertracker.models.firebase.FirebaseSpeaker
import com.shortstack.hackertracker.models.local.Vendor
import com.shortstack.hackertracker.ui.schedule.DayViewHolder
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.ui.schedule.TimeViewHolder
import com.shortstack.hackertracker.ui.search.LocationViewHolder
import com.shortstack.hackertracker.ui.speakers.SpeakerViewHolder
import com.shortstack.hackertracker.ui.vendors.VendorViewHolder

class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val EVENT = 0
        private const val LOCATION = 1
        private const val SPEAKER = 2
        private const val DAY = 3
        private const val TIME = 4
        private const val VENDOR = 5
    }

    private val collection = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EVENT -> EventViewHolder.inflate(parent)
            SPEAKER -> SpeakerViewHolder.inflate(parent)
            LOCATION -> LocationViewHolder.inflate(parent)
            DAY -> DayViewHolder.inflate(parent)
            TIME -> TimeViewHolder.inflate(parent)
            VENDOR -> VendorViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType.")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = collection[position]

        when (holder) {
            is EventViewHolder -> holder.render(item as FirebaseEvent)
            is SpeakerViewHolder -> holder.render(item as FirebaseSpeaker)
            is LocationViewHolder -> holder.render(item as FirebaseLocation)
            is DayViewHolder -> holder.render(item as Day)
            is TimeViewHolder -> holder.render(item as Time)
            is VendorViewHolder -> holder.render(item as Vendor)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (collection[position]) {
            is FirebaseSpeaker -> SPEAKER
            is FirebaseLocation -> LOCATION
            is FirebaseEvent -> EVENT
            is Day -> DAY
            is Time -> TIME
            is Vendor -> VENDOR
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