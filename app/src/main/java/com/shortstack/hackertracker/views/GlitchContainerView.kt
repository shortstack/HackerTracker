package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.preference.PreferenceManager
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.ui.glitch.Glitch
import kotlin.random.Random


class GlitchContainerView(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout(context, attrs) {

    var isNormal = true
    var isRunning = true

    init {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("glitch_screen", false)
        ) {
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (isRunning) {
                        invalidate()

                        val delay = if (isNormal) {
                            100
                        } else {
                            100
                        }

                        handler.postDelayed(this, delay.toLong())

                        isNormal = Random.nextInt(100) > 5
                    }
                }
            }, 1000)

            isDrawingCacheEnabled = true
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isRunning = false
    }

    companion object {
        var isDrawing = false
    }

    override fun draw(canvas: Canvas?) {
        if (isDrawing || isNormal) {
            super.draw(canvas)
            return
        }

        if (canvas == null) {
            super.draw(canvas)
        } else {
            synchronized(this) {
                isDrawing = true
                isNormal = false
                val bitmap: Bitmap? = drawingCache
                if (bitmap != null) {
                    Glitch.apply(canvas, bitmap)
                    destroyDrawingCache()
                }
                isDrawing = false
            }
        }
    }
}
