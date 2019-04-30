package com.shortstack.hackertracker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.item_type_header.view.*

class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.item_type_header, container, false)
    }

    fun render(text: String) {
        view.header.text = text
    }
}
