package com.arnatech.jadwalshalat

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arnatech.jadwalshalat.databinding.ActivityQrSettingBinding
import com.arnatech.jadwalshalat.factory.ViewModelFactory
import com.arnatech.jadwalshalat.utils.QRCodeGenerator
import com.arnatech.jadwalshalat.viewmodel.DeviceViewModel

class QrSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrSettingBinding

    private lateinit var qrImageView: ImageView
    private lateinit var deviceIdTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        deviceIdTextView = binding.deviceIdTextView
        qrImageView = binding.qrImageView

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: DeviceViewModel = ViewModelProvider(this, factory)[DeviceViewModel::class.java]
        viewModel.getOrCreateDeviceId()
        viewModel.deviceId.observe(this) { deviceId ->
            Log.i("Device ID", deviceId)
            deviceIdTextView.text = "Device ID: $deviceId"
            val qrCodeBitmap: Bitmap = QRCodeGenerator.generateQRCode(deviceId, 512, 512)
            qrImageView.setImageBitmap(qrCodeBitmap)
        }
    }
}