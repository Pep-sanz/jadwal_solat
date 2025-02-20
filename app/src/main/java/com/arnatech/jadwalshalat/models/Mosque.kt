package com.arnatech.jadwalshalat.models

data class Mosque(
    val id: Int,
    val name: String? = null,
    val address: String? = null,
    val latitude: Any? = null,
    val longitude: Any? = null,
)