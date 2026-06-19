package com.dream.template.feature.sample.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class SampleDatabaseFactory {
    actual fun createBuilder(): RoomDatabase.Builder<SampleDatabase> {
        val appSupportDirectory = File(
            System.getProperty("user.home"),
            "Library/Application Support/DreamTemplate"
        ).apply {
            mkdirs()
        }
        val databaseFile = File(appSupportDirectory, "dream_sample.db")

        return Room.databaseBuilder<SampleDatabase>(
            name = databaseFile.absolutePath
        )
    }
}