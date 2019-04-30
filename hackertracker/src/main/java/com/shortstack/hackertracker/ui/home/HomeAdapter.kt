package com.shortstack.hackertracker.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// TODO: Handle multiple types for the home screen.
class HomeAdapter : RecyclerView.Adapter<SkullHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkullHeaderViewHolder {
        return SkullHeaderViewHolder.inflate(parent)
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: SkullHeaderViewHolder, position: Int) {
        // TODO: Bind the views
    }
}