# 13. Time And Logical Date Practices

This architecture benefits from a particularly reusable time model.

### 13.1 One production clock access point

A dedicated production time provider should be the sole allowed caller of `Clock.System.now()`.

A `TimeProvider` interface lives in `shared/core/domain` and should be injected (via constructor parameter) into any class that needs the current time.

Rules:
- The only allowed call to `Clock.System.now()` in the entire codebase is inside the concrete `TimeProvider` implementation.
- Any `Clock.System.now()` call in feature code, repositories, ViewModels, or utils is a defect and must be replaced with `timeProvider.now()`.
- Use `TimeProvider` injection for: record creation timestamps, logical month/year filters, date comparisons, and any other time-derived value.
- Do NOT use `Clock.System.now()` in companion objects, default parameter values, or top-level property initializers.
- The `FakeTimeProvider` in `shared/core/testing` should be used in all tests that involve time-sensitive behavior.

### 13.2 Logical date is different from calendar date

The app supports the idea that the user's logical day may not reset at midnight.

Reusable principle:

- business time should be modeled explicitly when product rules depend on a special day boundary

### 13.3 Verification rule

Before signing off a feature that involves time-sensitive behavior:
1. Grep the feature module for `Clock.System.now()`.
2. If any occurrence exists outside the `TimeProvider` implementation, flag it as a defect.
3. Verify the feature's DI module provides `TimeProvider` to the relevant constructor.
