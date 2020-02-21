package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View

class StatusBarSpacer(context: Context, attrs: AttributeSet?) : androidx.legacy.widget.Space(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, measureHeight(heightMeasureSpec))
    }

    private fun measureHeight(measureSpec: Int): Int {
        return getMeasurement(measureSpec, getStatusBarHeight(context, this))
    }

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> Math.min(preferred, specSize)
            MeasureSpec.UNSPECIFIED -> preferred
            else -> preferred
        }
    }

    companion object {
        fun getStatusBarHeight(context: Context, view: View): Int {
            if (view.isInEditMode) return 52

            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }
    }
}