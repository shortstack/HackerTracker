package com.shortstack.hackertracker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.item_type_header.view.*

class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): HeaderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_type_header, parent, false)
            return HeaderViewHolder(view)
        }
    }

    fun render(text: String) {
        view.header.text = text
    }
}
