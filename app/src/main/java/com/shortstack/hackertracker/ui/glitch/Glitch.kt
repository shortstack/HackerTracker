package com.shortstack.hackertracker.ui.glitch

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import org.koin.core.KoinComponent
import org.koin.core.inject

object Glitch : KoinComponent {

    private val storage: Storage by inject()

    fun apply(canvas: Canvas, bitmap: Bitmap) {
        if (storage.theme != ThemesManager.Theme.SafeMode)
            return

        if (storage.corruption == Storage.CorruptionLevel.NONE)
            return

        val effect = ColorChannelShift(bitmap)

        effect.apply(canvas, bitmap)
    }

    var layout: Bitmap? = null

    fun apply(canvas: Canvas, view: View) {
        if (storage.theme != ThemesManager.Theme.SafeMode)
            return

        if (storage.corruption != Storage.CorruptionLevel.MEDIUM && storage.corruption != Storage.CorruptionLevel.MAJOR)
            return

        if (layout == null) {
            layout = convertLayout(view)
        }

        val bitmap = layout
        if (bitmap != null) {
            val effect = ColorChannelShift(bitmap)
            effect.apply(canvas, bitmap)
            //bitmap.recycle()
        }
    }


    private fun convertLayout(_view: View): Bitmap? {
        _view.isDrawingCacheEnabled = true
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
        _view.layout(0, 0, _view.measuredWidth, _view.measuredHeight)
        _view.buildDrawingCache(true)
        val drawingCache = _view.drawingCache ?: return null

        return Bitmap.createScaledBitmap(drawingCache, 400, 780, true)
    }
}