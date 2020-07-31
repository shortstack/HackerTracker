package com.shortstack.hackertracker.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.shortstack.hackertracker.R
import kotlin.random.Random


class SkullView : AppCompatImageView {

    enum class ColorChannel {
        RED,
        GREEN,
        BLUE
    }

    companion object {
        private val XFE_ADD = PorterDuffXfermode(PorterDuff.Mode.ADD)

        private const val WWIDTH: Int = 100
        private const val WHEIGHT: Int = 100
        private const val SMCOUNT: Int = (WWIDTH + 1) * (WHEIGHT + 1)
    }

    private val redPaint = getPaint(ColorChannel.RED)
    private val greenPaint = getPaint(ColorChannel.GREEN)
    private val bluePaint = getPaint(ColorChannel.BLUE)

    private val bitmap: Bitmap
    private val matrixOriginal = FloatArray(SMCOUNT * 2)

    // Tick Counter
    private var offset = 0

    private val maxHorizontalOffset: Float
        get() = bitmap.width * 0.25f

    private val maxVerticalOffset: Float
        get() = bitmap.height * 0.00f

    constructor(context: Context) : super(context) {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.skull))
        bitmap = drawable.toBitmap()
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setImageDrawable(getDrawable(attrs))
        bitmap = drawable.toBitmap()
        init()
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

    fun init() {
        initMatrix()

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                invalidate()
                handler.postDelayed(this, 150)
            }
        }, 1000)
    }

    override fun getSuggestedMinimumWidth(): Int {
        return (drawable.intrinsicWidth.toFloat() * 2f).toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return (drawable.intrinsicHeight.toFloat() * 1.25f).toInt()
    }

    private fun initMatrix() {
        val width = bitmap.width
        val height = bitmap.height

        var i = 0
        for (i2 in 0..(WHEIGHT)) {
            val f = ((height * i2).div(WHEIGHT)).toFloat()
            for (i3 in 0..(WWIDTH)) {
                val f2 = ((width * i3).div(WWIDTH)).toFloat()
                matrixOriginal[i * 2] = f2
                matrixOriginal[i * 2 + 1] = f
                i += 1
            }
        }
    }

    private fun modify(channel: ColorChannel): FloatArray {
        val random = Random(offset)

        val isNormal = random.nextBoolean()
        val areas = listOf(
            IntRange(0, 5000) to random.nextBoolean(),
            IntRange(5000, 10000) to random.nextBoolean(),
            IntRange(10000, 12000) to random.nextBoolean(),
            IntRange(12000, 18000) to random.nextBoolean()
        )


        val xOffset = random.nextFloat() * maxHorizontalOffset
        val yOffset = random.nextFloat() * maxVerticalOffset

        val matrix = matrixOriginal.clone()

        if (isNormal) {
            return matrix
        }

        synchronized(this) {
            for (index in 0 until (SMCOUNT * 2) step 2) {
                when (channel) {
                    ColorChannel.RED -> {
                        // shift left
                        matrix[index] = matrix[index] - xOffset
                        matrix[index + 1] = matrix[index + 1] + yOffset
                    }
                    ColorChannel.GREEN -> {
                        // do nothing
                    }
                    ColorChannel.BLUE -> {
                        // shift right
                        matrix[index] = matrix[index] + xOffset
                        matrix[index + 1] = matrix[index + 1] + yOffset
                    }
                }

                val pixelShift = areas.find { index in it.first }?.second ?: false

                if (pixelShift) {
                    matrix[index] = matrix[index] + Random(offset).nextInt(50)
                    if (Random(offset).nextBoolean()) {
                        matrix[index] = -matrix[index]
                    }
                }
            }

        }
        return matrix
    }

    override fun draw(canvas: Canvas?) {
        if (canvas == null) {
            super.draw(canvas)
            return
        }

        canvas.save()

        canvas.drawColor(Color.TRANSPARENT)

        canvas.translate((width / 2f - bitmap.width / 2f), (height / 2f - bitmap.height / 2f))

        canvas.drawBitmap(modify(ColorChannel.RED), redPaint)
        canvas.drawBitmap(modify(ColorChannel.GREEN), greenPaint)
        canvas.drawBitmap(modify(ColorChannel.BLUE), bluePaint)

        offset++

        if (offset > 35)
            offset = -15

        try {
            canvas.restore()
        } catch (ex: Exception) {

        }
    }

    private fun Canvas.drawBitmap(matrix: FloatArray, paint: Paint) {
        drawBitmapMesh(bitmap, WWIDTH, WHEIGHT, matrix, 0, null, 0, paint)
    }

    private fun getPaint(channel: ColorChannel): Paint {
        return Paint().apply {
            isFilterBitmap = true
            xfermode = XFE_ADD
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                set(getMatrix(channel))
            })
        }
    }

    private fun getMatrix(channel: ColorChannel): FloatArray {
        val matrix = Array(20) { 0.0f }
        val index = when (channel) {
            ColorChannel.RED -> 0
            ColorChannel.GREEN -> 6
            ColorChannel.BLUE -> 12
        }
        matrix[index] = 1.0f
        matrix[18] = 1.0f
        return matrix.toFloatArray()
    }
}
