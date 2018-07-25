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

    private lateinit var adapter: FilterAdapter

    init {
        App.application.component.inject(this)
        View.inflate(context, R.layout.view_filter, this)


    }

    private fun setCheckboxes(types: List<Type>) {

        val local = mutableListOf<Type>()
//        for(i in 0 until 20 )
            local.addAll(types)

        adapter = FilterAdapter(local, database)

        list.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

        list.adapter = adapter
    }

    fun setTypes(types: List<Type>?) {

        if (types != null)
            setCheckboxes(types)
    }


}
