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
import android.widget.LinearLayout
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
    private lateinit var fajrLayout: LinearLayout
    private lateinit var dhuhrLayout: LinearLayout
    private lateinit var asrLayout: LinearLayout
    private lateinit var maghribLayout: LinearLayout
    private lateinit var ishaLayout: LinearLayout
    private lateinit var fajrTextView: TextView
    //text prayer time name
    private lateinit var dhuhrTextView: TextView
    private lateinit var asrTextView: TextView
    private lateinit var maghribTextView: TextView
    private lateinit var ishaTextView: TextView
    //text prayer time
    private lateinit var fajrTimeTextView: TextView
    private lateinit var dhuhrTimeTextView: TextView
    private lateinit var asrTimeTextView: TextView
    private lateinit var maghribTimeTextView: TextView
    private lateinit var ishaTimeTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val clockHandler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fajrLayout = findViewById(R.id.fajr_time_layout)
        dhuhrLayout = findViewById(R.id.dzuhur_time_layout)
        asrLayout = findViewById(R.id.asr_time_layout)
        maghribLayout = findViewById(R.id.maghrib_time_layout)
        ishaLayout = findViewById(R.id.isha_time_layout)

        //text prayer time name
        fajrTextView = findViewById(R.id.fajr_time_text)
        dhuhrTextView= findViewById(R.id.dzuhur_time_text)
        asrTimeTextView = findViewById(R.id.asr_time_text)
        maghribTimeTextView = findViewById(R.id.maghrib_time_text)
        ishaTimeTextView = findViewById(R.id.isha_time_text)

        //text prayer time
        fajrTimeTextView= findViewById(R.id.fajr_time)
        dhuhrTimeTextView = findViewById(R.id.dzuhur_time)
        asrTimeTextView = findViewById(R.id.asr_time)
        maghribTimeTextView = findViewById(R.id.maghrib_time)
        ishaTimeTextView = findViewById(R.id.isha_time)

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


        val fajrTime = prayerTimesHelper.formatPrayerTime(prayerTimes.fajr)
        val dhuhrTime = prayerTimesHelper.formatPrayerTime(prayerTimes.dhuhr)
        val asrTime = prayerTimesHelper.formatPrayerTime(prayerTimes.asr)
        val maghribTime = prayerTimesHelper.formatPrayerTime(prayerTimes.maghrib)
        val ishaTime = prayerTimesHelper.formatPrayerTime(prayerTimes.isha)

        fajrTimeTextView.text = fajrTime
        dhuhrTimeTextView.text = dhuhrTime
        asrTimeTextView.text = asrTime
        maghribTimeTextView.text = maghribTime
        ishaTimeTextView.text = ishaTime

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
            now < prayerTimes.fajr -> {
                highlightPrayerTime(fajrLayout)
                prayerTimes.fajr
            }
            now < prayerTimes.dhuhr -> {
                highlightPrayerTime(dhuhrLayout)
                prayerTimes.dhuhr
            }
            now < prayerTimes.asr -> {
                highlightPrayerTime(asrLayout)
                prayerTimes.asr
            }
            now < prayerTimes.maghrib -> {
                highlightPrayerTime(maghribLayout)
                prayerTimes.maghrib
            }
            now < prayerTimes.isha -> {
                highlightPrayerTime(ishaLayout)
                prayerTimes.isha
            }
            else -> {
                highlightPrayerTime(fajrLayout)
                prayerTimes.fajr.plus(DateTimePeriod(days = 1), TimeZone.currentSystemDefault())
            }
        }

        val remainingMillis = nextPrayerTime.toEpochMilliseconds() - now.toEpochMilliseconds()
        startCountdown(remainingMillis)
    }

    private fun highlightPrayerTime(activeLayout: LinearLayout,) {
        // Ubah warna layout aktif
        activeLayout.setBackgroundResource(R.drawable.fajr_bacground)
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
