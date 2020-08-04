package com.shortstack.hackertracker.ui.information.villages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.item_village.view.*

class VillagesViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun inflate(parent: ViewGroup): VillagesViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_village, parent, false)
            return VillagesViewHolder(view)
        }
    }

    fun render(type: Type) {
        view.category_title.text = type.name
        view.category_description.text = type.description

        view.setOnClickListener {
            (view.context as MainActivity).navigate(type)
        }
    }
}
