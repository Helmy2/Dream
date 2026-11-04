# 11. Offline-First Storage Practices

This architecture works especially well in local-first applications.

### 11.1 Shared Room database helpers

Common Room builder setup should live in `shared/core-data` so feature data modules do not duplicate driver or query dispatcher policy.

Best practice:

- put bundled SQLite driver setup in the shared data core module
- use KMP-safe coroutine dispatchers directly, such as `Dispatchers.Default`, instead of a template-specific dispatcher provider abstraction
- for first-pass MVP iteration, destructive migration fallback is acceptable to recover from stale local test schemas across Android and iOS
- replace destructive fallback with real Room migrations when the app contract requires preserving existing local data

### 11.2 Single shared Room database (default pattern)

The Dream template uses one shared database class in `shared/core-data` for all features. This is the default pattern.

Where database classes live:

- `shared/core-data` owns the `@Database` class, entity definitions, DAO interfaces, platform-specific factories, and schema exports
- `shared/core-data` owns the `RoomDatabase.Builder` extension (`buildBundledRoomDatabase`)
- feature `data` modules own repository implementations, mappers, and DAO usage — they import DAOs from `shared/core-data`
- each feature adds its tables to the shared database class; new entities and DAOs are registered in the same `@Database` annotation

Why this is useful:

- one persistence entry point
- feature-specific tables coexist cleanly
- easier migrations and schema export management
- shared builder policy (driver, dispatchers, destructive fallback) stays in one place

When to use a per-feature database instead:

- only when a feature has a genuinely isolated schema that no other feature will ever share
- keep the database class, entity, and DAO in the feature `data` module in that case
- document the exception in `specs/decisions.yaml`

### 11.3 Entity design follows feature boundaries

The schema is feature-oriented:

- habits tables
- prayer cache table
- quran bookmark/log/goal tables
- athkar/tasbeeh tables

Best practice:

- let entities model stable persistence needs, not temporary UI shapes
- use singleton rows when the business object is logically singular, such as settings-like goals or bookmarks

### 11.4 Auto migrations where possible

The database uses auto migrations when possible and explicit migration logic when needed.

Best practice:

- prefer additive schema changes that Room can auto-migrate safely
- use explicit migration logic only when schema transformation requires it
- export schema files and keep them in version control
- do not rely on destructive fallback once users have real data that must be preserved

### 11.5 DAO exposure through DI

The database module exposes DAOs individually through Koin.

Why it helps:

- repositories depend only on the DAO they need
- fewer broad dependencies
- easier test replacement

### 11.6 Room KMP setup reference

**Dependencies** (in `shared/core-data/module.yaml`):
```yaml
dependencies:
  - $libs.androidx.room.runtime: exported
  - $libs.androidx.room.ktx: exported
settings:
  kotlin:
    ksp:
      processors: [$libs.androidx.room.compiler]   # unqualified, not platform-scoped
```

**Database class** (`src`):
```kotlin
@Database(
    entities = [SampleEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sampleDao(): SampleDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
```

**Android factory** (`src@android`):
```kotlin
fun getRoomDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_database"
    ).build()
}
```

**iOS factory** (`src@ios`):
```kotlin
fun getRoomDatabase(): AppDatabase {
    val dbFile = NSFileManager.defaultManager
        .URLForDirectory(NSDocumentDirectory, NSUserDomainMask, null, false, null)
        ?.path?.let { "$it/app_database" } ?: "app_database"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile,
        factory = { BundledSQLiteDriver() }
    ).build()
}
```

**Schema export** (in `shared/core-data/module.yaml`):
```yaml
settings:
  kotlin:
    ksp:
      processorOptions:
        room.schemaLocation: ./schemas
```
Schemas export to `shared/core-data/schemas` and are committed.

Best practice:

- export schema files to version control for migration verification
- use `BundledSQLiteDriver` for iOS (not the Android-only SQLite driver)
- keep the database class in `shared/core-data`, not in feature modules
- add new entities and DAOs to the existing `@Database` annotation

> **Reference:** [Android Room KMP documentation](https://developer.android.com/kotlin/multiplatform/room)
