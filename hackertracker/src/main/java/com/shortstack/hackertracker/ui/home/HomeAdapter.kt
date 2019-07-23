package com.shortstack.hackertracker.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.local.Article
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.views.EventView

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val SKULL = 0
        private const val HEADER = 1
        private const val EVENT = 2
        private const val ARTICLE = 3
    }

    private val collection = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SKULL -> SkullHeaderViewHolder.inflate(parent)
            HEADER -> HeaderViewHolder.inflate(parent)
            EVENT -> EventViewHolder.inflate(parent, EventView.DISPLAY_MODE_MIN)
            ARTICLE -> ArticleViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return SKULL


        return when (collection[position - 1]) {
            is Article -> ARTICLE
            is String -> HEADER
            else -> EVENT
        }
    }

    override fun getItemCount() = collection.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> holder.render(collection[position - 1] as Event)
            is ArticleViewHolder -> holder.render(collection[position - 1] as Article)
            is HeaderViewHolder -> holder.render(collection[position - 1] as String)
        }
    }

    fun setElements(list: List<Any>) {
        val size = collection.size
        collection.clear()
        notifyItemRangeRemoved(1, size)

        collection.addAll(list)
        notifyItemRangeInserted(1, list.size)
    }
}