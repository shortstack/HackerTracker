package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R

class SkullHeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): SkullHeaderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.header_home, parent, false)
            return SkullHeaderViewHolder(view)
        }
    }

    private fun onSkullClick() {
        // TODO Implement skull animation.
    }
}
