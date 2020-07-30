package com.shortstack.hackertracker.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.drawable.toBitmap
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R
import kotlin.random.Random


class SkullView : FrameLayout {

    companion object {
        private const val CHANNEL_RED = 0xFFFF0000
        private const val CHANNEL_GREEN = 0xFF00FF00
        private const val CHANNEL_BLUE = 0xFF0000FF

        private val XFE_ADD = PorterDuffXfermode(PorterDuff.Mode.ADD)
    }

    private val redMatrix = floatArrayOf(
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f
    )
    private val blueMatrix = floatArrayOf(
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f
    )
    private val greenMatrix = floatArrayOf(
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f
    )

    private val redPaint = Paint()
    private val greenPaint = Paint()
    private val bluePaint = Paint()


    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    val drawable = context.getDrawable(R.drawable.doggo)!!
    val bitmap = drawable.toBitmap()

    var canDraw = false

    private val malpha: Int = 150

    var w = bitmap.width
    var h = bitmap.height

    private var WWIDTH: Int = 10
    private var WHEIGHT: Int = 10

    private var SMCOUNT: Int = 0
    private var matrixVertsMoved = kotlin.FloatArray(0)
    private var matrixOriginal = kotlin.FloatArray(0)

    init {
//        View.inflate(context, R.layout.view_skull, this)

        initGhost()

        canDraw = true

        val h = Handler()
        h.postDelayed(object : Runnable {
            override fun run() {
                // do stuff then
                // can call h again after work!
                invalidate()

                h.postDelayed(this, 30)
            }
        }, 1000) // 1 second delay (takes millis)


    }

    private fun initGhost() {
        WWIDTH = 100
        WHEIGHT = 100
        InitSmudgeMatrix()
        setGhostColor()
    }

    private fun setGhostColor() {
        redPaint.isFilterBitmap = true
        redPaint.xfermode = XFE_ADD
        greenPaint.isFilterBitmap = true
        greenPaint.xfermode = XFE_ADD
        bluePaint.isFilterBitmap = true
        bluePaint.xfermode = XFE_ADD

        val colorMatrix = ColorMatrix()

        colorMatrix.set(redMatrix)
        redPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        colorMatrix.set(greenMatrix)
        greenPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        colorMatrix.set(blueMatrix)
        bluePaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    }

    private fun InitSmudgeMatrix() {
        SMCOUNT = (WWIDTH + 1) * (WHEIGHT + 1)
        matrixVertsMoved = FloatArray(SMCOUNT * 2)
        matrixOriginal = FloatArray(SMCOUNT * 2)

        var i = 0
        for (i2 in 0..(WHEIGHT)) {
            val f = ((h * i2).div(WHEIGHT)).toFloat()
            for (i3 in 0..(WWIDTH)) {
                val f2 = ((w * i3).div(WWIDTH)).toFloat()
                setXY(matrixVertsMoved, i, f2, f)
                setXY(matrixOriginal, i, f2, f)
                i += 1
            }
        }
    }

    private fun setXY(fArr: FloatArray, i: Int, f: Float, f2: Float) {
        fArr[i * 2] = f
        fArr[i * 2 + 1] = f2
    }

    var offset = 0


    private fun smudgeGhostRGB(left: Int, right: Int, i3: Int, i4: Int, motion: Int): FloatArray? {

        val random = Random

        var isNormal = false//offset < 0


        val MAX_HORIZONTAL_OFFSET = w * .0060f
        val MAX_VERTICAL_OFFSET = h * 0.0030f

        val xOffset = offset * MAX_HORIZONTAL_OFFSET//random.nextInt(MAX_HORIZONTAL_OFFSET.toInt())
        val yOffset = offset * MAX_VERTICAL_OFFSET //random.nextInt(MAX_VERTICAL_OFFSET.toInt())

        Logger.d("Drawing with offset: $xOffset, $yOffset")

        var fArr = FloatArray(0)
        synchronized(this) {
            fArr = kotlin.FloatArray(SMCOUNT * 2)
            for (i5 in 0..((SMCOUNT * 2) - 1) step 2) {
                //Log.d("DEBUG","$i $i2 $i3 $i4 $i5")

                val xOriginal = matrixOriginal[i5]
                val yOriginal = matrixOriginal[i5 + 1]

                if (isNormal)
                    continue


                val distX = ((left.toFloat() - xOriginal) / w.toFloat()) * 10.0f
                val distY = ((right.toFloat() - yOriginal) / h.toFloat()) * 10.0f
                val d = ((i4.toFloat() / 255.0f).toDouble() * 3.6) + 0.4

                val gaussX = Math.exp((-(distX * distX)).toDouble() / d).toFloat() * 0.4f
                val gaussY = Math.exp((-(distY * distY)).toDouble() / d).toFloat() * 0.4f

                //Log.d("DEBUG","$xOriginal $yOriginal $distX $distY $d $gaussX $gaussY")

                when (motion) {
                    0 -> {
                        fArr[i5] = left + xOriginal - xOffset
                        fArr[i5 + 1] = right + yOriginal - yOffset
                    }
                    1 -> {
                        fArr[i5] = left + xOriginal - xOffset
                        fArr[i5 + 1] = right + yOriginal + yOffset
                    }
                    2 -> {
                        fArr[i5] = left + xOriginal + xOffset
                        fArr[i5 + 1] = right + yOriginal + yOffset
                    }
                    3 -> {
                        fArr[i5] = left + xOriginal + xOffset
                        fArr[i5 + 1] = right + yOriginal - yOffset
                    }
                }


//                when (motion) {
//                    0 -> {
//                        fArr[i5] = xOriginal - (((w - i).toFloat() * gaussY) / i3.toFloat())
//                        fArr[i5 + 1] = yOriginal
//                    }
//                    1 -> {
//                        fArr[i5] = xOriginal + ((i.toFloat() * gaussY) / i3.toFloat())
//                        fArr[i5 + 1] = yOriginal
//                    }
//                    2 -> {
//                        fArr[i5] = xOriginal
//                        fArr[i5 + 1] = yOriginal - (((h - i2).toFloat() * gaussX) / i3.toFloat())
//                    }
//                    3 -> {
//                        fArr[i5] = xOriginal
//                        fArr[i5 + 1] = yOriginal + ((i2.toFloat() * gaussX) / i3.toFloat())
//                    }
//                    else -> {
//                        fArr[i5] = xOriginal
//                        fArr[i5 + 1] = yOriginal
//                    }
//                }

            }

        }
        return fArr
    }


    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas?) {
        Logger.d("onDraw!")
        if (!canDraw || canvas == null) {
            Logger.e("Cannot draw right now.")
            super.draw(canvas)
            return
        }

        canvas.save()

        // ghost
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        val left = width / 2f - bitmap.width / 2f
        val right = height / 2f - bitmap.height / 2f

        canvas.drawBitmapMesh(
            bitmap,
            WWIDTH,
            WHEIGHT,
            smudgeGhostRGB(left.toInt(), right.toInt(), 2, malpha, 0)!!,
            0,
            null,
            0,
            redPaint
        )

        canvas.drawBitmapMesh(
            bitmap,
            WWIDTH,
            WHEIGHT,
            smudgeGhostRGB(left.toInt(), right.toInt(), 2, malpha, 1)!!,
            0,
            null,
            0,
            greenPaint
        )
        canvas.drawBitmapMesh(
            bitmap,
            WWIDTH,
            WHEIGHT,
            smudgeGhostRGB(left.toInt(), right.toInt(), 4, malpha, 2)!!,
            0,
            null,
            0,
            bluePaint
        )

        offset++

        if (offset > 35)
            offset = -15

        //super.draw(canvas)
        try {
            canvas.restore()
        } catch (ex: Exception) {

        }
    }
}
