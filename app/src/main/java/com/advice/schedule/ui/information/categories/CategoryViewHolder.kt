package com.advice.schedule.ui.information.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.Type
import com.advice.schedule.ui.activities.MainActivity
import com.shortstack.hackertracker.databinding.ItemCategoryBinding

class CategoryViewHolder(private val binding: ItemCategoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(type: Type) {
        binding.categoryTitle.text = type.fullName
        binding.categoryDescription.text = type.description

        binding.root.setOnClickListener {
            (binding.root.context as MainActivity).showCategoryType(type)
        }
    }

    companion object {
        fun inflate(parent: ViewGroup): CategoryViewHolder {
            val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoryViewHolder(binding)
        }
    }
}
