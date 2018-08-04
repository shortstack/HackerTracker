package com.shortstack.hackertracker.views

import android.content.Context
import androidx.legacy.widget.Space
import android.util.AttributeSet

/**
 * Created by Chris on 6/3/2018.
 */
class StatusBarSpacer(context: Context, attrs: AttributeSet?) : androidx.legacy.widget.Space(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, measureHeight(heightMeasureSpec))
    }

    private fun measureHeight(measureSpec: Int): Int {
        return getMeasurement(measureSpec, getStatusBarHeight())
    }

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec);

        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> Math.min(preferred, specSize)
            MeasureSpec.UNSPECIFIED -> preferred
            else -> preferred
        }
    }

    private fun getStatusBarHeight(): Int {
        if (isInEditMode) return 0

        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}