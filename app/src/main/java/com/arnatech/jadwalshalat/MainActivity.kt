package com.arnatech.jadwalshalat

import ImageSliderAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.arnatech.jadwalshalat.factory.ViewModelFactory
import com.arnatech.jadwalshalat.models.NextPrayerTime
import com.arnatech.jadwalshalat.data.Result
import com.arnatech.jadwalshalat.models.DeviceData
import com.arnatech.jadwalshalat.utils.toInstant
import com.arnatech.jadwalshalat.viewmodel.DeviceViewModel
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone as TimeZoneJv

class MainActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var currentPosition = 0

    private lateinit var timeTextView: TextView

    private lateinit var mosqueName: TextView
    private lateinit var mosqueAddress: TextView

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

    private lateinit var fajrTimeData: Instant
    private lateinit var dhuhrTimeData: Instant
    private lateinit var asrTimeData: Instant
    private lateinit var maghribTimeData: Instant
    private lateinit var ishaTimeData: Instant

    //text prayer time
    private lateinit var fajrTimeView: TextView
    private lateinit var dhuhrTimeView: TextView
    private lateinit var asrTimeView: TextView
    private lateinit var maghribTimeView: TextView
    private lateinit var ishaTimeView: TextView

    private lateinit var textMarquee: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val clockHandler = Handler(Looper.getMainLooper())
    private lateinit var clockRunnable: Runnable

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mosqueName = findViewById(R.id.mosque_name)
        mosqueAddress = findViewById(R.id.mosque_address)

        timeTextView = findViewById(R.id.timeTextView)

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

        viewPager = findViewById(R.id.viewPager)
        textMarquee = findViewById(R.id.running_text)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: DeviceViewModel = ViewModelProvider(this, factory)[DeviceViewModel::class.java]

        viewModel.getOrCreateDeviceId()
        viewModel.deviceId.observe(this@MainActivity) { deviceId ->
            viewModel.getDeviceData(deviceId).observe(this@MainActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            val deviceData: DeviceData = result.data
                            setMosque(deviceData)
                            setBackgroundImage(deviceData)
                            setTextMarquee(deviceData)
                            setPrayerSchedule(deviceData)

                            startNextPrayerCountdown()

                        }
                        is Result.Error -> {
//                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                this@MainActivity,
                                "Terjadi kesalahan" + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        val settingsButton = findViewById<ImageButton>(R.id.settings_button)
        settingsButton.requestFocus()
        settingsButton.setOnClickListener {
            val intent = Intent(this, QrSettingActivity::class.java)
            startActivity(intent)
        }

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

    private fun setMosque(deviceData: DeviceData) {
        mosqueName.text = deviceData.mosque?.name ?: "Nama Mesjid"
        mosqueAddress.text = deviceData.mosque?.address ?: "Alamat Mesjid"
    }

    private fun setBackgroundImage(deviceData: DeviceData) {
        if (deviceData.sliders != null) {
            val imgList = deviceData.sliders.map { data -> data?.backgroundImage?.url ?: "" }
            viewPager.adapter = ImageSliderAdapter(imgList)
            handler = Handler(Looper.getMainLooper())
            runnable = object : Runnable {
                override fun run() {
                    if (currentPosition == deviceData.sliders.size) {
                        currentPosition = 0
                    }
                    viewPager.setCurrentItem(currentPosition++, true)
                    handler.postDelayed(this, 8000)
                }
            }
            handler.postDelayed(runnable, 8000)
        }
    }

    private fun setPrayerSchedule(deviceData: DeviceData) {
//        Log.i("MOSQUE:", deviceData.mosque?.name ?: "ERROR")
        fajrTimeData = toInstant(deviceData.prayerSchedule?.get(0)?.fajr ?: "00:00")
        dhuhrTimeData = toInstant(deviceData.prayerSchedule?.get(0)?.dhuhr ?: "00:00")
        asrTimeData = toInstant(deviceData.prayerSchedule?.get(0)?.asr ?: "00:00")
        maghribTimeData = toInstant(deviceData.prayerSchedule?.get(0)?.maghrib ?: "00:00")
        ishaTimeData = toInstant(deviceData.prayerSchedule?.get(0)?.isha ?: "00:00")

        fajrTimeView.text = deviceData.prayerSchedule?.get(0)?.fajr ?: "00:00"
        dhuhrTimeView.text = deviceData.prayerSchedule?.get(0)?.dhuhr ?: "00:00"
        asrTimeView.text = deviceData.prayerSchedule?.get(0)?.asr ?: "00:00"
        maghribTimeView.text = deviceData.prayerSchedule?.get(0)?.maghrib ?: "00:00"
        ishaTimeView.text = deviceData.prayerSchedule?.get(0)?.isha ?: "00:00"
    }

    private fun setTextMarquee(deviceData: DeviceData) {
        val runningTextResult = (deviceData.textMarquee?.map { data -> data?.text } ?: listOf("", "")).joinToString("   <>   ")
        textMarquee.text = runningTextResult
        textMarquee.isSelected = true
    }

    private fun highlightPrayerTime(
        activeLayout: LinearLayout,
        textPrayerTimeName: TextView,
        textPrayerTime: TextView
    ) {
        activeLayout.setBackgroundResource(R.drawable.card_item_selected_background)
        textPrayerTimeName.setTextColor(Color.WHITE)
        textPrayerTime.setTextColor(Color.WHITE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun unHighlightPrayerTime(
        activeLayout: LinearLayout,
        textPrayerTimeName: TextView,
        textPrayerTime: TextView
    ) {
        activeLayout.setBackgroundResource(R.color.white)
        textPrayerTimeName.setTextColor(getColor(R.color.primary))
        textPrayerTime.setTextColor(getColor(R.color.primary))
    }

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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startNextPrayerCountdown() {
        val now = Clock.System.now()

        val nextPrayerTime = when {
            now < fajrTimeData -> {
                unHighlightPrayerTime(itemIshaTimeLayout, ishaTimeTextView, ishaTimeView)
                highlightPrayerTime(itemFajrTimeLayout, fajrTimeTextView, fajrTimeView)
                ishaCountdownContainer.visibility = View.INVISIBLE
                fajrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(fajrTimeData, fajrCountdownTextView)
            }
            now < dhuhrTimeData -> {
                unHighlightPrayerTime(itemFajrTimeLayout, fajrTimeTextView, fajrTimeView)
                highlightPrayerTime(itemDhuhrTimeLayout, dhuhrTimeTextView, dhuhrTimeView)
                fajrCountdownContainer.visibility = View.INVISIBLE
                dhuhrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(dhuhrTimeData, dhuhrCountdownTextView)
            }
            now < asrTimeData -> {
                unHighlightPrayerTime(itemDhuhrTimeLayout, dhuhrTimeTextView, dhuhrTimeView)
                highlightPrayerTime(itemAsrTimeLayout, asrTimeTextView, asrTimeView)
                dhuhrCountdownContainer.visibility = View.INVISIBLE
                asrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(asrTimeData, asrCountdownTextView)
            }
            now < maghribTimeData -> {
                unHighlightPrayerTime(itemAsrTimeLayout, asrTimeTextView, asrTimeView)
                highlightPrayerTime(itemMaghribTimeLayout, maghribTimeTextView, maghribTimeView)
                asrCountdownContainer.visibility = View.INVISIBLE
                maghribCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(maghribTimeData, maghribCountdownTextView)
            }
            now < ishaTimeData -> {
                unHighlightPrayerTime(itemMaghribTimeLayout, maghribTimeTextView, maghribTimeView)
                highlightPrayerTime(itemIshaTimeLayout, ishaTimeTextView, ishaTimeView)
                maghribCountdownContainer.visibility = View.INVISIBLE
                ishaCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(ishaTimeData, ishaCountdownTextView)
            }
            else -> {
                unHighlightPrayerTime(itemIshaTimeLayout, ishaTimeTextView, ishaTimeView)
                highlightPrayerTime(itemFajrTimeLayout, fajrTimeTextView, fajrTimeView)
                ishaCountdownContainer.visibility = View.INVISIBLE
                fajrCountdownContainer.visibility = View.VISIBLE
                NextPrayerTime(fajrTimeData.plus(DateTimePeriod(days = 1), TimeZone.currentSystemDefault()), fajrCountdownTextView)
            }
        }

        val remainingMillis = nextPrayerTime.prayerTime.toEpochMilliseconds() - now.toEpochMilliseconds()
        startCountdown(remainingMillis, nextPrayerTime.prayerCountdownText)
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
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onFinish() {
                prayerCountdownText.text = ""
                startNextPrayerCountdown()
            }
        }.start()
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
