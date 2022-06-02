package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.shortstack.hackertracker.databinding.ViewFilterBinding
import com.shortstack.hackertracker.models.local.Type

class FilterView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var onClearListener: (() -> Unit)? = null
    private var onClickListener: ((Type) -> Unit)? = null


    private val binding = ViewFilterBinding.inflate(LayoutInflater.from(context), this, true)

    private val adapter: FilterAdapter = FilterAdapter {
        onClickListener?.invoke(it)
    }

    init {
        binding.list.adapter = adapter
        binding.toolbar.setOnMenuItemClickListener {
            onClearListener?.invoke()
            true
        }
    }

    fun setTypes(types: List<Type>?) {
        if (types != null) {
            val collection = ArrayList<Any>()

            types.find { it.isBookmark }?.let {
                collection.add(it)
            }

//            collection.add(context.getString(R.string.types))

            val elements = types.filter { !it.isBookmark && !it.isVillage && !it.isWorkshop }
                .sortedBy { it.shortName }
            collection.addAll(elements)

//            collection.add(context.getString(R.string.villages))

            val villages = types.filter { it.isVillage }.sortedBy { it.shortName }
            collection.addAll(villages)

//            collection.add(context.getString(R.string.workshops))

            val workshops = types.filter { it.isWorkshop }.sortedBy { it.shortName }
            collection.addAll(workshops)

            adapter.setElements(collection)
        }
    }

    fun setOnTypeClickListener(onClickListener: (Type) -> Unit) {
        this.onClickListener = onClickListener
    }

    fun setOnClearListener(onClearListener: () -> Unit) {
        this.onClearListener = onClearListener
    }
}
