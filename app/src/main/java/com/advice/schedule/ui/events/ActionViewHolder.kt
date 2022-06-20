package com.advice.schedule.ui.events

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.Action
import com.shortstack.hackertracker.databinding.ItemEventLinkBinding

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