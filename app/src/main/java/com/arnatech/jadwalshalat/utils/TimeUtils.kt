import android.content.Context
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RealTimeUtils(private val context: Context) {
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Mendapatkan zona waktu berdasarkan latitude dan longitude.
     */
    fun getTimeZoneFromCoordinates(latitude: Double, longitude: Double): TimeZone {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val timeZoneId = addresses[0].subAdminArea ?: TimeZone.getDefault().id
                TimeZone.getTimeZone(timeZoneId)
            } else {
                // Jika gagal, gunakan zona waktu terdekat dengan ID
                val timeZoneId = TimeZone.getAvailableIDs().firstOrNull { id ->
                    id.contains(getNearestTimeZone(latitude, longitude))
                } ?: "UTC"
                TimeZone.getTimeZone(timeZoneId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Gunakan default UTC jika ada masalah
            TimeZone.getTimeZone("UTC")
        }
    }

    /**
     * Menjalankan pembaruan waktu real-time berdasarkan zona waktu.
     */
    fun startRealTimeClock(
        latitude: Double,
        longitude: Double,
        onTimeUpdate: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Dapatkan zona waktu dari koordinat
                val timeZone = getTimeZoneFromCoordinates(latitude, longitude)

                // Update waktu setiap detik di UI Thread
                handler.post(object : Runnable {
                    override fun run() {
                        val currentTime = Calendar.getInstance(timeZone)
                        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        dateFormat.timeZone = timeZone

                        val formattedTime = dateFormat.format(currentTime.time)
                        onTimeUpdate(formattedTime)

                        // Jalankan lagi setiap 1 detik
                        handler.postDelayed(this, 1000)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Menghentikan pembaruan waktu.
     */
    fun stopRealTimeClock() {
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * Fungsi bantuan untuk mendapatkan zona waktu terdekat dari latitude dan longitude.
     */
    private fun getNearestTimeZone(latitude: Double, longitude: Double): String {
        val timeZones = TimeZone.getAvailableIDs()
        var nearestZone = "UTC"
        var shortestDistance = Double.MAX_VALUE

        for (zone in timeZones) {
            val tz = TimeZone.getTimeZone(zone)
            val offset = tz.rawOffset / 1000.0
            val distance = Math.abs(offset - longitude)

            if (distance < shortestDistance) {
                shortestDistance = distance
                nearestZone = zone
            }
        }
        return nearestZone
    }
}
