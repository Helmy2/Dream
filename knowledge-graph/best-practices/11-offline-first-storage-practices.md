# 11. Offline-First Storage Practices

This architecture works especially well in local-first applications.

### 10.1 Single shared Room database

The app can use one shared database for multiple features.

Why this is useful:

- one persistence entry point
- feature-specific tables can still live together cleanly
- easier migrations and schema export management

### 10.2 Entity design follows feature boundaries

The schema is feature-oriented:

- habits tables
- prayer cache table
- quran bookmark/log/goal tables
- athkar/tasbeeh tables

Best practice:

- let entities model stable persistence needs, not temporary UI shapes
- use singleton rows when the business object is logically singular, such as settings-like goals or bookmarks

### 10.3 Auto migrations where possible

The database uses auto migrations when possible and explicit migration logic when needed.

Best practice:

- prefer additive schema changes that Room can auto-migrate safely
- use explicit migration logic only when schema transformation requires it
- export schema files and keep them in version control

### 10.4 DAO exposure through DI

The database module exposes DAOs individually through Koin.

Why it helps:

- repositories depend only on the DAO they need
- fewer broad dependencies
- easier test replacement
