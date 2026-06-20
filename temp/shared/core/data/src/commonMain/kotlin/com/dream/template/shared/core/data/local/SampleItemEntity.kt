package com.dream.template.shared.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample_items")
data class SampleItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val position: Int
)