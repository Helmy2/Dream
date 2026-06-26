package com.dream.template.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dream.template.feature.sample.data.repository.RoomSampleRepository
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.data.datastore.createDataStore
import com.dream.template.shared.core.data.local.SampleDatabase
import com.dream.template.shared.core.data.local.SampleDatabaseFactory
import com.dream.template.shared.core.data.local.SampleItemDao
import com.dream.template.shared.core.data.local.createSampleDatabase
import com.dream.template.shared.core.data.network.HttpClientFactory
import com.dream.template.shared.core.data.session.DataStoreSessionStorage
import com.dream.template.shared.core.domain.SystemTimeProvider
import com.dream.template.shared.core.domain.TimeProvider
import com.dream.template.shared.core.domain.session.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun sampleDataModule(
    sampleDatabaseFactory: SampleDatabaseFactory,
    appContext: Any? = null,
) = module {
    single<TimeProvider> { SystemTimeProvider }
    single { sampleDatabaseFactory }
    single<SampleDatabase> { createSampleDatabase(get()) }
    single<SampleItemDao> { get<SampleDatabase>().sampleItemDao() }
    singleOf(::RoomSampleRepository).bind<SampleRepository>()

    single<DataStore<Preferences>> { createDataStore(appContext) }
    singleOf(::DataStoreSessionStorage).bind<SessionStorage>()
    single {
        HttpClientFactory(
            sessionStorage = get<SessionStorage>(),
            baseUrl = null,
        )
    }
}