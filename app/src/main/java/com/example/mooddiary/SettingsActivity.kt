package com.example.mooddiary

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // 加载用户设置的主题
        prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        applyTheme(prefs.getString("theme_mode", "auto") ?: "auto")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val radioGroup = findViewById<RadioGroup>(R.id.radioTheme)
        val backBtn = findViewById<Button>(R.id.btnBack)

        // 根据当前设置回显选择
        when (prefs.getString("theme_mode", "auto")) {
            "light" -> radioGroup.check(R.id.radioLight)
            "dark" -> radioGroup.check(R.id.radioDark)
            else -> radioGroup.check(R.id.radioAuto)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selected = when (checkedId) {
                R.id.radioLight -> "light"
                R.id.radioDark -> "dark"
                else -> "auto"
            }

            // 保存设置
            prefs.edit().putString("theme_mode", selected).apply()

            // 应用主题设置
            applyTheme(selected)

            Toast.makeText(this, "主题已更改，请重新启动 App 生效", Toast.LENGTH_LONG).show()
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    // 应用主题
    private fun applyTheme(mode: String) {
        when (mode) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
