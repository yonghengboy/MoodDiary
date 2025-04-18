package com.example.mooddiary

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar

class SplashSettingsActivity : AppCompatActivity() {

    private lateinit var durationEdit: EditText
    private lateinit var splashSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { finish() }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        splashSwitch = findViewById(R.id.switchEnableSplash)
        durationEdit = findViewById(R.id.etSplashDuration)

        splashSwitch.isChecked = prefs.getBoolean("enable_splash", true)
        durationEdit.setText(prefs.getString("splash_duration", "3") ?: "3")

        splashSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_splash", isChecked).apply()
        }

        durationEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveDuration()
                true
            } else false
        }
    }

    override fun onPause() {
        super.onPause()
        saveDuration()
    }

    private fun saveDuration() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val input = durationEdit.text.toString().toIntOrNull()
        if (input != null && input in 1..10) {
            prefs.edit().putString("splash_duration", input.toString()).apply()
            Toast.makeText(this, "动画时长已保存：$input 秒", Toast.LENGTH_SHORT).show()
        } else {
            durationEdit.setText("3")
            prefs.edit().putString("splash_duration", "3").apply()
            Toast.makeText(this, "请输入 1~10 的数字，已重置为 3 秒", Toast.LENGTH_SHORT).show()
        }
    }
}