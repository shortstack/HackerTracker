package com.shortstack.hackertracker.ui.schedule.renderers

import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.ui.schedule.EventBottomSheet
import com.shortstack.hackertracker.views.EventView
import kotlinx.android.synthetic.main.row.view.*

class EventRenderer : Renderer<DatabaseEvent>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView?.setOnClickListener {
            showEventBottomSheet()
        }
    }

    override fun render(payloads: List<Any>) {
        rootView.event.setContent(content)
    }

    private fun showEventBottomSheet() {
        val fragment = EventBottomSheet.newInstance(content)
        fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
    }
}
