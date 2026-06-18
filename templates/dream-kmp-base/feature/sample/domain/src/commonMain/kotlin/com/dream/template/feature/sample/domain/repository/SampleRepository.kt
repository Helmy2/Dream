package com.dream.template.feature.sample.domain.repository

import com.dream.template.feature.sample.domain.model.SampleItem

interface SampleRepository {
    fun loadItems(): List<SampleItem>
}
