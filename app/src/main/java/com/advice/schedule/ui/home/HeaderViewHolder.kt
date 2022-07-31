package com.advice.schedule.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.ItemHeaderBinding

class HeaderViewHolder(private val binding: ItemHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(text: String) {
        binding.header.text = text
    }

    companion object {
        fun inflate(parent: ViewGroup): HeaderViewHolder {
            val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return HeaderViewHolder(binding)
        }
    }
}