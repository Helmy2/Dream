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

### 12.3 KMP DataStore setup in `shared/core-data`

DataStore Preferences is multiplatform (Android and iOS here) using expect/actual. The template ships with a working implementation in `shared/core-data/src/.../datastore/`.

**Dependencies** (in `shared/core-data/module.yaml`):
```yaml
dependencies:
  - $libs.androidx.datastore.preferences.core: exported
```

**Common source** (`src`):
```kotlin
expect fun createDataStore(context: Any? = null): DataStore<Preferences>
```

**Android** (`src@android`):
```kotlin
actual fun createDataStore(context: Any?): DataStore<Preferences> {
    val appContext = (context as Context).applicationContext
    return PreferenceDataStoreFactory.create {
        appContext.filesDir.resolve("settings.pb")
    }
}
```

**iOS** (`src@ios`):
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

**DI wiring** (in `shared/core-data` or umbrella):
```kotlin
single { createDataStore(appContext) } // appContext = Context on Android, null elsewhere
```

Key points:
- `datastore-preferences-core` (not `datastore-preferences`) for platform-agnostic API
- `PreferenceDataStoreFactory` for the Android implementation
- `OkioStorage` on iOS native (okio is transitively included by DataStore)
- Host app passes `Context` on Android; other platforms pass `null`
- The template provides `createDataStore(context: Any?): DataStore<Preferences>` as an expect/actual factory in `shared/core-data`
- A `DataStoreSessionStorage` implementation is available in `shared/core-data/src/.../session/` for optional auth/session token persistence
- Both DataStore and SessionStorage are wired into the Koin DI module in `shared/umbrella-core`
