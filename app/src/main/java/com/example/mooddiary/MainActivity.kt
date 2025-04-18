package com.example.mooddiary

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences // 用于存储用户设置的 SharedPreferences 对象

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 如果首次启动，复制数据库文件到设备
        copyDatabaseFromAssetsIfNeeded()

        // 获取用于保存设置的 SharedPreferences
        prefs = getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)

        // 初始化界面组件
        val etLocation = findViewById<EditText>(R.id.etLocation)        // 地点输入框
        val etFace = findViewById<EditText>(R.id.etFace)                // 颜文字输入框
        val cbRandomFace = findViewById<CheckBox>(R.id.cbRandomFace)    // 随机表情勾选框
        val spMood = findViewById<Spinner>(R.id.spMoodSelect)           // 心情选择下拉列表
        val tvResult = findViewById<TextView>(R.id.tvResult)            // 显示生成结果的文本框
        val btnCopy = findViewById<Button>(R.id.btnCopy)                // 复制按钮

        // 加载之前保存的用户设置，并初始化界面显示
        etLocation.setText(prefs.getString("location", "家"))
        etFace.setText(prefs.getString("customFace", ""))
        cbRandomFace.isChecked = prefs.getBoolean("useRandomFace", true)

        // 为心情选择的 Spinner 设置数据源
        ArrayAdapter.createFromResource(
            this,
            R.array.mood_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spMood.adapter = adapter
        }

        // 保存用户设置到 SharedPreferences
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            prefs.edit().apply {
                putString("location", etLocation.text.toString())
                putString("customFace", etFace.text.toString())
                putBoolean("useRandomFace", cbRandomFace.isChecked)
                apply()
            }
            Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show()
        }

        // 生成日记按钮点击事件
        findViewById<Button>(R.id.btnGenerate).setOnClickListener {
            val location = etLocation.text.toString()         // 获取地点
            val customFace = etFace.text.toString()           // 获取用户自定义颜文字
            val randomFace = cbRandomFace.isChecked           // 是否随机生成颜文字
            val selectedMood = spMood.selectedItem.toString() // 获取用户选择的心情
            val finalMood = if (selectedMood == "（自动判断）") inferMood(location) else selectedMood

            // 根据选择的心情获取对应的表情和推荐语
            val face = if (randomFace) getFromDB(finalMood, "face").randomOrNull() ?: "（・・？）" else customFace
            val quote = getFromDB(finalMood, "quote").randomOrNull() ?: "愿你拥有平静的一天。"

            // 获取当前日期和时间
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // 判断是否为凌晨0点到12点，若是则视为前一天的日记
            val isLate = hour in 0..12
            if (isLate) calendar.add(Calendar.DAY_OF_MONTH, -1)

            // 格式化日期，u代表星期（数字，1=周一，7=周日）
            val date = SimpleDateFormat("MM.dd.u", Locale.CHINA).format(calendar.time)

            // 根据是否补记，生成对应的时间字符串（加“次”字）
            val time = if (isLate) "次${hour}.${minute.toString().padStart(2, '0')}" else "${hour}.${minute.toString().padStart(2, '0')}"

            // 组合生成日记的头部、尾部和主体内容
            val header = "2025.$date/$time/${location}启/$face"
            val footer = "$time/${location}完"
            val result = "$header\n推荐语：$quote\n心情：$finalMood\n$footer"

            // 将结果显示到文本框
            tvResult.text = result
        }

        // 点击复制按钮，将生成的日记复制到剪贴板
        btnCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("diary", tvResult.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show()

            // 添加简单的动画效果，增强用户交互体验
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

        // 点击设置按钮跳转到设置页面
        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    // 首次启动时，从assets目录复制数据库到应用数据目录
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

    // 从数据库根据心情和类型（face或quote）获取内容列表
    private fun getFromDB(mood: String, type: String): List<String> {
        val dbPath = getDatabasePath("mood_assets.db").path
        val db: SQLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor = db.rawQuery("SELECT content FROM MoodAssets WHERE mood=? AND type=?", arrayOf(mood, type))
        val result = mutableListOf<String>()
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return result
    }

    // 根据地点和当前时间自动推断用户的心情
    private fun inferMood(location: String): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 根据具体情况智能判断心情
        return when {
            hour in 0..3 -> "堕落"
            "公司" in location || "学校" in location -> "疲惫"
            hour in 6..8 -> "沉思"
            hour in 9..17 -> "忙碌"
            hour in 18..20 -> "放松"
            hour in 21..23 -> "开心"
            dayOfWeek == Calendar.MONDAY && hour in 8..10 -> "焦虑"
            dayOfWeek == Calendar.FRIDAY && hour >= 18 -> "开心"
            dayOfWeek == Calendar.SUNDAY && hour >= 18 -> "悲伤"
            else -> "平静"
        }
    }


}
