package com.advice.schedule.ui.schedule

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.Event
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.views.EventView

class EventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun render(event: Event) {
        (view as EventView).setContent(event)

        view.setOnClickListener {
            showEventFragment(event)
        }
    }

    private fun showEventFragment(event: Event) {
        (view.context as? MainActivity)?.showEvent(event)
    }

    companion object {

        // todo: inflate this using ViewBinding
        fun inflate(parent: ViewGroup, mode: Int): EventViewHolder {
            val view = EventView(parent.context, display = mode)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            return EventViewHolder(view)
        }
    }
}
