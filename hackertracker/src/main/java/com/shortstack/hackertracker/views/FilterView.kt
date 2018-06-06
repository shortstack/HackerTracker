package com.shortstack.hackertracker.views

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
import com.shortstack.hackertracker.models.Type
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_filter.view.*
import javax.inject.Inject

class FilterView : LinearLayout {

    private lateinit var checkboxes: Array<AppCompatCheckBox>

    @Inject
    lateinit var database: DatabaseManager

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        App.application.component.inject(this)
        View.inflate(context, R.layout.view_filter, this)
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

            box.setOnCheckedChangeListener { _, isChecked ->

                Single.fromCallable {
                    type.isSelected = isChecked
                    database.updateType(type)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({}, {})

            }

            return@Array box
        })
    }

    private fun clear() {
        filter_left.removeAllViews()
        filter_right.removeAllViews()
    }

    fun setTypes(types: List<Type>?) {
        clear()

        if (types != null)
            setCheckboxes(types)
    }


}
