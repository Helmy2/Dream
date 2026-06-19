package com.dream.template.feature.sample.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class SampleDatabaseFactory(
    private val context: Context
) {
    actual fun createBuilder(): RoomDatabase.Builder<SampleDatabase> {
        val appContext = context.applicationContext
        val databaseFile = appContext.getDatabasePath("dream_sample.db")

        return Room.databaseBuilder<SampleDatabase>(
            context = appContext,
            name = databaseFile.absolutePath
        )
    }
}