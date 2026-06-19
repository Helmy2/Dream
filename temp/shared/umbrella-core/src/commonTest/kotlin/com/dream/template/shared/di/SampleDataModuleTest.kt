package com.dream.template.shared.di

import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.TimeProvider
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

class SampleDataModuleTest {
    @Test
    fun resolvesCoreAndSampleDataDependencies() {
        val koin = koinApplication {
            modules(sampleDataModule(sampleDatabaseFactory = null))
        }.koin

        assertNotNull(koin.get<TimeProvider>())
        assertNotNull(koin.get<SampleRepository>())
    }
}