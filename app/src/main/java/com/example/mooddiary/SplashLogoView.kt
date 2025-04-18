package com.example.mooddiary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

// 自定义 View：绘制中心渐变圆环，作为启动页 Logo
class SplashLogoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradientColors = intArrayOf(
        Color.parseColor("#FF8A65"),  // 起始色（橙红）
        Color.parseColor("#00BCD4")   // 结束色（青蓝）
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 计算中心与半径
        val radius = (width.coerceAtMost(height)) / 2f - 10f
        val centerX = width / 2f
        val centerY = height / 2f

        // 创建 SweepGradient（旋转渐变）
        val gradient = SweepGradient(centerX, centerY, gradientColors, null)
        paint.shader = gradient
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f

        // 绘制圆环
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}
