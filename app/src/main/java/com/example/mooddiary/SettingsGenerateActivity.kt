package com.example.mooddiary

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar

class SettingsGenerateActivity : AppCompatActivity() {

    private lateinit var switchMood: SwitchCompat
    private lateinit var switchQuote: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_generate)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { finish() }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        switchMood = findViewById(R.id.switchEnableMood)
        switchQuote = findViewById(R.id.switchEnableQuote)

        switchMood.isChecked = prefs.getBoolean("enable_mood", true)
        switchQuote.isChecked = prefs.getBoolean("enable_recommendation", true)

        switchMood.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_mood", isChecked).apply()
            Toast.makeText(this, if (isChecked) "将生成心情" else "不再生成心情", Toast.LENGTH_SHORT).show()
        }

        switchQuote.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_recommendation", isChecked).apply()
            Toast.makeText(this, if (isChecked) "将生成推荐语" else "不再生成推荐语", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        saveSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun saveSettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit()
            .putBoolean("enable_mood", switchMood.isChecked)
            .putBoolean("enable_recommendation", switchQuote.isChecked)
            .apply()
    }
}