package com.shortstack.hackertracker.ui.schedule.renderers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Day
import com.shortstack.hackertracker.models.Time
import com.shortstack.hackertracker.utils.TimeUtil
import kotlinx.android.synthetic.main.row_time_container.view.*

class RelativeTimeRenderer : Renderer<Time>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_time_container, parent, false)
    }

    override fun render(payloads: List<Any>) {
        rootView.time_item.setContent(content)
    }
}

class RelativeDayRender : Renderer<Day>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup?): View {
        return inflater.inflate(R.layout.row_header, parent, false)
    }

    override fun render(payloads: MutableList<Any>?) {
        val context = context ?: return
        (rootView as TextView).text = TimeUtil.getRelativeDateStamp(context, content)
    }
}
