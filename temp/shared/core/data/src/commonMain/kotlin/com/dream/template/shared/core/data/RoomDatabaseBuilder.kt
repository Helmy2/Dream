package com.dream.template.shared.core.data

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun <T : RoomDatabase> RoomDatabase.Builder<T>.buildBundledRoomDatabase(
    allowDestructiveMigration: Boolean = false,
): T {
    val builder = setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
    return if (allowDestructiveMigration) {
        builder.fallbackToDestructiveMigration(dropAllTables = true).build()
    } else {
        builder.build()
    }
}
