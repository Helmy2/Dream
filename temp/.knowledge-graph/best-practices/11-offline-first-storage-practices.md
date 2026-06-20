# 11. Offline-First Storage Practices

This architecture works especially well in local-first applications.

### 11.1 Shared Room database helpers

Common Room builder setup should live in `shared/core/data` so feature data modules do not duplicate driver or query dispatcher policy.

Best practice:

- put bundled SQLite driver setup in the shared data core module
- use KMP-safe coroutine dispatchers directly, such as `Dispatchers.Default`, instead of a template-specific dispatcher provider abstraction
- keep feature-specific databases, DAOs, entities, and factories in the feature data module unless multiple features truly share one schema

### 11.2 Single shared Room database

The app can use one shared database for multiple features.

Why this is useful:

- one persistence entry point
- feature-specific tables can still live together cleanly
- easier migrations and schema export management

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

### 11.5 DAO exposure through DI

The database module exposes DAOs individually through Koin.

Why it helps:

- repositories depend only on the DAO they need
- fewer broad dependencies
- easier test replacement
