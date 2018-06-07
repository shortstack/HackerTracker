package com.shortstack.hackertracker.ui.home.renderers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.item_change_con.view.*

class ChangeConRenderer : Renderer<Void>() {

    override fun render(payloads : MutableList<Any>?) {

    }

    override fun hookListeners(rootView : View?) {
        rootView?.switch_con_button?.setOnClickListener {

        }
    }

    override fun inflate(inflater : LayoutInflater, parent : ViewGroup?) : View {
        return inflater.inflate(R.layout.item_change_con, parent, false)
    }
}