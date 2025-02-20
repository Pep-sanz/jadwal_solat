package com.arnatech.jadwalshalat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnatech.jadwalshalat.data.local.table.MosqueTable

@Dao
interface MosqueDao {
    @Query("SELECT * FROM mosque_table LIMIT 1")
    suspend fun getMosque(): MosqueTable?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMosque(mosque: MosqueTable)

    @Query("DELETE FROM mosque_table")
    suspend fun deleteAll()
}
