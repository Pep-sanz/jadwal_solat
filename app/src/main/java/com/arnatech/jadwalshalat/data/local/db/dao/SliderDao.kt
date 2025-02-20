package com.arnatech.jadwalshalat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnatech.jadwalshalat.data.local.table.SlidersTable

@Dao
interface SliderDao {
    @Query("SELECT * FROM sliders_table")
    suspend fun getAllSliders(): List<SlidersTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlidersList(slidersList: List<SlidersTable>)

    @Query("DELETE FROM sliders_table")
    suspend fun deleteAll()
}
