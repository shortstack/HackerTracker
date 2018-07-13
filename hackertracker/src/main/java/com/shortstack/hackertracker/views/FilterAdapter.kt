package com.shortstack.hackertracker.views

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Type
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_type.view.*

/**
 * Created by Chris on 7/12/2018.
 */
class FilterAdapter(private val types: List<Type>, private val database: DatabaseManager) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_type, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = types.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = types[position]

        holder.view.apply {
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
                    database.updateType(type)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, {})
            }
        }
    }
}