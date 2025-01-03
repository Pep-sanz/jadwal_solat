package com.arnatech.jadwalshalat

import ImageSliderAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.arnatech.jadwalshalat.utils.CountdownHelper
import com.arnatech.jadwalshalat.utils.PrayerTimesHelper
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import getHadis
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var currentPosition = 0
    private lateinit var timeTextView: TextView
    private lateinit var countdownTextView: TextView
    private lateinit var countdownContainer: View
    private lateinit var prayerTimesHelper: PrayerTimesHelper

    private lateinit var sharedPreferences: SharedPreferences
    private val clockHandler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countdownTextView = findViewById(R.id.countdownText)
        countdownContainer = findViewById(R.id.countdown_container)
        prayerTimesHelper = PrayerTimesHelper()
        val prayerTimes = prayerTimesHelper.getPrayerTimes()

        countdownTextView.setVisibility(View.VISIBLE)

        startNextPrayerCountdown()

        val settingsButton = findViewById<ImageView>(R.id.settings_button)
        settingsButton.requestFocus()
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        timeTextView = findViewById(R.id.timeTextView)

        val fajrTimeUi = findViewById<TextView>(R.id.fajr_time)
        val dhuhrTimeUi = findViewById<TextView>(R.id.dzuhur_time)
        val asrTimeUi = findViewById<TextView>(R.id.asr_time)
        val maghribTimeUi = findViewById<TextView>(R.id.maghrib_time)
        val ishaTimeUi = findViewById<TextView>(R.id.isha_time)

        val fajrTime = prayerTimesHelper.formatPrayerTime(prayerTimes.fajr)
        val dhuhrTime = prayerTimesHelper.formatPrayerTime(prayerTimes.dhuhr)
        val asrTime = prayerTimesHelper.formatPrayerTime(prayerTimes.asr)
        val maghribTime = prayerTimesHelper.formatPrayerTime(prayerTimes.maghrib)
        val ishaTime = prayerTimesHelper.formatPrayerTime(prayerTimes.isha)

        fajrTimeUi.text = fajrTime
        dhuhrTimeUi.text = dhuhrTime
        asrTimeUi.text = asrTime
        maghribTimeUi.text = maghribTime
        ishaTimeUi.text = ishaTime

        val runningText = findViewById<TextView>(R.id.running_text)
        val hadisList = getHadis(this)
        val runningTextResult = hadisList.joinToString("   ||   ")
        runningText.text = runningTextResult
        runningText.isSelected = true

        viewPager = findViewById(R.id.viewPager)
        val imageList = listOf(
            R.drawable.bg_mosque,
            R.drawable.kabah,
            R.drawable.prayer
        )
        val adapter = ImageSliderAdapter(imageList)
        viewPager.adapter = adapter
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if (currentPosition == imageList.size) {
                    currentPosition = 0
                }
                viewPager.setCurrentItem(currentPosition++, true)
                handler.postDelayed(this, 8000)
            }
        }
        handler.postDelayed(runnable, 8000)

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val masehiDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id"))
        val currentDate = masehiDateFormat.format(Date())

        val hijriMonths = arrayOf(
            "Muharram", "Safar", "Rabiul Awal", "Rabiul Akhir",
            "Jumadil Awal", "Jumadil Akhir", "Rajab", "Syaban",
            "Ramadhan", "Syawal", "Zulqaidah", "Zulhijjah"
        )
        val hijriCalendar = UmmalquraCalendar()
        val hijriYear = hijriCalendar.get(UmmalquraCalendar.YEAR)
        val hijriMonthIndex = hijriCalendar.get(UmmalquraCalendar.MONTH)
        val hijriDay = hijriCalendar.get(UmmalquraCalendar.DAY_OF_MONTH)
        val hijriMonth = hijriMonths[hijriMonthIndex]
        val hijriDate = "$hijriDay $hijriMonth $hijriYear H"

        val combinedDate = "$currentDate\n$hijriDate"
        dateTextView.text = combinedDate

        startDigitalClock()
    }

    private fun startNextPrayerCountdown() {
        val prayerTimes = prayerTimesHelper.getPrayerTimes()
        val now = Clock.System.now()

        val nextPrayerTime = when {
            now < prayerTimes.fajr -> prayerTimes.fajr
            now < prayerTimes.dhuhr -> prayerTimes.dhuhr
            now < prayerTimes.asr -> prayerTimes.asr
            now < prayerTimes.maghrib -> prayerTimes.maghrib
            now < prayerTimes.isha -> prayerTimes.isha
            else -> prayerTimes.fajr.plus(DateTimePeriod(days = 1), TimeZone.currentSystemDefault())
        }

        val remainingMillis = nextPrayerTime.toEpochMilliseconds() - now.toEpochMilliseconds()
        startCountdown(remainingMillis)
    }

    private var countdownTimer: CountDownTimer? = null

    private fun startCountdown(durationMillis: Long) {
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                countdownTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
//                updateCountdown(isCountdownActive = true, countdownValue = countdownValue)
            }

            override fun onFinish() {
//                updateCountdown(isCountdownActive = false, countdownValue = "")
                countdownTextView.text = "Waktu salat telah tiba!"
                startNextPrayerCountdown()
            }
        }.start()
    }

//    private fun updateCountdown(isCountdownActive: Boolean, countdownValue: String) {
//        if (isCountdownActive) {
//            countdownContainer.visibility = View.VISIBLE
//            countdownTextView.text = countdownValue
//        } else {
//            countdownContainer.visibility = View.GONE
//        }
//    }

    private fun startDigitalClock() {
        clockRunnable = object : Runnable {
            override fun run() {
                val currentTime = Calendar.getInstance().time
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                timeTextView.text = timeFormat.format(currentTime)
                clockHandler.postDelayed(this, 1000)
            }
        }
        clockHandler.post(clockRunnable)
    }

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "theme") {
                ThemeManager.applyTheme(this)
                recreate()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
        handler.removeCallbacks(runnable)
        clockHandler.removeCallbacks(clockRunnable)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
