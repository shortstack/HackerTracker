package com.shortstack.hackertracker.ui.information.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): CategoryViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return CategoryViewHolder(view)
        }
    }

    fun render(type: Type) {
        view.category_title.text = type.fullName
        view.category_description.text = type.description

        view.setOnClickListener {
            (view.context as MainActivity).navigate(type)
        }
    }
}
