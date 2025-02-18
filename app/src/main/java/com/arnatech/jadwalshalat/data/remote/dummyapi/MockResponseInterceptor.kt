package com.arnatech.jadwalshalat.data.remote.dummyapi

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.charset.StandardCharsets

class MockResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.i("TEST URL:", request.url.encodedPath)
        val responseString = when (request.url.encodedPath) {
            "/api/device/tv-content" -> """
                {
                      "mosque": {
                        "name": "Mesjid An-najah",
                        "address": "Jl. Ahmad Yani No.25, Pakuwon, Kec. Garut Kota, Kabupaten Garut, Jawa Barat 44117",
                        "latitude": -7.2156651,
                        "longitude": 107.9015837
                      },
                      "prayer_schedule": [
                        {
                          "date": "2025-01-01T06:09:58.893925Z",
                          "fajr":"04:41",
                          "sunrise":"05:45",
                          "dhuhr":"12:04",
                          "asr":"15:16",
                          "maghrib":"18:13",
                          "isha":"19:20"
                        }
                      ],
                      "sliders": [
                        {
                          "id": 1,
                          "mosque": 10,
                          "background_image": {
                            "id": 1,
                            "name": "slide-1",
                            "url": "https://masjid-display.ap-south-1.linodeobjects.com/masjid-display/dev/slide-1.jpg.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=6VYSJRFW9D3ZOZOEGR3N%2F20250217%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250217T082804Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=c01d1fe690e2474ee17d10eca51faddb63bdaeba3d28a1d49a5bc18bbbfa66f9",
                            "file_size": 222927
                          },
                          "text": "",
                          "created_at": "2025-01-01T09:15:00.893925Z"
                        },
                        {
                          "id": 2,
                          "mosque": 10,
                          "background_image": {
                            "id": 2,
                            "name": "slide-2",
                            "url": "https://masjid-display.ap-south-1.linodeobjects.com/masjid-display/dev/slide-2.jpeg.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=6VYSJRFW9D3ZOZOEGR3N%2F20250217%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250217T082805Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=4fef90e21295d4fe8d2c2d75ed2db8b9a2eae091d6a84132a8407f03610b70ea",
                            "file_size": 222927
                          },
                          "text": "",
                          "created_at": "2025-01-01T09:16:00.893925Z"
                        },
                        {
                          "id": 3,
                          "mosque": 10,
                          "background_image": {
                            "id": 3,
                            "name": "slide-3",
                            "url": "https://masjid-display.ap-south-1.linodeobjects.com/masjid-display/dev/slide-3.jpg.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=6VYSJRFW9D3ZOZOEGR3N%2F20250217%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250217T082805Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=280c03bfe951b7cbd63387881a54e6501f21456be1785d9d7840025fd8e07d05",
                            "file_size": 222927
                          },
                          "text": "",
                          "created_at": "2025-01-01T09:17:00.893925Z"
                        }
                      ],
                      "text_marquee": [
                        {
                          "id": 1,
                          "mosque": 10,
                          "text": "Sebaik-baik manusia adalah yang paling bermanfaat bagi orang lain.",
                          "created_at": "2025-01-01T09:18:00.893925Z"
                        },
                        {
                          "id": 2,
                          "mosque": 10,
                          "text": "Barangsiapa beriman kepada Allah dan hari akhir, maka berkatalah yang baik atau diam.",
                          "created_at": "2025-01-01T09:19:00.893925Z"
                        },
                        {
                          "id": 3,
                          "mosque": 10,
                          "text": "Sesungguhnya Allah menyukai jika seseorang melakukan pekerjaan dengan itqan (tepat dan sempurna).",
                          "created_at": "2025-01-01T09:20:00.893925Z"
                        }
                      ],
                      "configurations": {
                        "id": 100,
                        "mosque": 10,
                        "max_sliders": 2,
                        "max_text_marquee": 3,
                        "prayer_duration_days": 7,
                        "allow_calendar_access": true,
                        "created_at": "2025-01-01T09:10:00.893925Z"
                      }
                }
            """.trimIndent()
            else -> """{"error": "Not found"}"""
        }

        return Response.Builder()
            .code(200)
            .message(responseString)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(responseString.toByteArray(StandardCharsets.UTF_8)
                .toResponseBody("application/json".toMediaType()))
            .addHeader("content-type", "application/json")
            .build()
    }
}
