package com.arnatech.jadwalshalat

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arnatech.jadwalshalat.databinding.ActivityQrSettingBinding
import com.arnatech.jadwalshalat.factory.ViewModelFactory
import com.arnatech.jadwalshalat.sharedviewmodel.PrayerScheduleApplication
import com.arnatech.jadwalshalat.utils.QRCodeGenerator
import com.arnatech.jadwalshalat.viewmodel.DeviceViewModel

class QrSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrSettingBinding

    private lateinit var qrImageView: ImageView
    private lateinit var deviceIdTextView: TextView
    private lateinit var refreshButton: Button
    private lateinit var localDeviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        deviceIdTextView = binding.deviceIdTextView
        qrImageView = binding.qrImageView
        refreshButton = binding.refreshButton

        val factory = (application as PrayerScheduleApplication).getViewModelFactory()
        val viewModel: DeviceViewModel = ViewModelProvider(application as PrayerScheduleApplication, factory)[DeviceViewModel::class.java]

        viewModel.getOrCreateDeviceId()
        viewModel.deviceId.observe(this) { deviceId ->
            Log.i("Device ID", deviceId)
            localDeviceId = deviceId
            deviceIdTextView.text = "Device ID: $deviceId"
            val qrCodeBitmap: Bitmap = QRCodeGenerator.generateQRCode(deviceId, 512, 512)
            qrImageView.setImageBitmap(qrCodeBitmap)
            refreshButton.visibility = View.VISIBLE
        }

        refreshButton.requestFocus()
        refreshButton.setOnClickListener {
            Log.i("AMBIL", localDeviceId)
            viewModel.getDeviceData(this, localDeviceId)
            Toast.makeText(
                this,
                "Refreshing data...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}