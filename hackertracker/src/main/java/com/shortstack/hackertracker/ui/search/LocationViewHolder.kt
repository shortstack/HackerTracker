package com.shortstack.hackertracker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.firebase.FirebaseLocation
import kotlinx.android.synthetic.main.item_type_header.view.*

class LocationViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): LocationViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_type_header, parent, false)
            return LocationViewHolder(view)
        }
    }

    fun render(location: FirebaseLocation) {
        view.header.text = location.name
    }
}
