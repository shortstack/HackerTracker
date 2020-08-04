package com.shortstack.hackertracker.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.glitch.Glitch
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage
import org.koin.core.KoinComponent
import org.koin.core.get


class SkullView : AppCompatImageView, KoinComponent {

    companion object {
        private const val LONG_DELAY = 5_000L
        private const val MEDIUM_DELAY = 1_500L
        private const val SHORT_DELAY = 150L
        private const val GLITCH_DURATION = 150L
    }

    private val bitmap: Bitmap

    private var isGlitch = false
    private var isRunning = true
    private var isNormal = true

    private lateinit var normalRunnable: Runnable
    private lateinit var glitchRunnable: Runnable

    constructor(context: Context) : super(context) {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.skull))
        bitmap = drawable.toBitmap()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setImageDrawable(getDrawable(attrs))
        bitmap = drawable.toBitmap()
    }

    private fun getDrawable(attrs: AttributeSet? = null): Drawable {
        if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SkullView, 0, 0)

            try {
                if (array.hasValue(R.styleable.SkullView_skullViewDrawable))
                    return ContextCompat.getDrawable(
                        context,
                        array.getResourceId(
                            R.styleable.SkullView_skullViewDrawable,
                            R.drawable.skull
                        )
                    )!!
            } finally {
                array.recycle()
            }
        }

        return ContextCompat.getDrawable(context, R.drawable.skull)!!
    }

    override fun getSuggestedMinimumWidth(): Int {
        return (drawable.intrinsicWidth.toFloat() * 2f).toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return (drawable.intrinsicHeight.toFloat() * 1.25f).toInt()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) {
            isGlitch = true
            return
        }

        val storage = get<Storage>()
        isGlitch =
            storage.theme == ThemesManager.Theme.SafeMode && storage.corruption > Storage.CorruptionLevel.MINOR

        if (isGlitch) {
            val delay = when (storage.corruption) {
                Storage.CorruptionLevel.NONE -> return
                Storage.CorruptionLevel.MINOR -> LONG_DELAY
                Storage.CorruptionLevel.MEDIUM -> MEDIUM_DELAY
                Storage.CorruptionLevel.MAJOR -> SHORT_DELAY
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

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas) {
        val isGlitch = isGlitch && !isNormal && isRunning

        synchronized(this) {
            Glitch.apply(canvas, bitmap, isGlitch)
        }
    }
}
