package com.shortstack.hackertracker.ui.schedule.renderers

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.ui.schedule.EventBottomSheet
import com.shortstack.hackertracker.views.EventView
import kotlinx.android.synthetic.main.row.view.*

class EventRenderer(private val displayMode: Int = EventView.DISPLAY_MODE_MIN) : Renderer<DatabaseEvent>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row, parent, false)
    }

    override fun setUpView(rootView: View?) {
        rootView?.event?.setDisplayMode(displayMode)
    }

    override fun hookListeners(rootView: View?) {
        rootView?.setOnClickListener {
            showEventBottomSheet()
        }
    }

    override fun render(payloads: List<Any>) {
        rootView.event.setEvent(content)
    }

    private fun showEventBottomSheet() {
        val fragment = EventBottomSheet.newInstance(content.event)
        fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
    }
}
