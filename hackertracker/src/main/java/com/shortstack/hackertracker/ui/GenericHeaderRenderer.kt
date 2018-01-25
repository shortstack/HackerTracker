package com.shortstack.hackertracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R

class GenericHeaderRenderer : Renderer<String>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_header, parent, false)
    }

    override fun render(payloads: List<Any>) {
        (rootView as TextView).text = content
    }
}
