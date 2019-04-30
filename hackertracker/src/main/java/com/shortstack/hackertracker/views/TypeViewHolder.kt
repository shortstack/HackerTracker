package com.shortstack.hackertracker.views

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FirebaseType
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_type.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TypeViewHolder(val view: View) : RecyclerView.ViewHolder(view), KoinComponent {

    companion object {
        fun inflate(parent: ViewGroup): TypeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_type, parent, false)
            return TypeViewHolder(view)
        }
    }

    private val database: DatabaseManager by inject()

    fun render(type: FirebaseType) {
        view.apply {
            val color = Color.parseColor(type.color)

            chip.chipText = type.name
            chip.chipBackgroundColor = ColorStateList.valueOf(color)
            chip.isCloseIconEnabled = type.isSelected

            chip.setOnCheckedChangeListener(null)

            chip.isChecked = type.isSelected


            chip.setOnCheckedChangeListener { _, isChecked ->

                chip.isCloseIconEnabled = isChecked

                Single.fromCallable {
                    type.isSelected = isChecked
                    database.updateTypeIsSelected(type)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, {})
            }
        }
    }
}
