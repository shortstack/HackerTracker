package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R

class SkullHeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


    fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.header_home, parent, false)
    }

    private fun onSkullClick() {
        // TODO Implement skull animation.
    }
}
