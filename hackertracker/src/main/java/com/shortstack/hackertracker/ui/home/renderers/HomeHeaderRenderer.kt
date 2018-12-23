package com.shortstack.hackertracker.ui.home.renderers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseEvent
import kotlinx.android.synthetic.main.header_home.view.*

class HomeHeaderRenderer : Renderer<RendererContent<FirebaseEvent>>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.header_home, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView!!.logo.setOnClickListener { onSkullClick() }
    }

    override fun render(payloads: List<Any>) {
        // Do nothing.

    }

    fun onSkullClick() {
        // TODO Implement skull animation.
    }
}
