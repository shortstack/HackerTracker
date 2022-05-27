package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.glitch.Glitch
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import org.koin.core.KoinComponent
import org.koin.core.get

class GlitchContainerView(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout(context, attrs), KoinComponent {

    private var isGlitch = false
    private var corruption = GLITCH_NONE
    private var isOverriding = false

    private var isRunning = true
    private var isNormal = true

    // Keep track of if we're creating the cache or not.
    private var isDrawing = false

    private lateinit var normalRunnable: Runnable
    private lateinit var glitchRunnable: Runnable

    init {
        val array =
            context.theme.obtainStyledAttributes(attrs, R.styleable.GlitchContainerView, 0, 0)
        try {
            corruption =
                array.getInt(R.styleable.GlitchContainerView_glitch_corruptionLevel, GLITCH_NONE)
            isGlitch = array.getBoolean(R.styleable.GlitchContainerView_glitch_isGlitch, false)
            // if enabled via attrs, always use these settings
            isOverriding = isGlitch
        } finally {
            array.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) {
            isGlitch = false
            return
        }

        val delay = if (isOverriding) {
            getDelay(corruption)
        } else {
            val storage = get<Storage>()
            isGlitch =
                storage.theme == ThemesManager.Theme.SafeMode && storage.corruption > GLITCH_MEDIUM
            getDelay(storage.corruption)
        }

        if (isGlitch) {
            isDrawingCacheEnabled = true

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

    private fun getDelay(corruption: Int): Long {
        return when (corruption) {
            GLITCH_MINOR -> LONG_DELAY
            GLITCH_MEDIUM -> MEDIUM_DELAY
            GLITCH_MAJOR -> SHORT_DELAY
            else -> 0
        }
    }

    companion object {
        private const val GLITCH_NONE = 0
        private const val GLITCH_MINOR = 1
        private const val GLITCH_MEDIUM = 2
        private const val GLITCH_MAJOR = 3

        private const val LONG_DELAY = 5_000L
        private const val MEDIUM_DELAY = 1_500L
        private const val SHORT_DELAY = 1_500L
        private const val GLITCH_DURATION = 250L
    }
}
