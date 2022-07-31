package com.advice.schedule.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class StatusBarSpacer(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, measureHeight(heightMeasureSpec))
    }

    private fun measureHeight(measureSpec: Int): Int {
        return getMeasurement(measureSpec, getStatusBarHeight(context, this))
    }

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        val specSize = View.MeasureSpec.getSize(measureSpec)

        return when (View.MeasureSpec.getMode(measureSpec)) {
            View.MeasureSpec.EXACTLY -> specSize
            View.MeasureSpec.AT_MOST -> Math.min(preferred, specSize)
            MeasureSpec.UNSPECIFIED -> preferred
            else -> preferred
        }
    }

    companion object {
        fun getStatusBarHeight(context: Context, view: View): Int {
            if (view.isInEditMode) return 52

            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }
    }
}