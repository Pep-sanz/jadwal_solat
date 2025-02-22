package com.arnatech.jadwalshalat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PopupKhutbahViewModel: ViewModel() {
    private val _showPopupKhutbah = MutableLiveData<Boolean>()
    val showPopupKhutbah: LiveData<Boolean> get() = _showPopupKhutbah

    // Fungsi untuk memicu popup tampil
    fun triggerPopupKhutbah() {
        _showPopupKhutbah.value = true
    }

    // Fungsi untuk mereset state setelah popup muncul
    fun resetPopupKhutbahState() {
        _showPopupKhutbah.value = false
    }
}