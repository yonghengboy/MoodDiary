package com.example.mooddiary

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup

class SettingsActivity : AppCompatActivity() {

    private lateinit var toggleTheme: MaterialButtonToggleGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ 在 setContentView 前应用主题
        applySavedTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // ✅ 设置自定义 Toolbar 为顶栏
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener {
            finish() // 返回上一个页面
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val currentTheme = prefs.getString("app_theme", "系统默认")
        toggleTheme = findViewById(R.id.toggleTheme)

        // 设置初始选中项
        when (currentTheme) {
            "系统默认" -> toggleTheme.check(R.id.btnThemeAuto)
            "浅色" -> toggleTheme.check(R.id.btnThemeLight)
            "深色" -> toggleTheme.check(R.id.btnThemeDark)
        }

        // ✅ 实时切换主题并立即生效
        toggleTheme.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val selectedTheme = when (checkedId) {
                    R.id.btnThemeAuto -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        "系统默认"
                    }
                    R.id.btnThemeLight -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "浅色"
                    }
                    R.id.btnThemeDark -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        "深色"
                    }
                    else -> "系统默认"
                }

                prefs.edit().putString("app_theme", selectedTheme).apply()
                recreate() // ✅ 刷新主题
            }
        }

        // 跳转到开屏设置
        findViewById<LinearLayout>(R.id.layoutSplashSettings).setOnClickListener {
            startActivity(Intent(this, SplashSettingsActivity::class.java))
        }

        // 跳转到生成设置
        findViewById<LinearLayout>(R.id.layoutGenerate).setOnClickListener {
            startActivity(Intent(this, SettingsGenerateActivity::class.java))
        }
    }

    // ✅ 应用保存的主题（必须放在 setContentView 之前）
    private fun applySavedTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString("app_theme", "系统默认")) {
            "浅色" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "深色" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
