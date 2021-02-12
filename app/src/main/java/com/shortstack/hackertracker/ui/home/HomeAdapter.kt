package com.shortstack.hackertracker.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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


        return when (collection[position]) {
            is Article -> ARTICLE
            is String -> HEADER
            else -> EVENT
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> holder.render(collection[position] as Event)
            is ArticleViewHolder -> holder.render(collection[position] as Article)
            is HeaderViewHolder -> holder.render(collection[position] as String)
        }
    }

    fun setElements(list: List<Any>) {
        val list = listOf(SKULL) + list

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                if(oldItemPosition == 0 && newItemPosition == 0)
                    return true

                val lhs = collection[oldItemPosition]
                val rhs = list[newItemPosition]

                if (lhs is Event && rhs is Event) {
                    return lhs.id == rhs.id
                }

                if (lhs is Article && rhs is Article) {
                    return lhs.id == rhs.id
                }

                if (lhs is String && rhs is String) {
                    return lhs == rhs
                }

                return false
            }

            override fun getOldListSize() = collection.size

            override fun getNewListSize() = list.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                if(oldItemPosition == 0 && newItemPosition == 0)
                    return true

                val lhs = collection[oldItemPosition]
                val rhs = list[newItemPosition]

                if (lhs is Event && rhs is Event) {
                    return lhs.updated == rhs.updated && lhs.isBookmarked == rhs.isBookmarked
                }

                if (lhs is Article && rhs is Article) {
                    return lhs.name == rhs.name && lhs.text == rhs.text
                }

                if (lhs is String && rhs is String) {
                    return lhs == rhs
                }

                return false
            }

        })

        collection.clear()
        collection.addAll(list)
        result.dispatchUpdatesTo(this)
    }
}