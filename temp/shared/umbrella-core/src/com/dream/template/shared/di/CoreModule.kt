package com.dream.template.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dream.template.shared.core.data.datastore.createDataStore
import com.dream.template.shared.core.data.network.HttpClientFactory
import com.dream.template.shared.core.data.session.DataStoreSessionStorage
import com.dream.template.shared.core.domain.SystemTimeProvider
import com.dream.template.shared.core.domain.TimeProvider
import com.dream.template.shared.core.domain.session.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Feature-independent infrastructure: time, preferences storage, session storage, and HTTP.
 * Feature modules contribute their own Koin modules on top of this one.
 */
fun coreModule(appContext: Any? = null) = module {
    single<TimeProvider> { SystemTimeProvider }

    single<DataStore<Preferences>> { createDataStore(appContext) }
    singleOf(::DataStoreSessionStorage).bind<SessionStorage>()
    single {
        HttpClientFactory(
            sessionStorage = get<SessionStorage>(),
            baseUrl = null,
        )
    }
}
