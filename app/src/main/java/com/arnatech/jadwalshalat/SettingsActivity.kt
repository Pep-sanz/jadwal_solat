package com.arnatech.jadwalshalat

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Terapkan tema sebelum memuat layout
        ThemeManager.applyTheme(this)

        setContentView(R.layout.activity_settings)

        // Tombol tema dan logika perubahan
        findViewById<Button>(R.id.cyan_theme).setOnClickListener {
            updateTheme("cyan")
        }
        findViewById<Button>(R.id.red_theme).setOnClickListener {
            updateTheme("red")
        }
        findViewById<Button>(R.id.green_theme).setOnClickListener {
            updateTheme("green")
        }
        findViewById<Button>(R.id.yellow_theme).setOnClickListener {
            updateTheme("yellow")
        }
        findViewById<Button>(R.id.blue_theme).setOnClickListener {
            updateTheme("blue")
        }
    }

    private fun updateTheme(theme: String) {
        // Gunakan ThemeManager untuk menyimpan dan menerapkan tema
        ThemeManager.setTheme(this, theme)
        // Berikan feedback ke pengguna
        android.widget.Toast.makeText(this, "Tema $theme berhasil diterapkan!", android.widget.Toast.LENGTH_SHORT).show()

        // Refresh UI untuk menerapkan tema baru
        recreate()
    }
}
