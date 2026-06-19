package com.dream.template.feature.sample.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SampleItemDao {
    @Query("SELECT * FROM sample_items ORDER BY position ASC")
    suspend fun getAll(): List<SampleItemEntity>

    @Query("SELECT COUNT(*) FROM sample_items")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SampleItemEntity>)
}