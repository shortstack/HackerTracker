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
import kotlinx.android.synthetic.main.item_type_header.view.*

/**
 * Created by Chris on 7/12/2018.
 */
class FilterAdapter(private val collection: ArrayList<Any>, private val database: DatabaseManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 2
        const val TYPE_ITEM = 1
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_type_header, parent, false))
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_type, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (collection[position] is String) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    override fun getItemCount() = collection.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.view.apply {
                    header.text = collection[position] as String
                }
            }
            is ViewHolder -> {
                val type = collection[position] as FirebaseType

                holder.view.apply {
                    val color = Color.parseColor(type.color)

                    chip.chipText = type.name
                    chip.chipBackgroundColor = ColorStateList.valueOf(color)
//                    chip.isCloseIconEnabled = type.isSelected

                    chip.setOnCheckedChangeListener(null)

//                    chip.isChecked = type.isSelected


                    chip.setOnCheckedChangeListener { _, isChecked ->

                        chip.isCloseIconEnabled = isChecked

                        Single.fromCallable {
//                            type.isSelected = isChecked
//                            database.updateTypeIsSelected(type)
                        }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({}, {})
                    }
                }
            }
        }
    }
}