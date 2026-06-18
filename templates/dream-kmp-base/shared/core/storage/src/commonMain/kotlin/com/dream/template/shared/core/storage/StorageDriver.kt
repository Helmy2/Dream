package com.dream.template.shared.core.storage

interface StorageDriver {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun clear(key: String)
}

class InMemoryStorageDriver : StorageDriver {
    private val data = linkedMapOf<String, String>()

    override suspend fun putString(key: String, value: String) {
        data[key] = value
    }

    override suspend fun getString(key: String): String? = data[key]

    override suspend fun clear(key: String) {
        data.remove(key)
    }
}
