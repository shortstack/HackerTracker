package com.shortstack.hackertracker.Renderer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Event.UpdateListContentsEvent
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.activity_tutorial.view.*

class ChangeDayRenderer : Renderer<RendererContent<String>>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_feed_day, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView!!.button.setOnClickListener { onButtonClick() }
    }

    override fun render(payloads: List<Any>) {
        //view.setText(getContent().getItem());
    }

    fun onButtonClick() {
        App.application.storage!!.scheduleDay = App.application.storage!!.scheduleDay + 1
        App.application.postBusEvent(UpdateListContentsEvent())
    }
}
