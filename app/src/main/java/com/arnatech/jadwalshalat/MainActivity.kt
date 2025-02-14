package com.arnatech.jadwalshalat

import ImageSliderAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.arnatech.jadwalshalat.deviceRepository.DeviceRepository
import com.arnatech.jadwalshalat.models.NextPrayerTime
import com.arnatech.jadwalshalat.utils.PrayerTimesHelper
import com.arnatech.jadwalshalat.utils.QRCodeGenerator
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import getHadis
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.text.SimpleDateFormat
import java.util.TimeZone as TimeZoneJv
import java.util.*

class MainActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var currentPosition = 0

    private lateinit var repository: DeviceRepository
    private lateinit var qrImageView: ImageView
    private lateinit var deviceIdTextView: TextView

    private lateinit var timeTextView: TextView

    private lateinit var fajrCountdownContainer: View
    private lateinit var dhuhrCountdownContainer: View
    private lateinit var asrCountdownContainer: View
    private lateinit var maghribCountdownContainer: View
    private lateinit var ishaCountdownContainer: View

    private lateinit var fajrCountdownTextView: TextView
    private lateinit var dhuhrCountdownTextView: TextView
    private lateinit var asrCountdownTextView: TextView
    private lateinit var maghribCountdownTextView: TextView
    private lateinit var ishaCountdownTextView: TextView

    private lateinit var prayerTimesHelper: PrayerTimesHelper

    private lateinit var fajrLayout: LinearLayout
    private lateinit var dhuhrLayout: LinearLayout
    private lateinit var asrLayout: LinearLayout
    private lateinit var maghribLayout: LinearLayout
    private lateinit var ishaLayout: LinearLayout

    private lateinit var itemFajrTimeLayout: LinearLayout
    private lateinit var itemDhuhrTimeLayout: LinearLayout
    private lateinit var itemAsrTimeLayout: LinearLayout
    private lateinit var itemMaghribTimeLayout: LinearLayout
    private lateinit var itemIshaTimeLayout: LinearLayout

    //text prayer time name
    private lateinit var fajrTimeTextView: TextView
    private lateinit var dhuhrTimeTextView: TextView
    private lateinit var asrTimeTextView: TextView
    private lateinit var maghribTimeTextView: TextView
    private lateinit var ishaTimeTextView: TextView

    //text prayer time
    private lateinit var fajrTimeView: TextView
    private lateinit var dhuhrTimeView: TextView
    private lateinit var asrTimeView: TextView
    private lateinit var maghribTimeView: TextView
    private lateinit var ishaTimeView: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val clockHandler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        deviceIdTextView = findViewById(R.id.deviceIdTextView)
        qrImageView = findViewById(R.id.qrImageView)

        repository = DeviceRepository(this)

        lifecycleScope.launch {
            val deviceId = repository.getOrCreateDeviceId()
            deviceIdTextView.text = "Device ID: $deviceId"

            val qrCodeBitmap: Bitmap = QRCodeGenerator.generateQRCode(deviceId, 512, 512)
            qrImageView.setImageBitmap(qrCodeBitmap)
        }

        fajrLayout = findViewById(R.id.fajr_time_layout)
        dhuhrLayout = findViewById(R.id.dzuhur_time_layout)
        asrLayout = findViewById(R.id.asr_time_layout)
        maghribLayout = findViewById(R.id.maghrib_time_layout)
        ishaLayout = findViewById(R.id.isha_time_layout)

        itemFajrTimeLayout = findViewById(R.id.item_fajr_time_layout)
        itemDhuhrTimeLayout = findViewById(R.id.item_dzuhur_time_layout)
        itemAsrTimeLayout = findViewById(R.id.item_asr_time_layout)
        itemMaghribTimeLayout = findViewById(R.id.item_maghrib_time_layout)
        itemIshaTimeLayout = findViewById(R.id.item_isha_time_layout)

        //text prayer time name
        fajrTimeTextView= findViewById(R.id.fajr_time_text)
        dhuhrTimeTextView = findViewById(R.id.dzuhur_time_text)
        asrTimeTextView = findViewById(R.id.asr_time_text)
        maghribTimeTextView = findViewById(R.id.maghrib_time_text)
        ishaTimeTextView = findViewById(R.id.isha_time_text)

        //text prayer time
        fajrTimeView = findViewById(R.id.fajr_time)
        dhuhrTimeView= findViewById(R.id.dzuhur_time)
        asrTimeView = findViewById(R.id.asr_time)
        maghribTimeView = findViewById(R.id.maghrib_time)
        ishaTimeView = findViewById(R.id.isha_time)


        fajrCountdownContainer = findViewById(R.id.fajr_countdown_container)
        dhuhrCountdownContainer = findViewById(R.id.dzuhur_countdown_container)
        asrCountdownContainer = findViewById(R.id.asr_countdown_container)
        maghribCountdownContainer = findViewById(R.id.maghrib_countdown_container)
        ishaCountdownContainer = findViewById(R.id.isha_countdown_container)

        fajrCountdownTextView = findViewById(R.id.fajr_countdown_text)
        dhuhrCountdownTextView = findViewById(R.id.dzuhur_countdown_text)
        asrCountdownTextView = findViewById(R.id.asr_countdown_text)
        maghribCountdownTextView = findViewById(R.id.maghrib_countdown_text)
        ishaCountdownTextView = findViewById(R.id.isha_countdown_text)

        prayerTimesHelper = PrayerTimesHelper()

        val prayerTimes = prayerTimesHelper.getPrayerTimes()

//        fajrCountdownTextView.setVisibility(View.VISIBLE)

        startNextPrayerCountdown()

        val settingsButton = findViewById<ImageButton>(R.id.settings_button)
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

        fajrTimeView.text = fajrTime
        dhuhrTimeView.text = dhuhrTime
        asrTimeView.text = asrTime
        maghribTimeView.text = maghribTime
        ishaTimeView.text = ishaTime

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
                highlightPrayerTime(itemFajrTimeLayout, fajrTimeTextView, fajrTimeView)
                fajrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(prayerTimes.fajr, fajrCountdownTextView)
//                prayerTimes.fajr
            }
            now < prayerTimes.dhuhr -> {
                highlightPrayerTime(itemDhuhrTimeLayout, dhuhrTimeTextView, dhuhrTimeView)
                fajrCountdownContainer.visibility = View.INVISIBLE
                dhuhrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(prayerTimes.dhuhr, dhuhrCountdownTextView)
//                prayerTimes.dhuhr
            }
            now < prayerTimes.asr -> {
                highlightPrayerTime(itemAsrTimeLayout, asrTimeTextView, asrTimeView)
                dhuhrCountdownContainer.visibility = View.INVISIBLE
                asrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(prayerTimes.asr, asrCountdownTextView)
//                prayerTimes.asr
            }
            now < prayerTimes.maghrib -> {
                highlightPrayerTime(itemMaghribTimeLayout, maghribTimeTextView, maghribTimeView)
                asrCountdownContainer.visibility = View.INVISIBLE
                maghribCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(prayerTimes.maghrib, maghribCountdownTextView)
//                prayerTimes.maghrib
            }
            now < prayerTimes.isha -> {
                highlightPrayerTime(itemIshaTimeLayout, ishaTimeTextView, ishaTimeView)
                maghribCountdownContainer.visibility = View.INVISIBLE
                ishaCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(prayerTimes.isha, ishaCountdownTextView)
//                prayerTimes.isha
            }
            else -> {
                highlightPrayerTime(itemFajrTimeLayout, fajrTimeTextView, fajrTimeView)
                ishaCountdownContainer.visibility = View.INVISIBLE
                fajrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(prayerTimes.fajr.plus(DateTimePeriod(days = 1), TimeZone.currentSystemDefault()), fajrCountdownTextView)
//                prayerTimes.fajr.plus(DateTimePeriod(days = 1), TimeZone.currentSystemDefault())
            }
        }

        val remainingMillis = nextPrayerTime.prayerTime.toEpochMilliseconds() - now.toEpochMilliseconds()
        startCountdown(remainingMillis, nextPrayerTime.prayerCountdownText)
    }

    private fun highlightPrayerTime(
        activeLayout: LinearLayout,
        textPrayerTimeName: TextView,
        textPrayerTime: TextView
    ) {
        // Ubah warna layout aktif
        activeLayout.setBackgroundResource(R.drawable.card_item_selected_background)
        textPrayerTimeName.setTextColor(Color.WHITE)
        textPrayerTime.setTextColor(Color.WHITE)
    }

    private var countdownTimer: CountDownTimer? = null

    private fun startCountdown(durationMillis: Long, prayerCountdownText: TextView) {
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                prayerCountdownText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
//                updateCountdown(isCountdownActive = true, countdownValue = countdownValue)
            }

            override fun onFinish() {
//                updateCountdown(isCountdownActive = false, countdownValue = "")
                prayerCountdownText.text = "Waktu salat telah tiba!"
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
                val calendar = Calendar.getInstance(TimeZoneJv.getTimeZone("GMT+7"))
                val currentTime = calendar.time
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                // Set zona waktu Indonesia (WIB)
                timeFormat.timeZone = TimeZoneJv.getTimeZone("GMT+7")
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
