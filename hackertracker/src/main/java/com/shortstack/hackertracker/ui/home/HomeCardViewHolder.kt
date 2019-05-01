package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R

class HomeCardViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): HomeCardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_card, parent, false)
            return HomeCardViewHolder(view)
        }
    }

}