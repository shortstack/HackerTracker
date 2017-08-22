package com.shortstack.hackertracker.Renderer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.Activity.MainActivity
import com.shortstack.hackertracker.Model.Navigation
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_nav.view.*

class ActivityNavRenderer : Renderer<Navigation>(), View.OnClickListener {


    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_nav, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView!!.setOnClickListener(this)
    }

    override fun render(payloads: List<Any>) {
        rootView.header.text = content.title
        rootView.description.text = content.description
    }


    override fun onClick(view: View) {
        (context as MainActivity).loadFragment(MainActivity.NAV_INFORMATION)
    }
}
