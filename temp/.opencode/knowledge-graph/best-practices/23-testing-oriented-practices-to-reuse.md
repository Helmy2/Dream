# 23. Testing-Oriented Practices To Reuse

Even without reviewing the full test suite, the architecture already exposes several good testing practices.

### 23.1 Inject time

This is the single highest-value testing rule in the app.

> **See also:** [13. Time And Logical Date Practices](13-time-and-logical-date-practices.md) for the full `TimeProvider` API and `FakeTimeProvider` usage.

### 23.2 Keep domain pure

Pure domain modules are easier to unit test and easier to port.

### 23.3 Keep repository boundaries explicit

Typed results and repository interfaces reduce mocking complexity.

For Room-backed feature data modules, common tests should not depend on Android-only database setup. Prefer:

- pure entity-to-domain and domain-to-entity mapping tests in `commonTest`
- repository tests against fake DAO or fake database interfaces when the repository can depend on a narrow boundary
- integration tests with a real Room database only where the target setup is known to run reliably

Do not claim a Room repository is fully covered by a presentation test that mocks the repository.

The sample feature includes copyable test examples:

- `feature/sample/domain/src/commonTest/.../SampleItemTest.kt` for simple domain model tests
- `feature/sample/domain/src/commonTest/.../SampleRepositoryContractTest.kt` for repository contract expectations
- `feature/sample/data/src/commonTest/.../SampleItemMapperTest.kt` for data/entity mapping tests

### 23.4 Prefer flows for observable state sources

Flows make settings, logs, and derived state easier to observe consistently in both production and tests.

### 23.5 Keep shared test utilities in shared/core/testing

Reusable fake providers and coroutine-aware helpers should live in a shared testing module instead of being recreated in each feature.

Best practice:

- keep fake time and dispatcher providers in `shared/core/testing`
- declare test helper classes (fakes, builders, fixtures) as `internal` rather than `private` when they may be shared across test files in the same module
- add coroutine test dependencies once in the shared testing module and reuse them from feature tests
- test feature behavior at the feature layer and queue semantics at the shared presentation layer
- use `@BeforeTest` and `@AfterTest` for repeated main dispatcher setup in ViewModel tests instead of inlining `Dispatchers.setMain(...)` in every test
- prefer explicit flow/state testing such as Turbine where transition order matters
- construct meaningful date, month, and time cases with `kotlinx-datetime` helpers instead of unexplained raw epoch values

### 23.6 Use Mokkery for mocks

The project bundles [Mokkery](https://mokkery.dev) 3.4.1 as the KMP-native mocking library. It uses a Kotlin compiler plugin (not reflection), so it works on all targets including iOS and Wasm.

- **No per-module setup** — the Mokkery plugin and `commonTest` dependency are applied automatically via `DreamKmpConventionPlugin`
- **Suspend-friendly** — use `everySuspend` for coroutine methods and `verifySuspend` for interaction checks
- **Mock at method level** — create mocks inside individual test methods rather than as shared fields to keep tests isolated

```kotlin
val repository = mock<SampleRepository> {
    everySuspend { loadItems() } returns Result.Success(listOf(...))
}
val viewModel = SampleViewModel(repository = repository)
```

For paused or conditional answers, use `calls`:

```kotlin
val repository = mock<SampleRepository> {
    everySuspend { loadItems() } calls {
        releaseLoad.await()
        Result.Success(emptyList())
    }
}
```

Interaction verification is available when needed:

```kotlin
verifySuspend(exhaustiveOrder) {
    repository.loadItems()
}
```

Defaults to strict mode (un-stubbed calls throw). For very simple interfaces (single method), anonymous objects remain acceptable. Complex reusable fakes belong in `shared/core/testing`.
