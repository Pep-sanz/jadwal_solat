package com.arnatech.jadwalshalat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var cyanThemeButton: Button
    private lateinit var redThemeButton: Button
    private lateinit var greenThemeButton: Button
    private lateinit var yellowThemeButton: Button
    private lateinit var blueThemeButton: Button

    private lateinit var checkIconCyanTheme: ImageView
    private lateinit var checkIconRedTheme: ImageView
    private lateinit var checkIconGreenTheme: ImageView
    private lateinit var checkIconYellowTheme: ImageView
    private lateinit var checkIconBlueTheme: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Terapkan tema sebelum memuat layout
        ThemeManager.applyTheme(this)

        setContentView(R.layout.activity_settings)

        // Tombol tema
        cyanThemeButton = findViewById(R.id.cyan_theme)
        redThemeButton = findViewById(R.id.red_theme)
        greenThemeButton = findViewById(R.id.green_theme)
        yellowThemeButton = findViewById(R.id.yellow_theme)
        blueThemeButton = findViewById(R.id.blue_theme)

        // Gambar icon check
        checkIconCyanTheme = findViewById(R.id.check_icon_cyan_theme)
        checkIconRedTheme = findViewById(R.id.check_icon_red_theme)
        checkIconGreenTheme = findViewById(R.id.check_icon_green_theme)
        checkIconYellowTheme = findViewById(R.id.check_icon_yellow_theme)
        checkIconBlueTheme = findViewById(R.id.check_icon_blue_theme)

        // Focus Listener
        cyanThemeButton.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setVisibleIndicator("cyan")
            } else {
                setInvisibleIndicator("cyan")
            }
        }
        redThemeButton.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setVisibleIndicator("red")
            } else {
                setInvisibleIndicator("red")
            }
        }
        greenThemeButton.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setVisibleIndicator("green")
            } else {
                setInvisibleIndicator("green")
            }
        }
        yellowThemeButton.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setVisibleIndicator("yellow")
            } else {
                setInvisibleIndicator("yellow")
            }
        }
        blueThemeButton.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setVisibleIndicator("blue")
            } else {
                setInvisibleIndicator("blue")
            }
        }

        // Update Theme
        cyanThemeButton.setOnClickListener {
            updateTheme("cyan")
        }
        redThemeButton.setOnClickListener {
            updateTheme("red")
        }
        greenThemeButton.setOnClickListener {
            updateTheme("green")
        }
        yellowThemeButton.setOnClickListener {
            updateTheme("yellow")
        }
        blueThemeButton.setOnClickListener {
            updateTheme("blue")
        }
    }

    override fun onStart() {
        super.onStart()

        // Set icon check by current selected theme
        setDefaultThemeIndicator()
    }

    private fun getThemeName(): String? {
       return ThemeManager.getTheme(this)
    }

    private fun setDefaultThemeIndicator() {
        val theme = getThemeName()
        when (theme) {
            "cyan" -> {
                cyanThemeButton.requestFocus()
                checkIconCyanTheme.visibility = View.VISIBLE
            }
            "red" -> {
                redThemeButton.requestFocus()
                checkIconRedTheme.visibility = View.VISIBLE
            }
            "green" -> {
                greenThemeButton.requestFocus()
                checkIconGreenTheme.visibility = View.VISIBLE
            }
            "yellow" -> {
                yellowThemeButton.requestFocus()
                checkIconYellowTheme.visibility = View.VISIBLE
            }
            "blue" -> {
                blueThemeButton.requestFocus()
                checkIconBlueTheme.visibility = View.VISIBLE
            }
        }
    }

    private fun setVisibleIndicator(themeName: String) {
        when (themeName) {
            "cyan" -> checkIconCyanTheme.visibility = View.VISIBLE
            "red" -> checkIconRedTheme.visibility = View.VISIBLE
            "green" -> checkIconGreenTheme.visibility = View.VISIBLE
            "yellow" -> checkIconYellowTheme.visibility = View.VISIBLE
            "blue" -> checkIconBlueTheme.visibility = View.VISIBLE
        }
    }

    private fun setInvisibleIndicator(themeName: String) {
        when (themeName) {
            "cyan" -> checkIconCyanTheme.visibility = View.INVISIBLE
            "red" -> checkIconRedTheme.visibility = View.INVISIBLE
            "green" -> checkIconGreenTheme.visibility = View.INVISIBLE
            "yellow" -> checkIconYellowTheme.visibility = View.INVISIBLE
            "blue" -> checkIconBlueTheme.visibility = View.INVISIBLE
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
