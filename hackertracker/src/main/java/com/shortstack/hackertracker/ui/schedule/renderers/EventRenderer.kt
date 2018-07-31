package com.shortstack.hackertracker.ui.schedule.renderers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.events.EventFragment
import kotlinx.android.synthetic.main.row.view.*

class EventRenderer : Renderer<DatabaseEvent>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView?.setOnClickListener {
            showEventFragment()
        }
    }

    override fun render(payloads: List<Any>) {
        rootView.event.setContent(content)
    }

    private fun showEventFragment() {
        val bundle = Bundle()
        bundle.putParcelable(EventFragment.EXTRA_EVENT, content)

        (context as? MainActivity)?.navController?.navigate(R.id.nav_event, bundle)
    }
}
