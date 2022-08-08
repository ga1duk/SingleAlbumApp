package com.company.singlealbumapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.company.singlealbumapp.R
import kotlin.math.min


private const val CUSTOM_STROKE_WIDTH = 2F
private const val Y_AXIS_OFFSET = 18
private const val X_AXIS_OFFSET = 10
private const val X_AXIS_OFFSET_ENLARGED = 20

class PlayButton @JvmOverloads constructor(
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

    private val trianglePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = CUSTOM_STROKE_WIDTH
        color = ContextCompat.getColor(context, R.color.black)
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
        path.moveTo(center.x - X_AXIS_OFFSET, center.y + Y_AXIS_OFFSET)
        path.lineTo(center.x - X_AXIS_OFFSET, center.y - Y_AXIS_OFFSET)
        path.lineTo(center.x + X_AXIS_OFFSET_ENLARGED, center.y)
        path.lineTo(center.x - X_AXIS_OFFSET, center.y + Y_AXIS_OFFSET)
        path.close()

        canvas.drawPath(path, trianglePaint)
    }
}