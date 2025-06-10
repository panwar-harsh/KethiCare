package com.example.greendoc

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class SliderComparisonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var leftImage: Bitmap? = null
    private var rightImage: Bitmap? = null
    private var sliderPosition: Float = 0.5f // Default at center (50%)
    private val sliderPaint = Paint()

    init {
        sliderPaint.color = ContextCompat.getColor(context, android.R.color.holo_green_light)
        sliderPaint.strokeWidth = 8f
        sliderPaint.style = Paint.Style.FILL
    }

    fun setImages(left: Bitmap, right: Bitmap) {
        leftImage = left
        rightImage = right

        // Post ensures the view is measured before scaling
        post {
            if (width > 0 && height > 0) {
                scaleBitmaps()
            }
            invalidate()
        }
    }


    private fun scaleBitmaps() {
        if (width > 0 && height > 0 && leftImage != null && rightImage != null) {
            leftImage = leftImage?.let { Bitmap.createScaledBitmap(it, width, height, true) }
            rightImage = rightImage?.let { Bitmap.createScaledBitmap(it, width, height, true) }
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (leftImage == null || rightImage == null || width == 0 || height == 0) return

        val sliderX = (width * sliderPosition).toInt()

        // Ensure images are scaled correctly
        if (leftImage!!.width != width || leftImage!!.height != height) {
            scaleBitmaps()
        }

        // Draw left (unhealthy) image
        canvas.drawBitmap(leftImage!!, 0f, 0f, null)

        // Clip and draw right (healthy) image
        canvas.save()
        canvas.clipRect(sliderX, 0, width, height)
        canvas.drawBitmap(rightImage!!, 0f, 0f, null)
        canvas.restore()

        // Draw slider line
        canvas.drawLine(sliderX.toFloat(), 0f, sliderX.toFloat(), height.toFloat(), sliderPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                sliderPosition = (event.x / width).coerceIn(0f, 1f)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
