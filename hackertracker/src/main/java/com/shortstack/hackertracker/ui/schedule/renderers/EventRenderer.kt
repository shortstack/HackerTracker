package com.shortstack.hackertracker.ui.schedule.renderers

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.ui.schedule.EventBottomSheet
import kotlinx.android.synthetic.main.row.view.*

class EventRenderer : Renderer<Event>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView?.setOnClickListener {
            showEventBottomSheet()
        }
    }

    override fun render(payloads: List<Any>) {
        rootView.item.setItem(content)
    }

    private fun showEventBottomSheet() {
        val fragment = EventBottomSheet.newInstance(content)
        fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
    }
}
