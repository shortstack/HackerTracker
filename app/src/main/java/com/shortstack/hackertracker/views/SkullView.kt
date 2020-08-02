package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.glitch.Glitch
import com.shortstack.hackertracker.ui.themes.ThemesManager
import com.shortstack.hackertracker.utilities.Storage


class SkullView : AppCompatImageView {

    private val bitmap: Bitmap

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

    private val storage = App.instance.storage
    private val glitch: Boolean =
        storage.theme == ThemesManager.Theme.SafeMode && storage.corruption > Storage.CorruptionLevel.MINOR

    private var isRunning = true

    init {
        if (glitch) {
            initHandler()
        }
    }

    private fun initHandler() {
        val delay = when (storage.corruption) {
            Storage.CorruptionLevel.NONE -> return
            Storage.CorruptionLevel.MINOR -> 5_000L
            Storage.CorruptionLevel.MEDIUM -> 1_000L
            Storage.CorruptionLevel.MAJOR -> 150L
        }

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isRunning) {
                    invalidate()
                    handler.postDelayed(this, delay)
                }
            }
        }, delay)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isRunning = false
    }

    companion object {
        var isDrawing = false
    }

    override fun draw(canvas: Canvas?) {
        if (!glitch || isDrawing || !isRunning) {
            super.draw(canvas)
            return
        }

        if (canvas == null) {
            super.draw(canvas)
        } else {
            synchronized(this) {
                isDrawing = true
                Glitch.apply(canvas, bitmap)
                isDrawing = false
            }
        }
    }
}
