package com.dream.template.shared.di

import com.dream.template.shared.core.domain.TimeProvider
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

class CoreModuleTest {
    @Test
    fun resolvesTimeProvider() {
        val koin = koinApplication {
            modules(coreModule())
        }.koin

        assertNotNull(koin.get<TimeProvider>())
    }
}
