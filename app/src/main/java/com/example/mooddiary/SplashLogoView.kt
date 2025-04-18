package com.example.mooddiary

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

// 启动画面 Logo 动画 View：带渐变光环 + 动态旋转弧
class SplashLogoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 20f
        style = Paint.Style.STROKE
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val arcRect = RectF()
    private var currentAngle = 0f

    // 渐变颜色（你原有的橙红 + 青蓝）
    private val gradientColors = intArrayOf(
        Color.parseColor("#FF8A65"),
        Color.parseColor("#00BCD4"),
        Color.parseColor("#FF8A65") // 收尾闭环
    )

    // 启动动画（外部控制时长）
    fun startSweepAnimation(duration: Long = 3000L) {
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.duration = duration
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            currentAngle = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = (width.coerceAtMost(height)) / 2f - 20f
        val cx = width / 2f
        val cy = height / 2f

        // 渐变环背景
        val shader = SweepGradient(cx, cy, gradientColors, null)
        circlePaint.shader = shader
        canvas.drawCircle(cx, cy, radius, circlePaint)

        // 绘制动态旋转光弧（加亮边缘）
        arcPaint.shader = null
        arcPaint.color = Color.WHITE
        arcRect.set(cx - radius, cy - radius, cx + radius, cy + radius)
        canvas.drawArc(arcRect, currentAngle, 36f, false, arcPaint)
    }
}
