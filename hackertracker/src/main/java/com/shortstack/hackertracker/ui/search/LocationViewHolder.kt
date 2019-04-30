package com.shortstack.hackertracker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseLocation
import kotlinx.android.synthetic.main.item_type_header.view.*

class LocationViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.item_type_header, container, false)
    }

    fun render(location: FirebaseLocation) {
        view.header.text = location.name
    }
}
