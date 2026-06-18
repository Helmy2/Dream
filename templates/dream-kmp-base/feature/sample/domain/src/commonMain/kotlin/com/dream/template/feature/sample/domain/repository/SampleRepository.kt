package com.dream.template.feature.sample.domain.repository

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.shared.core.domain.Result

interface SampleRepository {
    fun loadItems(): Result<List<SampleItem>>
}
