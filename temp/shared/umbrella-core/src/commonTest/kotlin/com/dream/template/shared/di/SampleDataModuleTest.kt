package com.dream.template.shared.di

import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.TimeProvider
import com.dream.template.shared.core.data.local.SampleDatabase
import com.dream.template.shared.core.data.local.SampleDatabaseFactory
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

class SampleDataModuleTest {
    @Test
    fun resolvesCoreAndSampleDataDependencies() {
        val testFactory = object : SampleDatabaseFactory {
            override fun createBuilder(): androidx.room.RoomDatabase.Builder<SampleDatabase> {
                return androidx.room.Room.databaseBuilder<SampleDatabase>(name = "test.db")
                    .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
            }
        }
        
        val koin = koinApplication {
            modules(sampleDataModule(testFactory))
        }.koin

        assertNotNull(koin.get<TimeProvider>())
        assertNotNull(koin.get<SampleRepository>())
    }
}