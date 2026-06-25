# 12. Settings And Preference Storage

Settings can be stored through DataStore Preferences and mapped into a domain model.

### 12.1 Observe one typed settings object

A settings repository can expose `Flow<AppSettings>` instead of many unrelated flows.

This is a strong practice because:

- consumers read one coherent settings snapshot
- defaults are centralized
- invalid stored enum values are safely recovered with fallback logic

### 12.2 Convert primitive storage into domain types at the repository boundary

Examples:

- strings into `CalculationMethod`
- strings into `AppTheme`
- strings into `AppLanguage`
- booleans and coordinates into `LocationMode`

Best practice:

- never let UI or use cases parse raw preference keys directly

### 12.3 KMP DataStore setup in `shared:core:data`

DataStore Preferences is multiplatform (Android, iOS, Desktop) using expect/actual:

**Dependencies** (in `shared:core:data/build.gradle.kts`):
```kotlin
commonMainApi(libs.androidx.datastore.preferences.core)
```

**Common source** (`commonMain`):
```kotlin
expect fun createDataStore(context: Any? = null): DataStore<Preferences>
```

**Android** (`androidMain`):
```kotlin
actual fun createDataStore(context: Any?): DataStore<Preferences> {
    val appContext = (context as Context).applicationContext
    return PreferenceDataStoreFactory.create {
        appContext.filesDir.resolve("settings.pb")
    }
}
```

**Desktop** (`desktopMain`):
```kotlin
actual fun createDataStore(context: Any?): DataStore<Preferences> {
    val appDir = File(System.getProperty("user.home"), ".app").apply { mkdirs() }
    return PreferenceDataStoreFactory.create {
        File(appDir, "settings.pb")
    }
}
```

**iOS** (`iosMain`):
```kotlin
actual fun createDataStore(context: Any?): DataStore<Preferences> {
    return DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = PreferencesSerializer,
            producePath = {
                val documentDirectory = NSFileManager.defaultManager
                    .URLForDirectory(NSDocumentDirectory, NSUserDomainMask, null, false, null)
                    ?.path ?: ""
                Path("$documentDirectory/settings.pb")
            }
        )
    )
}
```

**DI wiring** (in `shared:core:data` or umbrella):
```kotlin
single { createDataStore(appContext) } // appContext = Context on Android, null elsewhere
```

Key points:
- `datastore-preferences-core` (not `datastore-preferences`) for platform-agnostic API
- `FileStorage` via `PreferenceDataStoreFactory` on JVM targets
- `OkioStorage` on iOS native (okio is transitively included by DataStore)
- Host app passes `Context` on Android; other platforms pass `null`
