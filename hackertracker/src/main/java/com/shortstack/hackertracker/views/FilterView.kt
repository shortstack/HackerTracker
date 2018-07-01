package com.shortstack.hackertracker.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.widget.CompoundButtonCompat
import androidx.appcompat.widget.AppCompatCheckBox
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

class FilterView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var checkboxes: Array<AppCompatCheckBox>

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
        View.inflate(context, R.layout.view_filter, this)
    }

    private fun setCheckboxes(types: List<Type>) {

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
