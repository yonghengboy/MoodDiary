package com.example.mooddiary

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class GradientLightDividerView : View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var gradient: LinearGradient? = null
    private var shift = 0f
    private var animator: ObjectAnimator? = null

    // 兼容 Kotlin 1.8 的三个构造函数写法
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    // 初始化动画
    private fun init() {
        post {
            startLightAnimation()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        gradient = LinearGradient(
            -w + shift, 0f, shift, 0f,
            intArrayOf(Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        paint.shader = gradient
        canvas.drawRect(0f, 0f, w, h, paint)
    }

    // 为 ObjectAnimator 提供 getter/setter
    fun setShift(value: Float) {
        shift = value
        invalidate()
    }

    fun getShift(): Float {
        return shift
    }

    // 启动动画
    private fun startLightAnimation() {
        animator = ObjectAnimator.ofFloat(this, "shift", 0f, width.toFloat() * 2).apply {
            duration = 2000L
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            start()
        }
    }

    // 销毁时停止动画
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
