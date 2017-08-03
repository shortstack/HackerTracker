package com.shortstack.hackertracker.Renderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.header_home.view.*


class HomeHeaderRenderer : Renderer<Void>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.header_home, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView!!.logo.setOnClickListener { onSkullClick() }
    }

    override fun render(payloads: List<Any>) {
        // Do nothing.
    }

    fun onSkullClick() {
        // TODO Implement skull animation.
        val bitmap = getViewBitmap(rootView)

//        val bitmap = (rootView.logo.drawable as BitmapDrawable).bitmap

        val width = bitmap.width
        val height = bitmap.height

        //      val rgbval = IntArray(size = width * height + 1)

//        bitmap.getPixels(rgbval, 0, width, 0, 0, width, height)


//        for (i in 1..height step 2) {
//            for (t in 1..width) {
//                rgbval[i * width + t] = 0
//            }
//        }


        //bitmap.setPixels(rgbval, 0, width, 0, 0, width, height)


        val scalar = 16
        val small = getResizedBItmap(bitmap, width / scalar, height / scalar)
        val pixelated = getResizedBItmap(small, width, height)

        rootView!!.logo.setImageBitmap(pixelated)

        //bitmap.recycle()
        small.recycle()

        Handler().postDelayed({
            rootView.logo.setImageBitmap(bitmap)
            pixelated.recycle()
        }, 640)


        
    }


    fun getViewBitmap(view: View): Bitmap {
        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        val width = view.width
        val height = view.height

        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //Create a bitmap backed Canvas to draw the view into
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c)

        return b
    }

    fun getResizedBItmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false)
        return resizedBitmap
    }
}
