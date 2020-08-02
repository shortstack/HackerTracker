package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.ui.glitch.Glitch
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage.CorruptionLevel.*
import kotlin.random.Random

class GlitchContainerView(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout(context, attrs) {

    private val storage = App.instance.storage
    private val glitch: Boolean = storage.theme == ThemesManager.Theme.SafeMode

    var isNormal = true
    var isRunning = true

    init {
        if (glitch) {
            val chance = when (storage.corruption) {
                NONE -> 0
                MINOR -> 0
                MEDIUM -> 5 // 0.5%
                MAJOR -> 30 // 3.0%
            }

            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (isRunning) {
                        invalidate()
                        handler.postDelayed(this, 100L)
                        isNormal = Random.nextInt(1000) > chance
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
        if (isDrawing || isNormal || !glitch) {
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
