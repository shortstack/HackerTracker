package com.shortstack.hackertracker.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import retrofit2.http.HEAD
import java.lang.IllegalStateException

// TODO: Handle multiple types for the home screen.
class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val HEADER = 0
        private const val CARD = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> SkullHeaderViewHolder.inflate(parent)
            CARD -> HomeCardViewHolder.inflate(parent)
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            else -> CARD
        }
    }

    override fun getItemCount() = 9

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // TODO: Bind the views
    }
}