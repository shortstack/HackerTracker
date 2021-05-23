package com.shortstack.hackertracker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.ItemTypeHeaderBinding
import com.shortstack.hackertracker.models.local.Location

class LocationViewHolder(private val binding: ItemTypeHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(location: Location) {
        binding.header.text = location.name
    }

    companion object {
        fun inflate(parent: ViewGroup): LocationViewHolder {
            val binding = ItemTypeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LocationViewHolder(binding)
        }
    }
}
