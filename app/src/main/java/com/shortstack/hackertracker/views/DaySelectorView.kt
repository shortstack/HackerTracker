package com.shortstack.hackertracker.views

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.START
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.shortstack.hackertracker.databinding.ViewDaySelectorBinding
import java.text.SimpleDateFormat
import java.util.*


class DaySelectorView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val binding = ViewDaySelectorBinding.inflate(LayoutInflater.from(context), this, true)

    private val children = ArrayList<TextView>()
    private var listener: OnDaySelectedListener? = null

    private var begin: Int = -1
    private var end: Int = -1

    init {
        for (i in 0..binding.frame.childCount) {
            val view = binding.frame.getChildAt(i)
            if (view is TextView) {
                children.add(view)
                view.setOnClickListener {
                    val tag = view.tag as? Long
                    if (tag != null) {
                        listener?.onDaySelected(Date(tag))
                    }
                }
            }
        }
    }

    private fun onDaySelected(view: View, position: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.frame)

        constraintSet.apply {
            connect(binding.bubble.id, position, view.id, position)
        }


        val transition = ChangeBounds().apply {
            interpolator = AnticipateOvershootInterpolator(1.0f)
            duration = 500
        }

        TransitionManager.beginDelayedTransition(binding.frame, transition)
        constraintSet.applyTo(binding.frame)
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
        if (beginIndex != -1) {
            val view = getViewByIndex(beginIndex)
            onBeginDaySelected(view)
            setCenter(view)
        }

        val endIndex = getDayIndex(dates, endDay)
        if (endIndex != -1) {
            onEndDaySelected(getViewByIndex(endIndex))
        }
    }

    private fun setCenter(view: View) {
        val screenWidth = (context as Activity).windowManager
            .defaultDisplay.width

        val scrollX = view.left - screenWidth / 2 + view.width / 2

        val animator = ObjectAnimator.ofInt(binding.root, "scrollX", scrollX)
        animator.duration = 300
        animator.start()
    }

    private fun getViewByIndex(index: Int): View = with(binding) {
        return when (index) {
            0 -> day1
            1 -> day2
            2 -> day3
            3 -> day4
            4 -> day5
            5 -> day6
            6 -> day7
            7 -> day8
            8 -> day9
            9 -> day10
            10 -> day11
            11 -> day12
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


        for (index in 0..11) {
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

        if (begin == -1 && end == -1) {
            onBeginDaySelected(children[0])
            onEndDaySelected(children[0])
        }
    }

    interface OnDaySelectedListener {
        fun onDaySelected(day: Date)
    }

}