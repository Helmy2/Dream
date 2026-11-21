package com.dream.template.feature.sample.data.di

import com.dream.template.feature.sample.data.local.SampleDatabase
import com.dream.template.feature.sample.data.local.SampleDatabaseFactory
import com.dream.template.feature.sample.data.local.SampleItemDao
import com.dream.template.feature.sample.data.local.createSampleDatabase
import com.dream.template.feature.sample.data.repository.RoomSampleRepository
import com.dream.template.feature.sample.domain.repository.SampleRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Data-layer wiring owned by the sample feature. Needs `TimeProvider` from the shared core module.
 */
fun sampleDataModule(sampleDatabaseFactory: SampleDatabaseFactory) = module {
    single { sampleDatabaseFactory }
    single<SampleDatabase> { createSampleDatabase(get()) }
    single<SampleItemDao> { get<SampleDatabase>().sampleItemDao() }
    singleOf(::RoomSampleRepository).bind<SampleRepository>()
}
