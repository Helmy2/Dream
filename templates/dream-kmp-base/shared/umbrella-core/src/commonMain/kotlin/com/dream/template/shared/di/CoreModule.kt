package com.dream.template.shared.di

import com.dream.template.feature.sample.data.local.SampleDatabase
import com.dream.template.feature.sample.data.local.SampleDatabaseFactory
import com.dream.template.feature.sample.data.local.createSampleDatabase
import com.dream.template.feature.sample.data.repository.InMemorySampleRepository
import com.dream.template.feature.sample.data.repository.RoomSampleRepository
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.SystemTimeProvider
import com.dream.template.shared.core.domain.TimeProvider
import org.koin.dsl.bind
import org.koin.dsl.module

fun sampleDataModule(sampleDatabaseFactory: SampleDatabaseFactory?) = module {
    single<TimeProvider> { SystemTimeProvider }

    if (sampleDatabaseFactory == null) {
        single { InMemorySampleRepository() } bind SampleRepository::class
    } else {
        single { sampleDatabaseFactory }
        single<SampleDatabase> { createSampleDatabase(get()) }
        single<SampleRepository> { RoomSampleRepository(get()) }
    }
}
