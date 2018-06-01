package com.shortstack.hackertracker.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v4.widget.CompoundButtonCompat
import android.support.v7.widget.AppCompatCheckBox
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Filter
import com.shortstack.hackertracker.models.Type
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_filter.view.*
import java.util.*
import javax.inject.Inject

class FilterView : LinearLayout {

    private lateinit var checkboxes: Array<AppCompatCheckBox>

    @Inject
    lateinit var storage: SharedPreferencesUtil

    @Inject
    lateinit var database: DatabaseManager

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, filter: Filter) : super(context) {
        init()
//        setFilter(filter)
    }

    private fun setFilter(filter: Filter) {
        val typesArray = filter.typesArray

        if (typesArray.isEmpty()) {
            for (type in checkboxes) {
                type.isChecked = true
            }
        }

        for (aTypesArray in typesArray) {
            for (i1 in checkboxes.indices) {
                if (aTypesArray == checkboxes[i1].text.toString()) {
                    checkboxes[i1].isChecked = true
                }
            }
        }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        App.application.myComponent.inject(this)

        View.inflate(context, R.layout.view_filter, this)

        database.getTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ types ->
                    setCheckboxes(types)
                }, {

                })
    }

    private fun setCheckboxes(types: List<Type>) {
        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())

        checkboxes = Array(types.size, {
            val type = types[it]

            val box = AppCompatCheckBox(context)
            box.text = type.type
            CompoundButtonCompat.setButtonTintList(box, ColorStateList.valueOf(Color.parseColor(type.colour)))

            box.isChecked = type.isSelected

            if ((it + 1) <= types.size / 2)
                filter_left.addView(box)
            else
                filter_right.addView(box)

            return@Array box
        })
    }

    fun save(): Filter {
        val selected = ArrayList<String>()

        for (i in checkboxes.indices) {
            val type = checkboxes[i]
            if (type.isChecked) {
                selected.add(checkboxes[i].text.toString())
            }
        }

        val strings = selected.toTypedArray()

        val filter = Filter(strings)

        storage.saveFilter(filter)

        return filter
    }
}
