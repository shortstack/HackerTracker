package com.advice.schedule.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.ItemTypeHeaderBinding

class HeaderViewHolder(private val binding: ItemTypeHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(text: String) {
        binding.header.text = text
    }

    companion object {
        fun inflate(parent: ViewGroup): HeaderViewHolder {
            val binding = ItemTypeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return HeaderViewHolder(binding)
        }
    }
}
