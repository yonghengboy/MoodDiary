package com.example.mooddiary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class GradientTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    override fun onDraw(canvas: Canvas) {
        // 创建线性渐变颜色（从左到右）
        val paint = paint
        val text = text.toString()
        val width = paint.measureText(text)

        val shader = LinearGradient(
            0f, 0f, width, textSize,
            intArrayOf(Color.parseColor("#FF4081"), Color.parseColor("#3F51B5")),
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        super.onDraw(canvas)
    }
}
