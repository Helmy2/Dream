package com.dream.template.shared.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

actual fun createDataStore(context: Any?): DataStore<Preferences> {
    val appContext = (context as Context).applicationContext
    return PreferenceDataStoreFactory.create {
        appContext.filesDir.resolve("dream_settings.pb")
    }
}