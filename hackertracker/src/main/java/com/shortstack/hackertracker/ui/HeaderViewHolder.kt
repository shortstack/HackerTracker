package com.shortstack.hackertracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R

class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_header, parent, false)
    }

    fun render(text: String) {
        (view as TextView).text = text
    }
}
