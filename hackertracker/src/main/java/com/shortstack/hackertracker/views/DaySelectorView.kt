package com.shortstack.hackertracker.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.constraintlayout.widget.Constraints
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_day_selector.view.*
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class DaySelectorView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val children = ArrayList<TextView>()
    private var listener: OnDaySelectedListener? = null

    private var begin: Int = -1
    private var end: Int = -1

    init {
        View.inflate(context, R.layout.view_day_selector, this)

        begin = day_1.id
        end = day_1.id

        for (i in 0..frame.childCount) {
            val view = frame.getChildAt(i)
            if (view is TextView) {
                children.add(view)
                view.setOnClickListener {
                    listener?.onDaySelected(Date(view.tag as Long))
                }
            }
        }
    }

    private fun onDaySelected(view: View, position: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(frame)

        constraintSet.apply {
            connect(bubble.id, position, view.id, position)
        }


        val transition = ChangeBounds().apply {
            interpolator = AnticipateOvershootInterpolator(1.0f)
            duration = 500
        }

        TransitionManager.beginDelayedTransition(frame, transition)
        constraintSet.applyTo(frame)
    }

    fun onScroll(begin: Date, end: Date) {
        val instance = Calendar.getInstance()
        instance.time = begin

        when (instance.get(Calendar.DAY_OF_MONTH)) {
            31 -> onBeginDaySelected(day_1)
            1 -> onBeginDaySelected(day_2)
            2 -> onBeginDaySelected(day_3)
        }

        instance.time = end

        when (instance.get(Calendar.DAY_OF_MONTH)) {
            31 -> onEndDaySelected(day_1)
            1 -> onEndDaySelected(day_2)
            2 -> onEndDaySelected(day_3)
        }
    }

    private fun onBeginDaySelected(view: View) {
        if(begin == view.id)
            return

        begin = view.id

        onDaySelected(view, START)
    }

    private fun onEndDaySelected(view: View) {
        if(end == view.id)
            return

        end = view.id

        onDaySelected(view, END)
    }

    fun addOnDaySelectedListener(listener: OnDaySelectedListener) {
        this.listener = listener
    }

    fun setDays(days: List<Date>) {
        Logger.d(days)

        children.clear()

        val format = SimpleDateFormat("MMM d")


        days.forEachIndexed { index, date ->
            val view = when(index) {
                0 -> day_1
                1 -> day_2
                2 -> day_3
                3 -> day_4
                else -> throw IllegalArgumentException("Day is out of bounds $index.")
            }

            view.text = format.format(date)
            view.tag = date.time
        }

        3 /4
    }

    interface OnDaySelectedListener {
        fun onDaySelected(day: Date)
    }

}