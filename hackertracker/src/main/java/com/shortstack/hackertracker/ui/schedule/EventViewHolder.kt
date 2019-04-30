package com.shortstack.hackertracker.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.row.view.*

class EventViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row, parent, false)
    }

    fun render(event: FirebaseEvent) {
        view.event.setContent(event)

        view.setOnClickListener {
            showEventFragment(event)
        }
    }

    private fun showEventFragment(event: FirebaseEvent) {
        (view.context as? MainActivity)?.navigate(event)
    }
}
