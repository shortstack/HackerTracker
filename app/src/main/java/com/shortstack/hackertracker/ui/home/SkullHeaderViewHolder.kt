package com.shortstack.hackertracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.HeaderHomeBinding

class SkullHeaderViewHolder(private val binding: HeaderHomeBinding) :
    RecyclerView.ViewHolder(binding.root) {


    private fun onSkullClick() {
        // TODO Implement skull animation.
    }

    companion object {
        fun inflate(parent: ViewGroup): SkullHeaderViewHolder {
            val binding = HeaderHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SkullHeaderViewHolder(binding)
        }
    }
}
