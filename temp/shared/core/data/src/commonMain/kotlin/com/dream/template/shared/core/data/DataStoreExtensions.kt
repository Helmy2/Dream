package com.dream.template.shared.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(context: Any? = null): DataStore<Preferences>