package com.dream.template.shared.core.testing

import com.dream.template.shared.core.domain.TimeProvider

class FakeTimeProvider(
    private var current: Long = 0L
) : TimeProvider {
    override fun nowMillis(): Long = current

    fun advanceBy(deltaMillis: Long) {
        current += deltaMillis
    }
}
