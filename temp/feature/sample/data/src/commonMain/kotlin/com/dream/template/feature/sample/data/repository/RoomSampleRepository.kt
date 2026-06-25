package com.dream.template.feature.sample.data.repository

import com.dream.template.feature.sample.data.mapper.toDomain
import com.dream.template.feature.sample.data.mapper.toEntity
import com.dream.template.feature.sample.domain.model.SampleCategory
import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.data.local.SampleItemDao
import com.dream.template.shared.core.data.local.SampleItemEntity
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.core.domain.Result
import com.dream.template.shared.core.domain.TimeProvider
import com.dream.template.shared.core.domain.runSuspendCatching

class RoomSampleRepository(
    private val dao: SampleItemDao,
    private val timeProvider: TimeProvider,
) : SampleRepository {
    override suspend fun loadItems(): Result<List<SampleItem>> = runSuspendCatching(
        block = {
            seedIfNeeded()
            dao.getAll().map { entity -> entity.toDomain() }
        },
        onFailure = { AppError.Unknown }
    )

    override suspend fun loadItem(id: String): Result<SampleItem?> = runSuspendCatching(
        block = {
            seedIfNeeded()
            dao.getById(id)?.toDomain()
        },
        onFailure = { AppError.Unknown }
    )

    override suspend fun createItem(
        title: String,
        description: String,
        category: SampleCategory,
    ): Result<SampleItem> = runSuspendCatching(
        block = {
            seedIfNeeded()
            val now = timeProvider.nowMillis()
            val item = SampleItem(
                id = "sample-$now",
                title = title.trim(),
                description = description.trim(),
                category = category,
                occurredAtMillis = now,
                updatedAtMillis = now,
            )
            dao.insert(item.toEntity(position = dao.count()))
            item
        },
        onFailure = { AppError.Unknown }
    )

    override suspend fun updateItem(item: SampleItem): Result<SampleItem> = runSuspendCatching(
        block = {
            val now = timeProvider.nowMillis()
            val existing = dao.getById(item.id)
            val updated = item.copy(updatedAtMillis = now)
            dao.update(updated.toEntity(position = existing?.position ?: dao.count()))
            updated
        },
        onFailure = { AppError.Unknown }
    )

    private suspend fun seedIfNeeded() {
        if (dao.count() == 0) {
            dao.insertAll(defaultSampleEntities())
        }
    }

    private fun defaultSampleEntities(): List<SampleItemEntity> = listOf(
        SampleItemEntity(
            id = "contract-first",
            title = "sample.seed.contract_first.title",
            description = "sample.seed.contract_first.description",
            category = SampleCategory.Contract.name,
            occurredAtMillis = 1_720_000_000_000,
            updatedAtMillis = 1_720_000_000_000,
            position = 0,
        ),
        SampleItemEntity(
            id = "shared-architecture",
            title = "sample.seed.shared_architecture.title",
            description = "sample.seed.shared_architecture.description",
            category = SampleCategory.Architecture.name,
            occurredAtMillis = 1_720_086_400_000,
            updatedAtMillis = 1_720_086_400_000,
            position = 1,
        ),
        SampleItemEntity(
            id = "desktop-preview",
            title = "sample.seed.desktop_preview.title",
            description = "sample.seed.desktop_preview.description",
            category = SampleCategory.Preview.name,
            occurredAtMillis = 1_720_172_800_000,
            updatedAtMillis = 1_720_172_800_000,
            position = 2,
        )
    )
}
