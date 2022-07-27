package com.company.singlealbumapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.company.singlealbumapp.R
import kotlin.math.min


class PauseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context, attrs, defStyleAttr, defStyleRes
) {

    private var radius: Float = 0F
    private var center: PointF = PointF()

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.yellow)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 10F
        color = resources.getColor(R.color.black)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F
        center = PointF(w / 2F, h / 2F)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, circlePaint)
        canvas.drawLine(center.x + 8, center.y - 15, center.x + 8, center.y + 15, linePaint)
        canvas.drawLine(center.x - 8, center.y - 15, center.x - 8, center.y + 15, linePaint)
    }
}