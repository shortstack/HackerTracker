package com.shortstack.hackertracker.Renderer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_time_container.view.*
import java.util.*

class RelativeTimeRenderer : Renderer<Date>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_time_container, parent, false)
    }

    override fun render(payloads: List<Any>) {
        rootView.time_item.setDate(content)
    }
}
