package com.shortstack.hackertracker.Renderer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_feed_day.view.*

class FeedEndRenderer : Renderer<RendererContent<String>>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_feed_end, parent, false)
    }

    override fun render(payloads: List<Any>) {
        rootView.row_text.text = content.item
    }
}
