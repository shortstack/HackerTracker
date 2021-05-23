package com.shortstack.hackertracker.views

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.databinding.ItemTypeBinding
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class TypeViewHolder(private val binding: ItemTypeBinding) : RecyclerView.ViewHolder(binding.root),
    KoinComponent {

    // todo: move this out of here
    private val database: DatabaseManager by inject()

    fun render(type: Type) = with(binding) {
        val context = root.context


        val color = if (type.color == "#FFFFFF") {
            val theme = (context as MainActivity).theme
            val outValue = TypedValue()
            theme.resolveAttribute(R.attr.colorOnPrimary, outValue, true)
            outValue.data
        } else {
            Color.parseColor(type.color)
        }

        chip.text = type.shortName
        chip.chipBackgroundColor = ColorStateList.valueOf(color)
        chip.isCloseIconVisible = type.isSelected

        chip.setOnCheckedChangeListener(null)

        chip.isChecked = type.isSelected

        chip.setOnCloseIconClickListener {
            chip.isChecked = false
        }

        chip.setOnCheckedChangeListener { _, isChecked ->
            chip.isCloseIconVisible = isChecked

            // todo: put this on the right scope
            GlobalScope.launch {
                type.isSelected = isChecked
                database.updateTypeIsSelected(type)
            }
        }
    }

    companion object {
        fun inflate(parent: ViewGroup): TypeViewHolder {
            return TypeViewHolder(ItemTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}
