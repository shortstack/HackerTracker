package com.shortstack.hackertracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseLocation
import com.shortstack.hackertracker.models.FirebaseSpeaker
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.ui.search.LocationViewHolder
import com.shortstack.hackertracker.ui.search.SpeakerViewHolder

class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val EVENT = 0
        private const val LOCATION = 1
        private const val SPEAKER = 2
    }

    private val collection = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            EVENT -> EventViewHolder(inflater.inflate(R.layout.row, parent, false))
            SPEAKER -> SpeakerViewHolder(inflater.inflate(R.layout.row_speaker, parent, false))
            LOCATION -> LocationViewHolder(inflater.inflate(R.layout.item_type_header, parent, false))
            else -> throw IllegalStateException("Unknown viewType $viewType.")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = collection[position]

        when(holder) {
            is EventViewHolder -> holder.render(item as FirebaseEvent)
            is SpeakerViewHolder -> holder.render(item as FirebaseSpeaker)
            is LocationViewHolder -> holder.render(item as FirebaseLocation)
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