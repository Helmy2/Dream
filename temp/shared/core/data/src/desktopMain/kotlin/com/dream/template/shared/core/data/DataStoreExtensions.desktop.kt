package com.dream.template.shared.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    val appDir = File(
        System.getProperty("user.home"),
        ".dream"
    ).apply {
        mkdirs()
    }
    return PreferenceDataStoreFactory.create {
        File(appDir, "dream_settings.pb")
    }
}