package com.arnatech.jadwalshalat.utils

import android.os.Handler
import android.os.Looper
import java.util.*

class CountdownHelper(private val callback: CountdownCallback) {
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    interface CountdownCallback {
        fun onCountdownUpdate(remainingTime: String)
        fun onCountdownFinish()
    }

    fun startCountdown(targetTime: Date) {
        runnable = object : Runnable {
            override fun run() {
                val currentTime = Calendar.getInstance().time
                val remainingMillis = targetTime.time - currentTime.time

                if (remainingMillis > 0) {
                    val hours = remainingMillis / (1000 * 60 * 60)
                    val minutes = (remainingMillis / (1000 * 60)) % 60
                    val seconds = (remainingMillis / 1000) % 60

                    val remainingTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    callback.onCountdownUpdate(remainingTime)
                    handler.postDelayed(this, 1000)
                } else {
                    callback.onCountdownFinish()
                }
            }
        }
        handler.post(runnable!!)
    }

    fun stopCountdown() {
        runnable?.let { handler.removeCallbacks(it) }
    }
}
