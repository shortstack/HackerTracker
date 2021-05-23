package com.shortstack.hackertracker.ui.events

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.ItemEventLinkBinding
import com.shortstack.hackertracker.models.local.Action

class ActionViewHolder(private val binding: ItemEventLinkBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(action: Action) {
        binding.action.setImageResource(action.res)
        binding.actionText.text = action.label
        binding.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(action.url))
            binding.root.context.startActivity(intent)
        }
    }

    companion object {

        fun inflate(parent: ViewGroup): ActionViewHolder {
            val binding = ItemEventLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ActionViewHolder(binding)
        }
    }
}