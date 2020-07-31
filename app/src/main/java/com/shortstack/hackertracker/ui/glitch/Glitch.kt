package com.shortstack.hackertracker.ui.glitch

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

object Glitch {

    fun apply(canvas: Canvas, bitmap: Bitmap) {
        val effect = ColorChannelShift(bitmap)

        effect.apply(canvas, bitmap)
    }

    var layout: Bitmap? = null

    fun apply(canvas: Canvas, view: View) {

        if(layout == null) {
            layout = convert_layout(view)
        }

        val bitmap = layout
        if (bitmap != null) {
            val effect = ColorChannelShift(bitmap)
            effect.apply(canvas, bitmap)
            //bitmap.recycle()
        }
    }

    fun convert_layout(_view: View): Bitmap? {
        _view.setDrawingCacheEnabled(true)
        _view.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        _view.layout(0, 0, _view.getMeasuredWidth(), _view.getMeasuredHeight())
        _view.buildDrawingCache(true)
        val drawingCache = _view.getDrawingCache()
        if (drawingCache == null)
            return null

        return Bitmap.createScaledBitmap(drawingCache, 400, 780, true)
    }
}