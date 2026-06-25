package com.dream.template.feature.sample.data.repository

import com.dream.template.feature.sample.domain.model.SampleCategory
import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.shared.core.data.local.SampleItemDao
import com.dream.template.shared.core.data.local.SampleItemEntity
import com.dream.template.shared.core.domain.Result
import com.dream.template.shared.core.testing.FakeTimeProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RoomSampleRepositoryTest {
    @Test
    fun seedsStableStarterResourceKeysWhenRoomIsEmpty() = runTest {
        val dao = FakeSampleItemDao()
        val repository = RoomSampleRepository(
            dao = dao,
            timeProvider = FakeTimeProvider(current = 500L),
        )

        val loaded = assertIs<Result.Success<List<SampleItem>>>(repository.loadItems()).value

        assertEquals(3, loaded.size)
        assertEquals("contract-first", dao.items[0].id)
        assertEquals("sample.seed.contract_first.title", dao.items[0].title)
        assertEquals("sample.seed.contract_first.description", dao.items[0].description)
        assertEquals("shared-architecture", dao.items[1].id)
        assertEquals("sample.seed.shared_architecture.title", dao.items[1].title)
        assertEquals("sample.seed.shared_architecture.description", dao.items[1].description)
        assertEquals("desktop-preview", dao.items[2].id)
        assertEquals("sample.seed.desktop_preview.title", dao.items[2].title)
        assertEquals("sample.seed.desktop_preview.description", dao.items[2].description)
    }

    @Test
    fun createsItemThroughDaoWithCurrentTime() = runTest {
        val dao = FakeSampleItemDao()
        val repository = RoomSampleRepository(
            dao = dao,
            timeProvider = FakeTimeProvider(current = 500L),
        )

        val created = assertIs<Result.Success<*>>(
            repository.createItem(" New ", " Description ", SampleCategory.Preview)
        ).value as com.dream.template.feature.sample.domain.model.SampleItem

        assertEquals("sample-500", created.id)
        assertEquals("New", created.title)
        assertEquals("Description", created.description)
        assertEquals(SampleCategory.Preview, created.category)
        assertEquals(500L, created.occurredAtMillis)
        assertEquals(500L, dao.items.last().updatedAtMillis)
    }

    @Test
    fun updatesItemThroughDaoWithCurrentTimeAndExistingPosition() = runTest {
        val dao = FakeSampleItemDao(
            SampleItemEntity(
                id = "contract-first",
                title = "Contract-first features",
                description = "Generate compact contracts.",
                category = SampleCategory.Contract.name,
                occurredAtMillis = 100L,
                updatedAtMillis = 100L,
                position = 7,
            )
        )
        val repository = RoomSampleRepository(
            dao = dao,
            timeProvider = FakeTimeProvider(current = 600L),
        )
        val original = assertIs<Result.Success<*>>(repository.loadItem("contract-first")).value
            as com.dream.template.feature.sample.domain.model.SampleItem

        val updated = assertIs<Result.Success<*>>(
            repository.updateItem(
                original.update(
                    title = "Updated title",
                    description = "Updated description",
                    category = SampleCategory.Architecture,
                    updatedAtMillis = 550L,
                )
            )
        ).value as com.dream.template.feature.sample.domain.model.SampleItem

        assertEquals("Updated title", updated.title)
        assertEquals(SampleCategory.Architecture, updated.category)
        assertEquals(600L, updated.updatedAtMillis)
        assertEquals(7, dao.items.single().position)
    }
}

private class FakeSampleItemDao(
    vararg seedItems: SampleItemEntity,
) : SampleItemDao {
    val items = seedItems.toMutableList()

    override suspend fun getAll(): List<SampleItemEntity> = items.sortedBy { item -> item.position }

    override suspend fun getById(id: String): SampleItemEntity? = items.firstOrNull { item -> item.id == id }

    override suspend fun count(): Int = items.size

    override suspend fun insertAll(items: List<SampleItemEntity>) {
        this.items.addAll(items)
    }

    override suspend fun insert(item: SampleItemEntity) {
        items.removeAll { existing -> existing.id == item.id }
        items.add(item)
    }

    override suspend fun update(item: SampleItemEntity) {
        val index = items.indexOfFirst { existing -> existing.id == item.id }
        if (index >= 0) {
            items[index] = item
        } else {
            items.add(item)
        }
    }
}
