package com.example.mooddiary

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var globalPrefs: SharedPreferences  // 全局设置

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide() // ✅ 隐藏默认顶部标题栏

        copyDatabaseFromAssetsIfNeeded()

        prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
        globalPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        val etLocation = findViewById<EditText>(R.id.etLocation)
        val etFace = findViewById<EditText>(R.id.etFace)
        val cbRandomFace = findViewById<CheckBox>(R.id.cbRandomFace)
        val spMood = findViewById<Spinner>(R.id.spMoodSelect)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnCopy = findViewById<Button>(R.id.btnCopy)

        etLocation.setText(prefs.getString("location", "家"))
        etFace.setText(prefs.getString("customFace", ""))
        cbRandomFace.isChecked = prefs.getBoolean("useRandomFace", true)

        ArrayAdapter.createFromResource(
            this,
            R.array.mood_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spMood.adapter = adapter
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            prefs.edit().apply {
                putString("location", etLocation.text.toString())
                putString("customFace", etFace.text.toString())
                putBoolean("useRandomFace", cbRandomFace.isChecked)
                apply()
            }
            Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnGenerate).setOnClickListener {
            val location = etLocation.text.toString()
            val customFace = etFace.text.toString()
            val randomFace = cbRandomFace.isChecked
            val selectedMood = spMood.selectedItem.toString()
            val finalMood = if (selectedMood == "（自动判断）") inferMood(location) else selectedMood

            val face = if (randomFace) getFromDB(finalMood, "face").randomOrNull() ?: "（・・？）" else customFace
            val quote = getFromDB(finalMood, "quote").randomOrNull() ?: "愿你拥有平静的一天。"

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val isLate = hour in 0..12
            if (isLate) calendar.add(Calendar.DAY_OF_MONTH, -1)

            val date = SimpleDateFormat("MM.dd.u", Locale.CHINA).format(calendar.time)
            val time = if (isLate) "次${hour}.${minute.toString().padStart(2, '0')}" else "${hour}.${minute.toString().padStart(2, '0')}"
            val header = "2025.$date/$time/${location}启/$face"
            val footer = "$time/${location}完"

            val enableMood = globalPrefs.getBoolean("enable_mood", true)
            val enableQuote = globalPrefs.getBoolean("enable_recommendation", true)

            val moodLine = if (enableMood) "心情：$finalMood\n" else ""
            val quoteLine = if (enableQuote) "推荐语：$quote\n" else ""

            val result = "$header\n$quoteLine$moodLine$footer"
            tvResult.text = result
        }

        btnCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("diary", tvResult.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show()

            btnCopy.animate().apply {
                duration = 300
                scaleX(1.1f)
                scaleY(1.1f)
            }.withEndAction {
                btnCopy.animate().apply {
                    duration = 300
                    scaleX(1f)
                    scaleY(1f)
                }
            }.start()
        }

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun copyDatabaseFromAssetsIfNeeded() {
        val dbName = "mood_assets.db"
        val dbPath = getDatabasePath(dbName)
        if (!dbPath.exists()) {
            try {
                val inputStream = assets.open(dbName)
                val outputDir = dbPath.parentFile
                if (outputDir != null && !outputDir.exists()) outputDir.mkdirs()
                val outputStream = FileOutputStream(dbPath)
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "数据库复制失败：${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getFromDB(mood: String, type: String): List<String> {
        val dbPath = getDatabasePath("mood_assets.db").path
        val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor = db.rawQuery("SELECT content FROM MoodAssets WHERE mood=? AND type=?", arrayOf(mood, type))
        val result = mutableListOf<String>()
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return result
    }

    private fun inferMood(location: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 0..3 -> "堕落"
            "公司" in location || "学校" in location -> "疲惫"
            hour in 6..8 -> "沉思"
            hour in 9..17 -> "忙碌"
            hour in 18..20 -> "放松"
            hour in 21..23 -> "开心"
            else -> "平静"
        }
    }
}
