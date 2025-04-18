package com.example.mooddiary

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SplashActivity : AppCompatActivity() {

    private var isSkipped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 读取设置：是否启用开屏页面
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val enableSplash = prefs.getBoolean("enable_splash", true)

        if (!enableSplash) {
            goToMain()
            return
        }

        // 设置内容视图
        setContentView(R.layout.activity_splash)

        // 读取动画时长（秒）并转换为毫秒，默认 3 秒，最大不超过 10 秒
        val durationSec = prefs.getString("splash_duration", "3")?.toIntOrNull()?.coerceIn(1, 10) ?: 3
        val splashDuration = durationSec * 1000L

        // 设置标题文字淡入动画
        val titleText = findViewById<TextView>(R.id.appTitle)
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1200
            fillAfter = true
        }
        titleText.startAnimation(fadeIn)

        // Logo 动画视图
        val logoView = findViewById<SplashLogoView>(R.id.splashLogoView)

        // 启动 Logo 自定义 sweep 动画（自定义 View 中实现）
        logoView.startSweepAnimation(splashDuration)

        // 启动旋转+缩放+透明度组合动画
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(logoView, "scaleX", 0.7f, 1f),
                ObjectAnimator.ofFloat(logoView, "scaleY", 0.7f, 1f),
                ObjectAnimator.ofFloat(logoView, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(logoView, "rotation", 0f, 360f)
            )
            duration = splashDuration
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        // 设置“跳过”按钮点击逻辑
        val skipButton = findViewById<Button>(R.id.btn_skip)
        skipButton.setOnClickListener {
            if (!isSkipped) {
                isSkipped = true
                goToMain()
            }
        }

        // 动画结束后跳转主界面（若未点击跳过）
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isSkipped) {
                goToMain()
            }
        }, splashDuration)
    }

    // 跳转主界面方法
    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
