package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Type
import kotlinx.android.synthetic.main.view_filter.view.*
import org.koin.standalone.KoinComponent

class FilterView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), KoinComponent {

    companion object {
        private const val SPAN_COUNT = 2
    }

    init {
        View.inflate(context, R.layout.view_filter, this)
    }

    fun setTypes(types: List<Type>?) {
        if (types != null) {

            val collection = ArrayList<Any>()

            types.find { it.isBookmark }?.let {
                collection.add(it)
            }

            collection.add(context.getString(R.string.types))

            val elements = types.filter { !it.isBookmark && !it.filtered }
            collection.addAll(elements)

            val adapter = FilterAdapter(collection)

            list.layoutManager = GridLayoutManager(context, SPAN_COUNT, RecyclerView.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter.getItemViewType(position)) {
                            FilterAdapter.TYPE_HEADER -> SPAN_COUNT
                            else -> 1
                        }
                    }
                }
            }
            list.adapter = adapter

        }
    }
}
