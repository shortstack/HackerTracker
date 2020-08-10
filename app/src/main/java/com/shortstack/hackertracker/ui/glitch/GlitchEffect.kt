package com.shortstack.hackertracker.ui.glitch

import android.graphics.Bitmap
import android.graphics.Canvas

interface GlitchEffect {

    fun apply(canvas: Canvas, bitmap: Bitmap, isGlitch: Boolean = true)

}