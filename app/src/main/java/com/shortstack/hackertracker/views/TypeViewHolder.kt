package com.shortstack.hackertracker.views

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.local.Type
import kotlinx.android.synthetic.main.item_type.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class TypeViewHolder(val view: View) : RecyclerView.ViewHolder(view), KoinComponent {

    companion object {
        fun inflate(parent: ViewGroup): TypeViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_type, parent, false)
            return TypeViewHolder(view)
        }
    }

    private val database: DatabaseManager by inject()

    fun render(type: Type) {
        view.apply {
            val color = Color.parseColor(type.color)

            chip.chipText = type.name
            chip.chipBackgroundColor = ColorStateList.valueOf(color)
            chip.isCloseIconEnabled = type.isSelected

            chip.setOnCheckedChangeListener(null)

            chip.isChecked = type.isSelected


            chip.setOnCheckedChangeListener { _, isChecked ->

                chip.isCloseIconEnabled = isChecked

                // todo: put this on the right scope
                GlobalScope.launch {
                    type.isSelected = isChecked
                    database.updateTypeIsSelected(type)
                }
            }
        }
    }
}
