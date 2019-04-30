package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R

// TODO: Handle multiple types for the home screen.
class HomeAdapter : RecyclerView.Adapter<SkullHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkullHeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.header_home, parent, false)
        return SkullHeaderViewHolder(view)
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: SkullHeaderViewHolder, position: Int) {
        // TODO: Bind the views
    }
}