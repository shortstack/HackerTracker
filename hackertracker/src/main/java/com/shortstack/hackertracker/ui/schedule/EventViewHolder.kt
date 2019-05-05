package com.shortstack.hackertracker.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row.view.*

class EventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): EventViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
            return EventViewHolder(view)
        }
    }

    fun render(event: Event) {
        view.event.setContent(event)

        view.setOnClickListener {
            showEventFragment(event)
        }
    }

    private fun showEventFragment(event: Event) {
        (view.context as? MainActivity)?.navigate(event)
    }
}
