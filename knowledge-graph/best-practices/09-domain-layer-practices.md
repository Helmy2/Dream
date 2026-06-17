# 9. Domain Layer Practices

The domain layer in this architecture style contains some of the most reusable patterns.

### 8.1 Pure Kotlin domain

The domain layer should remain platform-agnostic and framework-light.

Good rules to preserve:

- domain models describe business meaning, not storage shape
- use cases express user or system actions clearly
- repository interfaces live in domain, implementations live in data

### 8.2 Explicit result modeling

One effective approach uses:

```kotlin
sealed interface Result<out D, out E : DomainError>
typealias EmptyResult<E> = Result<Unit, E>
```

Why this is valuable:

- expected failures are typed
- business logic does not rely on unchecked exceptions for normal flow
- callers must handle success and failure deliberately

Best practice to copy:

- use typed result wrappers for domain-facing operations
- define domain-specific error hierarchies per feature

### 8.3 Use case naming

Good use case names are generally action-oriented and readable:

- `ObserveQuranStateUseCase`
- `TogglePrayerStatusUseCase`
- `SetDynamicThemeUseCase`
- `SeedPrayerHabitsUseCase`

Best practice:

- name use cases after clear business intent
- prefer `ObserveX`, `SetX`, `ToggleX`, `MarkX`, `ComputeX`, `AdvanceX`

### 8.4 Time access through a contract

This is one of the strongest rules in the repository:

- domain and feature code must not call `Clock.System.now()` directly
- time flows through `TimeProvider`

Why it matters:

- testability
- deterministic logic
- centralized rollover behavior
- easier support for religious or product-specific day boundaries
