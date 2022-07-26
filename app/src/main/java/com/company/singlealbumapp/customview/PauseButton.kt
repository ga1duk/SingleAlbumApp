package com.company.singlealbumapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.company.singlealbumapp.R
import kotlin.math.min


private const val CUSTOM_STROKE_WIDTH = 10F
private const val Y_AXIS_OFFSET = 15
private const val X_AXIS_OFFSET = 8

class PauseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context, attrs, defStyleAttr
) {

    private var radius: Float = 0F
    private var center: PointF = PointF()

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.yellow)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = CUSTOM_STROKE_WIDTH
        color = ContextCompat.getColor(context, R.color.black)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F
        center = PointF(w / 2F, h / 2F)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, circlePaint)
        canvas.drawLine(
            center.x + X_AXIS_OFFSET,
            center.y - Y_AXIS_OFFSET,
            center.x + X_AXIS_OFFSET,
            center.y + Y_AXIS_OFFSET,
            linePaint
        )
        canvas.drawLine(
            center.x - X_AXIS_OFFSET,
            center.y - Y_AXIS_OFFSET,
            center.x - X_AXIS_OFFSET,
            center.y + Y_AXIS_OFFSET,
            linePaint
        )
    }
}