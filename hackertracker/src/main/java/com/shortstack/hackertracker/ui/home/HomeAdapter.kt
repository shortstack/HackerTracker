package com.shortstack.hackertracker.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.schedule.EventViewHolder

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val SKULL = 0
        private const val EVENT = 2
        private const val ARTICLE = 3
    }

    private val collection = ArrayList<Any>()

    init {
        collection.add("Thank You" to "Thanks for being part of #io19! Keep the conversation going and remember to rate sessions you were able to attend. We'd love ot hear your feedback on the event as a whole: g.co/io/feedback")
        collection.add("I/O'19 Codelabs" to "Learn about the latest and greatest Google technologies on our ready-to-code kiosks equipped with the newest hardware from Android Auto, TensorFlow, Cast, and more! View all #io19 codelabs at g.co/io/codelabs")
        collection.add("I/O Arts" to "Art provides deep insight into the relationship between culture and technology.")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SKULL -> SkullHeaderViewHolder.inflate(parent)
            EVENT -> EventViewHolder.inflate(parent)
            ARTICLE -> ArticleViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0)
            return SKULL


        return when (collection[position - 1]) {
            is Pair<*, *> -> ARTICLE
            else -> EVENT
        }
    }

    override fun getItemCount() = collection.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> holder.render(collection[position - 1] as Event)
            is ArticleViewHolder -> {
                val pair = collection[position - 1] as Pair<String, String>
                holder.render(pair.first, pair.second)
            }
        }
    }

    fun addRecent(list: List<Event>) {
        val size = collection.size + 1
//        collection.clear()
//        notifyItemRangeRemoved(1, size)
//
        collection.addAll(list)
        notifyItemRangeInserted(size, list.size)
    }
}