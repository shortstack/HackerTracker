package com.shortstack.hackertracker.ui.schedule

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.views.EventView

class EventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {

        fun inflate(parent: ViewGroup, mode: Int): EventViewHolder {
            val view = EventView(parent.context, display = mode)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            return EventViewHolder(view)
        }
    }

    fun render(event: Event) {
        (view as EventView).setContent(event)

        view.setOnClickListener {
            showEventFragment(event)
        }
    }

    private fun showEventFragment(event: Event) {
        (view.context as? MainActivity)?.navigate(event)
    }
}
