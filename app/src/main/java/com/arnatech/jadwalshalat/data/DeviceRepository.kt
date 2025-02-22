package com.arnatech.jadwalshalat.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.arnatech.jadwalshalat.data.local.db.AppDatabase
import com.arnatech.jadwalshalat.data.local.table.BackgroundImageEntity
import com.arnatech.jadwalshalat.data.local.table.ConfigurationTable
import com.arnatech.jadwalshalat.data.local.table.DeviceTable
import com.arnatech.jadwalshalat.data.local.table.MosqueTable
import com.arnatech.jadwalshalat.data.local.table.PrayerScheduleTable
import com.arnatech.jadwalshalat.data.local.table.SlidersTable
import com.arnatech.jadwalshalat.data.local.table.TextMarqueeTable
import com.arnatech.jadwalshalat.data.remote.api.ApiService
import com.arnatech.jadwalshalat.models.BackgroundImage
import com.arnatech.jadwalshalat.models.Configuration
import com.arnatech.jadwalshalat.models.DeviceData
import com.arnatech.jadwalshalat.models.Mosque
import com.arnatech.jadwalshalat.models.PrayerSchedule
import com.arnatech.jadwalshalat.models.Slider
import com.arnatech.jadwalshalat.models.TextMarquee
import com.arnatech.jadwalshalat.utils.formatSchedule
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRepository private constructor(
    apiService: ApiService,
    dummyApiService: ApiService,
    appDatabase: AppDatabase
) {
    private val deviceDao = appDatabase.deviceDao()
    private val mosqueDao = appDatabase.mosqueDao()
    private val prayerScheduleDao = appDatabase.prayerScheduleDao()
    private val sliderDao = appDatabase.sliderDao()
    private val textMarqueeDao = appDatabase.textMarqueeDao()
    private val configurationDao = appDatabase.configurationDao()

    private val api = apiService
    private val dummyApi = dummyApiService

    suspend fun getOrCreateDeviceId(): String {
        return withContext(Dispatchers.IO) {
            var device = deviceDao.getDeviceId()

            if (device == null) {
                val newId = UUID.randomUUID().toString()
                device = DeviceTable(newId)
                deviceDao.insertDevice(device)
            }

            device.id
        }
    }

    fun getDeviceData(deviceId: String): LiveData<Result<DeviceData>> = liveData {
        emit(Result.Loading)

        // FETCH & SAVE TO DB
        try {
//            val response = dummyApi.getContentByDeviceId(deviceId)
            val response = api.getContentByDeviceId(deviceId)
            val mosqueTable = MosqueTable(
                0,
                response.mosque?.name,
                response.mosque?.address,
                response.mosque?.latitude.toString().toDoubleOrNull(),
                response.mosque?.longitude.toString().toDoubleOrNull(),
            )
            val prayerScheduleList = response.prayerSchedule?.map { schedule ->
                PrayerScheduleTable(
                    0,
                    formatSchedule(schedule?.date),
                    formatSchedule(schedule?.asr),
                    formatSchedule(schedule?.sunrise),
                    formatSchedule(schedule?.isha),
                    formatSchedule(schedule?.dhuhr),
                    formatSchedule(schedule?.fajr),
                    formatSchedule(schedule?.maghrib),
                )
            } ?: listOf()
            val sliderList = response.sliders?.map { sliderItem ->
                SlidersTable(
                    sliderItem?.id,
                    sliderItem?.mosque,
                    BackgroundImageEntity(
                        sliderItem?.backgroundImage?.id,
                        sliderItem?.backgroundImage?.name,
                        sliderItem?.backgroundImage?.url,
                        sliderItem?.backgroundImage?.fileSize
                    ),
                    sliderItem?.text,
                    sliderItem?.createdAt
                )
            } ?: listOf()
            val textMarqueeList = response.textMarquee?.map { textMarquee ->
                TextMarqueeTable(
                    textMarquee?.id,
                    textMarquee?.mosque,
                    textMarquee?.createdAt,
                    textMarquee?.text,
                )
            } ?: listOf()
            val configurationTable = ConfigurationTable(
                0,
                response.configurations?.mosque,
                response.configurations?.maxSliders,
                response.configurations?.maxTextMarquee,
                response.configurations?.prayerDurationDays,
                response.configurations?.allowCalendarAccess,
                response.configurations?.createdAt,
            )
            mosqueDao.deleteAll()
            prayerScheduleDao.deleteAll()
            sliderDao.deleteAll()
            textMarqueeDao.deleteAll()
            configurationDao.deleteAll()

            mosqueDao.insertMosque(mosqueTable)
            prayerScheduleDao.insertPrayerScheduleList(prayerScheduleList)
            sliderDao.insertSlidersList(sliderList)
            textMarqueeDao.insertTextMarqueeList(textMarqueeList)
            configurationDao.insertConfiguration(configurationTable)
        } catch (e: Exception) {
            Log.d("DeviceRepository", "getDeviceData: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }

        // GET FROM DB
        val localMosque = mosqueDao.getMosque()?.run {
            Mosque(id, name, address, latitude, longitude)
        }
        val localPrayerScheduleList = prayerScheduleDao.getAllPrayerSchedule().map {
            it.run { PrayerSchedule(date, asr, sunrise, isha, dhuhr, fajr, maghrib) }
        }
        val localSliderList = sliderDao.getAllSliders().map {
            it.run {
                Slider(
                    id,
                    mosque,
                    BackgroundImage(
                        backgroundImage?.backgroundId,
                        backgroundImage?.name,
                        backgroundImage?.url,
                        backgroundImage?.fileSize
                    ),
                    text,
                    createdAt
                )
            }
        }
        val localTextMarqueeList = textMarqueeDao.getAllTextMarquee().map {
            it.run { TextMarquee(mosque, createdAt, id, text) }
        }
        val localConfiguration = configurationDao.getConfiguration()?.run {
            Configuration(id, mosque, maxSliders, maxTextMarquee, prayerDurationDays, allowCalendarAccess, createdAt)
        }

        val deviceData = DeviceData(
            localMosque,
            localPrayerScheduleList,
            localSliderList,
            localTextMarqueeList,
            localConfiguration
        )
        val localDeviceData: LiveData<Result<DeviceData>> = MutableLiveData(Result.Success(deviceData))
        emitSource(localDeviceData)
    }

    companion object {
        @Volatile
        private var instance: DeviceRepository? = null
        fun getInstance(
            apiService: ApiService,
            dummyApiService: ApiService,
            appDatabase: AppDatabase
        ): DeviceRepository =
            instance ?: synchronized(this) {
                instance ?: DeviceRepository(
                    apiService,
                    dummyApiService,
                    appDatabase
                )
            }.also { instance = it }
    }
}
