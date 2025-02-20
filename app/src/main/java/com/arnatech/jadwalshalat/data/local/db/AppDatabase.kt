package com.arnatech.jadwalshalat.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arnatech.jadwalshalat.data.local.db.dao.ConfigurationDao
import com.arnatech.jadwalshalat.data.local.table.DeviceTable
import com.arnatech.jadwalshalat.data.local.db.dao.DeviceDao
import com.arnatech.jadwalshalat.data.local.db.dao.MosqueDao
import com.arnatech.jadwalshalat.data.local.db.dao.PrayerScheduleDao
import com.arnatech.jadwalshalat.data.local.db.dao.SliderDao
import com.arnatech.jadwalshalat.data.local.db.dao.TextMarqueeDao
import com.arnatech.jadwalshalat.data.local.table.ConfigurationTable
import com.arnatech.jadwalshalat.data.local.table.MosqueTable
import com.arnatech.jadwalshalat.data.local.table.PrayerScheduleTable
import com.arnatech.jadwalshalat.data.local.table.SlidersTable
import com.arnatech.jadwalshalat.data.local.table.TextMarqueeTable

@Database(
    entities = [
        DeviceTable::class,
        MosqueTable::class,
        PrayerScheduleTable::class,
        SlidersTable::class,
        TextMarqueeTable::class,
        ConfigurationTable::class
               ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun mosqueDao(): MosqueDao
    abstract fun prayerScheduleDao(): PrayerScheduleDao
    abstract fun sliderDao(): SliderDao
    abstract fun textMarqueeDao(): TextMarqueeDao
    abstract fun configurationDao(): ConfigurationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "device_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
