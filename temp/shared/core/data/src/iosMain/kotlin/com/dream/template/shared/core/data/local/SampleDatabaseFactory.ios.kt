package com.dream.template.shared.core.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
class IosSampleDatabaseFactory : SampleDatabaseFactory {
    override fun createBuilder(): RoomDatabase.Builder<SampleDatabase> {
        val documentDirectory = requireNotNull(
            NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )?.path
        )
        val databasePath = "$documentDirectory/dream_sample.db"

        return Room.databaseBuilder<SampleDatabase>(
            name = databasePath
        )
    }
}