package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipeableViewPager(context: Context, attrs: AttributeSet) : androidx.viewpager.widget.ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}