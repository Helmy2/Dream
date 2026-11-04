# 10. Data Layer Practices

The data layer is designed to translate external systems into domain-safe behavior.

### 10.1 Repository implementations own translation

Repositories should:

- talk to Room, DataStore, APIs, sensors, or platform adapters
- map DTOs/entities into domain models
- convert infrastructure failures into domain errors
- expose domain-friendly return types

### 10.2 Use `safeCall` for local writes and exception mapping

One good pattern is using `safeCall` to catch non-cancellation exceptions and map them into typed failures.

Why this is good:

- reduces repeated `try/catch`
- preserves coroutine cancellation
- creates a standard repository error boundary

Rule to copy exactly:

- always rethrow `CancellationException`
- never swallow coroutine cancellation in repository helpers

### 10.3 Cache before network when appropriate

A repository implementation can check cached data first, then fetch remote data only if needed.

Reusable lesson:

- prefer local cache as the first read path in offline-first or low-churn features
- persist normalized data after remote fetch
- return domain models, not raw remote objects

### 10.4 Named `HttpClient` instances per feature

A strong network boundary pattern is using a per-feature Ktor client setup. The template provides a shared `HttpClientFactory` in `shared/core-data/src/.../network/` that feature data modules can use to create configured clients.

Why it is smart:

- feature network behavior stays isolated
- logging/content negotiation can differ per feature
- ownership is clear

Best practice:

- each data module that talks to a backend should own its own named client unless there is a strong reason to share one

### 10.5 Mappers belong near their boundary

The repository uses dedicated mapper files for DTO/entity conversion.

Best practice:

- keep mapping logic out of UI and use cases
- separate storage shape from domain shape
- use dedicated mapper classes at important boundaries

Recommended mapper placement:

- data layer: DTO <-> entity <-> domain mapping stays in the **feature's** data layer, not the shared data layer
- presentation layer: domain -> UI mapping stays in presentation

Anti-example: mappers that convert `Entity → FeatureDomainModel` belong in `feature/<name>-data/.../mapper/`, not in `shared/core-data/mapper/`. The shared data module owns entities and DAOs, not domain-bound mappers. Placing feature mappers in shared data creates unnecessary coupling and breaks the feature boundary.

Recommended mapper style:

- prefer mapper classes over extension functions for presentation mapping
- keep one mapper focused on one transformation direction or one feature area
- make mapper dependencies explicit through constructor injection when needed
