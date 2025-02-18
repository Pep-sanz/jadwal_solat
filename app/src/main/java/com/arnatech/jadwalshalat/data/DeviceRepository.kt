package com.arnatech.jadwalshalat.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.arnatech.jadwalshalat.data.local.db.AppDatabase
import com.arnatech.jadwalshalat.data.local.table.DeviceTable
import com.arnatech.jadwalshalat.data.remote.api.ApiService
import com.arnatech.jadwalshalat.data.remote.response.DeviceResponse
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRepository private constructor(
    apiService: ApiService,
    dummyApiService: ApiService,
    appDatabase: AppDatabase
) {
    private val deviceDao = appDatabase.deviceDao()
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

    fun getDeviceData(deviceId: String): LiveData<Result<DeviceResponse>> = liveData {
        emit(Result.Loading)
        try {
            //            val articles = response.articles
//            val newsList = articles.map { article ->
//                val isBookmarked = newsDao.isNewsBookmarked(article.title)
//                NewsEntity(
//                    article.title,
//                    article.publishedAt,
//                    article.urlToImage,
//                    article.url,
//                    isBookmarked
//                )
//            }
//            newsDao.deleteAll()
//            newsDao.insertNews(newsList)

            val response = dummyApi.getContentByDeviceId(deviceId)
            Log.i("HASIL:", response.mosque?.name ?: "NOL")
            Log.i("HASIL JADWAL:", response.prayerSchedule?.fajr ?: "NOL")
            val dummyResponse: LiveData<Result<DeviceResponse>> = MutableLiveData(Result.Success(response))
            emitSource(dummyResponse)
        } catch (e: Exception) {
            Log.d("NewsRepository", "getHeadlineNews: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
//        val localData: LiveData<Result<List<NewsEntity>>> = newsDao.getNews().map { Result.Success(it) }
//        emitSource(localData)
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
