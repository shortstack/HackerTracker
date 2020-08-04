package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.shortstack.hackertracker.ui.glitch.Glitch
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import com.shortstack.hackertracker.utilities.Storage.CorruptionLevel.*
import org.koin.core.KoinComponent
import org.koin.core.get
import kotlin.random.Random

class GlitchContainerView(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout(context, attrs), KoinComponent {

    private var isGlitch = false
    private var isNormal = true
    private var isRunning = true
    private var isDrawing = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) {
            isGlitch = false
            return
        }

        val storage = get<Storage>()
        isGlitch = false//storage.theme == ThemesManager.Theme.SafeMode && storage.corruption > MEDIUM

        if (isGlitch) {
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

    override fun draw(canvas: Canvas) {
        if (isDrawing || isNormal || !isGlitch) {
            super.draw(canvas)
            return
        }

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
