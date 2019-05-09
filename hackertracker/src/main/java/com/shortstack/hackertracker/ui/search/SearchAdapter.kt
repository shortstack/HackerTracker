package com.shortstack.hackertracker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Location
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.schedule.EventViewHolder
import com.shortstack.hackertracker.ui.speakers.SpeakerViewHolder

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val EVENT = 0
        private const val LOCATION = 1
        private const val SPEAKER = 2
        private const val HEADER = 3
    }

    private val collection = ArrayList<Any>()
    var state: State = State.INIT
    var query: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            EVENT -> EventViewHolder.inflate(parent)
            SPEAKER -> SpeakerViewHolder.inflate(parent)
            LOCATION -> LocationViewHolder.inflate(parent)
            HEADER -> HeaderViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType.")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = collection[position]

        when(holder) {
            is EventViewHolder -> holder.render(item as Event)
            is SpeakerViewHolder -> holder.render(item as Speaker)
            is LocationViewHolder -> holder.render(item as Location)
            is HeaderViewHolder -> holder.render(item as String)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(collection[position]) {
            is Speaker -> SPEAKER
            is Event -> EVENT
            is Location -> LOCATION
            is String -> HEADER
            else -> throw IllegalStateException("Unknown viewType ${collection[position].javaClass}")
        }
    }

    fun setList(elements: List<Any>) {
        state = when {
            query.isNullOrBlank() -> State.INIT
            elements.isNotEmpty() -> State.RESULTS
            else -> State.EMPTY
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

                val left = collection[oldItemPosition]
                val right = elements[newItemPosition]
                if (left is Event && right is Event) {
                    return left.id == right.id
                }
                if (left is Location && right is Location) {
                    return left.name == right.name
                }
                if (left is Speaker && right is Speaker) {
                    return left.name == right.name
                }
                if (left is String && right is String) {
                    return left == right
                }
                return false
            }

            override fun getOldListSize() = collection.size

            override fun getNewListSize() = elements.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return true
            }
        })

        collection.clear()
        collection.addAll(elements)

        result.dispatchUpdatesTo(this)
    }

    enum class State {
        INIT,
        RESULTS,
        EMPTY
    }
}