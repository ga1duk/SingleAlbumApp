package com.company.singlealbumapp.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.company.singlealbumapp.R
import kotlin.math.min


class PlayButton @JvmOverloads constructor(
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

    private val trianglePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2F
        color = resources.getColor(R.color.black)
        style = Paint.Style.FILL_AND_STROKE
    }

    private val path = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F
        center = PointF(w / 2F, h / 2F)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, circlePaint)

        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(center.x - 10, center.y + 18)
        path.lineTo(center.x - 10, center.y - 18)
        path.lineTo(center.x + 20, center.y)
        path.lineTo(center.x - 10, center.y + 18)
        path.close()

        canvas.drawPath(path, trianglePaint)
    }
}