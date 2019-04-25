package com.shortstack.hackertracker.ui.schedule.renderers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.views.EventView
import kotlinx.android.synthetic.main.row.view.*

class EventRenderer(private val displayMode: Int = EventView.DISPLAY_MODE_MIN) : Renderer<FirebaseEvent>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row, parent, false)
    }

    override fun setUpView(rootView: View?) {
        rootView?.event?.setDisplayMode(displayMode)
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
        (context as? MainActivity)?.navigate(content)
    }
}
