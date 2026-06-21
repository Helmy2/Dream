package com.dream.template.feature.sample.data.repository

import com.dream.template.feature.sample.data.mapper.toDomain
import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.data.local.SampleDatabase
import com.dream.template.shared.core.data.local.SampleItemEntity
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.core.domain.Result
import com.dream.template.shared.core.domain.runSuspendCatching

class RoomSampleRepository(
    private val database: SampleDatabase
) : SampleRepository {
    override suspend fun loadItems(): Result<List<SampleItem>> = runSuspendCatching(
        block = {
        val dao = database.sampleItemDao()
        if (dao.count() == 0) {
            dao.insertAll(defaultSampleEntities())
        }
        dao.getAll().map { entity -> entity.toDomain() }
    },
        onFailure = { AppError.Unknown }
    )

    private fun defaultSampleEntities(): List<SampleItemEntity> = listOf(
        SampleItemEntity(
            id = "contract-first",
            title = "Contract-first features",
            description = "Generate features from compact contracts stored in .mvp/features/.",
            position = 0
        ),
        SampleItemEntity(
            id = "shared-architecture",
            title = "Shared architecture",
            description = "Keep domain, data, presentation, and UI code in stable modules.",
            position = 1
        ),
        SampleItemEntity(
            id = "desktop-preview",
            title = "Desktop preview loop",
            description = "Use the desktop target as the fastest preview and repair surface.",
            position = 2
        )
    )
}
