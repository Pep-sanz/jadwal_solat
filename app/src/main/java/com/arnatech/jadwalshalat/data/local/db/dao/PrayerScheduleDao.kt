package com.arnatech.jadwalshalat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnatech.jadwalshalat.data.local.table.PrayerScheduleTable

@Dao
interface PrayerScheduleDao {
    @Query("SELECT * FROM prayer_schedule_table")
    suspend fun getAllPrayerSchedule(): List<PrayerScheduleTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerScheduleList(prayerScheduleList: List<PrayerScheduleTable>)

    @Query("DELETE FROM prayer_schedule_table")
    suspend fun deleteAll()
}