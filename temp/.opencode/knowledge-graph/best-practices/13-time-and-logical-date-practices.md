# 13. Time And Logical Date Practices

This architecture benefits from a particularly reusable time model.

### 13.1 One production clock access point

A dedicated production time provider should be the sole allowed caller of `Clock.System.now()`.

A `TimeProvider` interface lives in `shared/core-domain` and should be injected through constructors into any class that needs the current time, current date, logical month, or shared date formatting. The production implementation is `SystemTimeProvider`; tests should use `FakeTimeProvider` from `shared/core-testing`.

Core API:

- `nowMillis()` for persisted timestamps and event creation times
- `today()` for current local date behavior
- `currentYearMonth()` for logical period filtering
- `formatDate(...)` and `formatDateTime(...)` for deterministic English shared UI formatting

Rules:
- The only allowed call to `Clock.System.now()` in the entire codebase is inside the concrete `TimeProvider` implementation.
- Any `Clock.System.now()` call in feature code, repositories, ViewModels, or utils is a defect and must be replaced with the appropriate `TimeProvider` API.
- Use `TimeProvider` injection for: record creation timestamps, logical month/year filters, date comparisons, shared date/date-time formatting, and any other time-derived value.
- Date fields in contracts and code must be named by meaning and unit, for example `createdAtMillis`, `occurredAtMillis`, or `dueAtMillis`; avoid ambiguous names such as `date`, `dateTime`, or `dateMillis`.
- Do not create feature-local date formatting helpers in common code; use `TimeProvider.formatDate(...)` or `TimeProvider.formatDateTime(...)`.
- Shared formatting is deterministic English-only. Do not use `java.time.format`, `java.text.*`, `java.util.*`, or platform formatters in common `src`.
- Do NOT use `Clock.System.now()` in companion objects, default parameter values, or top-level property initializers.
- The `FakeTimeProvider` in `shared/core-testing` should be used in all tests that involve time-sensitive behavior.

### 13.2 Logical date is different from calendar date

The app supports the idea that the user's logical day may not reset at midnight.

Reusable principle:

- business time should be modeled explicitly when product rules depend on a special day boundary

### 13.3 Verification rule

Before signing off a feature that involves time-sensitive behavior:
1. Grep the feature module for `Clock.System.now()`.
2. If any occurrence exists outside the `TimeProvider` implementation, flag it as a defect.
3. Verify the feature's DI module provides `TimeProvider` to the relevant constructor.
4. Verify shared UI date text uses `formatDate(...)` or `formatDateTime(...)` instead of ad hoc formatting.
