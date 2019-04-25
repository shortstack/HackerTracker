package com.shortstack.hackertracker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseLocation
import kotlinx.android.synthetic.main.item_type_header.view.*

class LocationRenderer : Renderer<FirebaseLocation>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.item_type_header, container, false)
    }

    override fun render(p0: MutableList<Any>?) {
        rootView.header.text = content.name
    }
}
