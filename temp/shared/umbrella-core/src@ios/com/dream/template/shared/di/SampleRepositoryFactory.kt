package com.dream.template.shared.di

import com.dream.template.feature.sample.data.repository.RoomSampleRepository
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.data.local.IosSampleDatabaseFactory
import com.dream.template.shared.core.data.local.createSampleDatabase
import com.dream.template.shared.core.domain.SystemTimeProvider

fun iosSampleRepository(): SampleRepository {
    val database = createSampleDatabase(IosSampleDatabaseFactory())
    return RoomSampleRepository(
        dao = database.sampleItemDao(),
        timeProvider = SystemTimeProvider,
    )
}
