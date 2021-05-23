package com.shortstack.hackertracker.ui.information.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.databinding.ItemCategoryBinding
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity

class CategoryViewHolder(private val binding: ItemCategoryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(type: Type) {
        binding.categoryTitle.text = type.fullName
        binding.categoryDescription.text = type.description

        binding.root.setOnClickListener {
            (binding.root.context as MainActivity).navigate(type)
        }
    }

    companion object {
        fun inflate(parent: ViewGroup): CategoryViewHolder {
            val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CategoryViewHolder(binding)
        }
    }
}
