package com.shortstack.hackertracker.views

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.search.HeaderViewHolder
import kotlin.math.min

// todo: replace with ListAdapter
class FilterAdapter(
    private val onClickListener: (Type) -> Unit,
    private val onLongClickListener: (Type) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val collection = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.inflate(parent)
            else -> TypeViewHolder.inflate(parent)
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
            is HeaderViewHolder -> holder.render(collection[position] as String)
            is TypeViewHolder -> holder.render(
                collection[position] as Type,
                onClickListener,
                onLongClickListener
            )
        }
    }

    fun setElements(elements: List<Any>) {
        collection.clear()
        collection.addAll(elements)
        notifyDataSetChanged()
    }

    fun getSpanCount(position: Int, spanCount: Int): Int {
        val element = collection[position] as Type
        val min = min(element.shortName.length / 10, spanCount)
        return spanCount
        //return min
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
}