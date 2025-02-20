package com.arnatech.jadwalshalat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnatech.jadwalshalat.data.local.table.ConfigurationTable

@Dao
interface ConfigurationDao {
    @Query("SELECT * FROM configuration_table LIMIT 1")
    suspend fun getConfiguration(): ConfigurationTable?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguration(configuration: ConfigurationTable)

    @Query("DELETE FROM configuration_table")
    suspend fun deleteAll()
}