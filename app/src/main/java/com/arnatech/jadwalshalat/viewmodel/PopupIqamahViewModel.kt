package com.arnatech.jadwalshalat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PopupIqamahViewModel: ViewModel() {
    private val _showPopupIqamah = MutableLiveData<Boolean>()
    val showPopupIqamah: LiveData<Boolean> get() = _showPopupIqamah

    // Fungsi untuk memicu popup tampil
    fun triggerPopupIqamah() {
        _showPopupIqamah.value = true
    }

    // Fungsi untuk mereset state setelah popup muncul
    fun resetPopupIqamahState() {
        _showPopupIqamah.value = false
    }
}