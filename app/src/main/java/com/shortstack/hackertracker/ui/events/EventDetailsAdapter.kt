package com.shortstack.hackertracker.ui.events

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.Action
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.ui.information.speakers.SpeakerViewHolder
import com.shortstack.hackertracker.ui.search.HeaderViewHolder
import kotlinx.android.synthetic.main.item_event_link.view.*

class EventDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ACTION = 1
        private const val TYPE_SPEAKER = 2
    }

    private val collection = ArrayList<Any>()

    override fun getItemViewType(position: Int): Int {
        return when (collection[position]) {
            is String -> TYPE_HEADER
            is Action -> TYPE_ACTION
            is Speaker -> TYPE_SPEAKER
            else -> throw IllegalStateException("Unknown view type: ${collection[position].javaClass.simpleName}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.inflate(parent)
            TYPE_ACTION -> ActionViewHolder.inflate(parent)
            TYPE_SPEAKER -> SpeakerViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown view type: $viewType")
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.render(collection[position] as String)
            is ActionViewHolder -> holder.render(collection[position] as Action)
            is SpeakerViewHolder -> holder.render(collection[position] as Speaker)
        }
    }

    fun getSpanSize(position: Int, span: Int): Int {
        return when (collection[position]) {
            is String -> span
            is Action -> 1
            is Speaker -> span
            else -> throw IllegalStateException("Unknown view type: ${collection[position].javaClass.simpleName}")
        }
    }

    fun setElements(list: List<Any>) {
        collection.clear()
        collection.addAll(list)
        notifyDataSetChanged()
    }


    class ActionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun inflate(parent: ViewGroup): ActionViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_event_link, parent, false)
                return ActionViewHolder(view)
            }
        }

        fun render(action: Action) {
            view.action.setImageResource(action.res)
            view.action_text.text = action.label
            view.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(action.url))
                view.context.startActivity(intent)
            }
        }
    }
}