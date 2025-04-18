package com.example.mooddiary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 获取标题 TextView 并设置淡入动画
        val titleText = findViewById<TextView>(R.id.appTitle)
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1200      // 淡入时长
            fillAfter = true     // 动画结束后保留状态
        }
        titleText.startAnimation(fadeIn)

        // 延迟 2 秒后跳转主界面
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
