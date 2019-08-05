package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Day
import kotlinx.android.synthetic.main.view_day_selector.view.*
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

        for (i in 0..frame.childCount) {
            val view = frame.getChildAt(i)
            if (view is TextView) {
                children.add(view)
                view.setOnClickListener {
                    val tag = view.tag as? Long
                    if( tag != null) {
                        listener?.onDaySelected(Date(tag))
                    }
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
        val dates = children.map { Date(it.tag as Long) }

        val instance = Calendar.getInstance()

        instance.time = begin
        instance.set(Calendar.HOUR_OF_DAY, 0)
        instance.set(Calendar.MINUTE, 0)
        instance.set(Calendar.SECOND, 0)
        instance.set(Calendar.MILLISECOND, 0)

        val beginDay = instance.time

        instance.time = end
        instance.set(Calendar.HOUR_OF_DAY, 0)
        instance.set(Calendar.MINUTE, 0)
        instance.set(Calendar.SECOND, 0)
        instance.set(Calendar.MILLISECOND, 0)

        val endDay = instance.time


        val beginIndex = getDayIndex(dates, beginDay)
        if(beginIndex != -1) {
            onBeginDaySelected(getViewByIndex(beginIndex))
        }

        val endIndex = getDayIndex(dates, endDay)
        if (endIndex != -1) {
            onEndDaySelected(getViewByIndex(endIndex))
        }
    }

    private fun getViewByIndex(index: Int): View {
        return when (index) {
            0 -> day_1
            1 -> day_2
            2 -> day_3
            3 -> day_4
            4 -> day_5
            5 -> day_6
            6 -> day_7
            else -> throw ArrayIndexOutOfBoundsException("Index out of bounds: $index.")
        }
    }

    private fun getDayIndex(dates: List<Date>, endDay: Date): Int {
        return dates.indexOfFirst {
            it.time == endDay.time
        }
    }

    private fun onBeginDaySelected(view: View) {
        if (begin == view.id)
            return

        begin = view.id

        onDaySelected(view, START)
    }

    private fun onEndDaySelected(view: View) {
        if (end == view.id)
            return

        end = view.id

        onDaySelected(view, END)
    }

    fun addOnDaySelectedListener(listener: OnDaySelectedListener) {
        this.listener = listener
    }

    fun setDays(days: List<Date>) {
        val calendar = Calendar.getInstance()

        days.forEach {
            calendar.time = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            it.time = calendar.time.time
        }


        children.clear()

        val format = SimpleDateFormat("MMM d")


        for (index in 0..6) {
            val view = getViewByIndex(index) as TextView

            if (index < days.size) {
                val date = days[index]

                view.visibility = View.VISIBLE
                view.text = format.format(date)
                view.tag = date.time

                children.add(view)
            } else {
                view.visibility = View.GONE
            }
        }
    }

    interface OnDaySelectedListener {
        fun onDaySelected(day: Date)
    }

}