package com.arnatech.jadwalshalat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnatech.jadwalshalat.data.local.table.TextMarqueeTable

@Dao
interface TextMarqueeDao {
    @Query("SELECT * FROM text_marquee_table")
    suspend fun getAllTextMarquee(): List<TextMarqueeTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextMarqueeList(textMarqueeList: List<TextMarqueeTable>)

    @Query("DELETE FROM text_marquee_table")
    suspend fun deleteAll()
}