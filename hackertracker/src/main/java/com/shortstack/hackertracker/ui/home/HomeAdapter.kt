package com.shortstack.hackertracker.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.schedule.EventViewHolder

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val SKULL = 0
        private const val CARD = 1
        private const val EVENT = 2
    }

    private val collection = ArrayList<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SKULL -> SkullHeaderViewHolder.inflate(parent)
            CARD -> HomeCardViewHolder.inflate(parent)
            EVENT -> EventViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> SKULL
            else -> EVENT
        }
    }

    override fun getItemCount() = collection.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> holder.render(collection[position - 1])
        }
    }

    fun addRecent(list: List<Event>) {
        val size = collection.size
        collection.clear()
        notifyItemRangeRemoved(1, size)

        collection.addAll(list)
        notifyItemRangeInserted(1, list.size)
    }
}