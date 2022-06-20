package com.advice.schedule.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.Location
import com.shortstack.hackertracker.databinding.ItemTypeHeaderBinding

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
