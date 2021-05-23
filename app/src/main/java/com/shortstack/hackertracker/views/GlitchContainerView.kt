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

class GlitchContainerView(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout(context, attrs), KoinComponent {

    private var isGlitch = false
    private var isRunning = true
    private var isNormal = true

    // Keep track of if we're creating the cache or not.
    private var isDrawing = false

    private lateinit var normalRunnable: Runnable
    private lateinit var glitchRunnable: Runnable

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) {
            isGlitch = false
            return
        }

        val storage = get<Storage>()
        isGlitch = storage.theme == ThemesManager.Theme.SafeMode && storage.corruption > MEDIUM

        if (isGlitch) {
            isDrawingCacheEnabled = true

            val delay = when (storage.corruption) {
                NONE -> return
                MINOR -> LONG_DELAY
                MEDIUM -> MEDIUM_DELAY
                MAJOR -> SHORT_DELAY
            }

            val handler = Handler()
            normalRunnable = Runnable {
                if (isRunning) {
                    isNormal = true
                    invalidate()
                    handler.postDelayed(glitchRunnable, delay)
                }
            }

            glitchRunnable = Runnable {
                if (isRunning) {
                    isNormal = false
                    invalidate()
                    handler.postDelayed(normalRunnable, GLITCH_DURATION)
                }
            }

            handler.postDelayed(normalRunnable, delay)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isRunning = false
    }


    override fun draw(canvas: Canvas) {
        if (isDrawing || !isGlitch || isNormal) {
            super.draw(canvas)
            return
        }

        synchronized(this) {
            isDrawing = true
            val bitmap: Bitmap? = drawingCache
            if (bitmap != null) {
                Glitch.apply(canvas, bitmap, isGlitch = true)
                destroyDrawingCache()
            }
            isDrawing = false
        }
    }

    companion object {
        private const val LONG_DELAY = 5_000L
        private const val MEDIUM_DELAY = 1_500L
        private const val SHORT_DELAY = 1_500L
        private const val GLITCH_DURATION = 250L
    }
}
