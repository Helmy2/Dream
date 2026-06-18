# Dream Third Try Report

## Purpose

This file records the third end-to-end expense-tracker run, specifically the follow-up run that replaced in-memory expense storage with local persisted storage.

It captures:

- what improved relative to the second run
- whether the local-persistence implementation is structurally sound
- what workflow defects still remain in Dream itself
- whether this run should be treated as approved, partial, or blocked

## Run Summary

- Run type: third expense-tracker regression run focused on local persisted storage
- Prompt family: expense tracker MVP with local saved expenses
- Reported model: GPT 5.4
- Result: partial, but materially improved

Why still partial:

- the generated app now persists expenses locally and reloads them across repository recreation
- Android, Desktop, and iOS hosts all assemble the local repository in stable remembered roots
- delegated tooling still failed, so Dream still relied on manual fallback
- Dream still mutated `templates/dream-kmp-base` in place instead of instantiating a destination app copy
- `feature/sample` still remains beside `feature/expenses` because the template was mutated directly
- the generated `.mvp/features/expenses/implementation-report.md` still does not follow the required table-based template

## What Improved

- `LocalExpensesRepository` replaced the earlier in-memory expense repository
- local persistence is wired for all three host targets:
  - Android via `AndroidPreferencesStorageDriver`
  - Desktop via `DesktopPreferencesStorageDriver`
  - iOS via `IosUserDefaultsStorageDriver`
- repository recreation is covered by `LocalExpensesRepositoryTest.reloadsSavedExpensesFromSharedLocalDriver`
- malformed stored data now returns a failure instead of silently producing bad state
- the generated verification report correctly records delegated-tool failure and manual review fallback instead of claiming full approval

## Code Review Outcome

The storage-backed implementation is acceptable for the currently tested MVP path. The basic architecture remains aligned with Dream's rules:

- `Presentation -> Domain <- Data` boundaries are preserved
- time-sensitive behavior still flows through `GetCurrentExpenseMonthUseCase(SystemTimeProvider)`
- hosts assemble dependencies in stable remembered roots and pass the resulting `ExpensesViewModel` into the shared shell
- the repository uses one shared serialization path across platforms instead of duplicating feature logic per target

No blocking cross-platform defect was found for the currently tested add-and-reload flow.

One real code-level risk remains:

### Desktop storage has a hard payload ceiling

`DesktopPreferencesStorageDriver` uses Java `Preferences`, and `LocalExpensesRepository` stores the full expense database as one serialized string under a single key.

Why this matters:

- Java `Preferences` has a maximum value length of `8 * 1024`
- once the serialized expenses payload grows past that limit, desktop saves can start failing with an `IllegalArgumentException`
- the current tests only prove small-data persistence and malformed-value handling; they do not exercise realistic growth limits

Impact:

- this is not a blocker for a tiny MVP dataset
- it is a real implementation constraint and should be treated as a quality risk if Dream presents this as a reusable local-database pattern

## Remaining Dream Workflow Defects

### 1. Template instantiation is still wrong

Dream is still generating directly inside `templates/dream-kmp-base` instead of copying the template into a new destination app.

Why this matters:

- it mutates the reusable baseline
- it makes later regression runs less trustworthy
- it prevents safe cleanup of template-only surfaces inside the generated app copy

### 2. `feature/sample` is still present in the generated tree

Both `feature/sample` and `feature/expenses` remain in the current tree.

Why this matters:

- once the first real feature replaces the sample, the generated app copy should no longer carry `feature/sample`
- this remains unresolved because Dream is mutating the template rather than a destination copy

### 3. Delegated tooling failure is still unresolved

- `dream-implementer` failed with `NOT NULL constraint failed: session_message.seq`
- `dream-inspector` failed with the same error

Why this matters:

- Dream still depends on manual fallback for substantive runs
- approval remains operationally fragile even when code generation succeeds

### 4. Generated implementation reporting still lags the prompt rules

`.mvp/features/expenses/implementation-report.md` still uses the older summary format instead of the required contract-mapping table with `implemented|partial|blocked` rows.

Why this matters:

- the guidance was updated, but the emitted artifact still does not comply
- Dream still has a prompt-to-artifact parity gap

## Decision

- Feature-code quality for the reviewed local-storage path: acceptable for small-data MVP use
- Dream workflow quality: not yet approved
- Final decision: `partial`

## What To Fix Before The Next Try

1. Make Dream instantiate a destination app copy instead of mutating `templates/dream-kmp-base`.
2. Remove `feature/sample` from generated app copies as soon as the first real feature replaces it.
3. Fix delegated session/tool persistence failures so implementer and inspector can complete reliably.
4. Force emitted implementation reports to use the required table-based template.
5. Decide whether Dream's default desktop local-storage pattern should remain `Preferences`-backed or move to a file/database approach that does not cap the whole dataset at roughly 8 KB.