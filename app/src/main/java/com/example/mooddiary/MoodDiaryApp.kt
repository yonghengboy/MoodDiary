package com.example.mooddiary

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class MoodDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // 获取保存的主题偏好设置
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString("app_theme", "系统默认")) {
            "浅色" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "深色" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
