package com.shortstack.hackertracker.ui.schedule.renderers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import kotlinx.android.synthetic.main.row_time_container.view.*

class RelativeTimeRenderer : Renderer<Time>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_time_container, parent, false)
    }

    override fun render(payloads: List<Any>) {
        rootView.time_item.setDate(content)
    }
}

class RelativeDayRender : Renderer<Day>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.row_header, parent, false)
    }

    override fun render(payloads: MutableList<Any>?) {
        (rootView as TextView).text = App.getRelativeDateStamp(content)
    }
}
