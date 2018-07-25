package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Type
import kotlinx.android.synthetic.main.view_filter.view.*
import javax.inject.Inject

class FilterView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
        View.inflate(context, R.layout.view_filter, this)
    }

    fun setTypes(types: List<Type>?) {
        if (types != null) {
            val adapter = FilterAdapter(types, database)

            list.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter.getItemViewType(position)) {
                            FilterAdapter.TYPE_HEADER -> 2
                            else -> 1
                        }
                    }
                }
            }
            list.adapter = adapter

        }
    }
}
