package com.dream.template.shared.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.data.local.SampleDatabase
import com.dream.template.shared.core.data.local.SampleDatabaseFactory
import com.dream.template.shared.core.domain.TimeProvider
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

class SampleDataModuleTest {
    @Test
    fun resolvesTimeProviderAndSampleRepository() {
        val testFactory = object : SampleDatabaseFactory {
            override fun createBuilder(): RoomDatabase.Builder<SampleDatabase> {
                return Room.databaseBuilder<SampleDatabase>(name = "test.db")
            }
        }

        val koin = koinApplication {
            modules(sampleDataModule(testFactory))
        }.koin

        assertNotNull(koin.get<TimeProvider>())
        assertNotNull(koin.get<SampleRepository>())
    }
}