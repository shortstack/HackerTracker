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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class DaySelectorView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val children = ArrayList<TextView>()
    private var listener: OnDaySelectedListener? = null

    init {
        View.inflate(context, R.layout.view_day_selector, this)

        for (i in 0..frame.childCount) {
            val view = frame.getChildAt(i)
            if (view is TextView) {
                children.add(view)
                view.setOnClickListener {
                    listener?.onDaySelected(Date((view.tag as String).toLong()))
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
        onDaySelected(view, START)
    }

    private fun onEndDaySelected(view: View) {
        onDaySelected(view, END)
    }

    fun addOnDaySelectedListener(listener: OnDaySelectedListener) {
        this.listener = listener
    }

    fun setDays(days: List<Date>) {
        Logger.d(days)

        children.clear()


        val format = SimpleDateFormat("MMM d")

        val constraintSet = ConstraintSet()
        constraintSet.clone(frame)


        val text = (LayoutInflater.from(context).inflate(R.layout.view_day, frame, false) as TextView).apply {
//        val text = TextView(context).apply {
            layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.MATCH_CONSTRAINT)
            text = "Test"
            id = 10000
        }


        frame.addView(text)

        constraintSet.apply {

//            addToVerticalChain(text.id, ConstraintSet.PARENT_ID, ConstraintSet.PARENT_ID)
//            addToHorizontalChain(text.id, ConstraintSet.PARENT_ID, 0)

            connect(text.id, START, PARENT_ID, START)
//            connect(text.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.UNSET)
            connect(text.id, TOP, PARENT_ID, TOP)
            connect(text.id, BOTTOM, PARENT_ID, BOTTOM)
        }


//
//
//
//
//        var previous = ConstraintSet.PARENT_ID
//        for (day in days) {
//
//
//
//            val view = LayoutInflater.from(context).inflate(R.layout.view_day, this, false) as TextView
//            view.id = day.hashCode()
//            view.text = format.format(day)
//            view.tag = day.time
//
//            constraintSet.apply {
//                if(previous == ConstraintSet.PARENT_ID)
//                    connect(view.id, ConstraintSet.START, previous, ConstraintSet.START)
//                else
//                    connect(view.id, ConstraintSet.START, previous, ConstraintSet.END)
//
//                connect(view.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
//                connect(view.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
//            }
//
//            frame.addView(view)
//
//            children.add(view)
//            view.setOnClickListener {
//                listener?.onDaySelected(Date(view.tag as Long))
//            }
//
//            previous = view.id
//        }

        val transition = ChangeBounds().apply {
            interpolator = AnticipateOvershootInterpolator(1.0f)
            duration = 500
        }

        TransitionManager.beginDelayedTransition(frame, transition)
        constraintSet.applyTo(frame)
    }

    interface OnDaySelectedListener {
        fun onDaySelected(day: Date)
    }

}