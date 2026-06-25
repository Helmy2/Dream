# Dream OpenCode Operating Guide

## Purpose

This guide defines the default working behavior for OpenCode agents generating, reviewing, verifying, or repairing Dream projects.

Use this guide together with:

- `.opencode/guidance/opencode-agent-map.md`
- `.knowledge-graph/PRODUCT.md`
- `.knowledge-graph/README.md`
- relevant files in `.knowledge-graph/best-practices/`
- `AGENTS.md`
- the selected template README and source files

## Required Read Order

Unless a narrower task makes some steps irrelevant, use this read order:

1. `.knowledge-graph/PRODUCT.md`
2. `.opencode/guidance/opencode-agent-map.md`
3. `AGENTS.md`
4. `.knowledge-graph/README.md`
5. relevant `.knowledge-graph/best-practices/*.md` files for the current concern
6. selected template README
7. target project `.mvp/` metadata if it already exists

## Default Mission

OpenCode agents working for Dream should help produce Kotlin Multiplatform MVPs that are:

- runnable
- verifiable
- readable
- repairable
- aligned with the Dream architecture rules

OpenCode agents should not optimize only for fastest generation.
They should optimize for correct generation plus future repairability.

## Agent Role Model

Dream should default to these OpenCode roles:

- `general` orchestrates the workflow
- `dream-explore` performs fast discovery
- `dream-context-scout` builds focused context packs
- `implementer` applies code and file changes
- `inspector` performs the final QA gate

Use the smallest capable agent for each subtask.

## Non-Negotiable Rules

Unless a higher-priority project-specific document explicitly overrides them, all Dream OpenCode agents must preserve these rules:

- preserve thin app shells
- keep reusable infrastructure in shared modules
- place product behavior in feature modules
- enforce `Presentation -> Domain <- Data`
- keep domain pure Kotlin
- keep navigation centralized
- keep shared MVI primitives small
- keep routing concerns out of feature UI where practical
- keep user-visible shared UI strings in shared resources
- treat Android, iOS, and contract-selected Desktop as generated app product targets
- use Android CLI for Android environment checks, emulator/device setup, APK install, launch, layout/screenshot inspection, logs, and repair loops
- use `xcodebuild` and `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture
- use Maestro for black-box smoke and end-to-end user-flow checks after platform launch is healthy
- when Desktop is included in the app contract, validate it as a supported target and use `:desktopApp:hotRunJvm` for interactive runs
- not report success until verification is green or a blocker is clearly documented

## Mandatory Graph Usage

Before generating or reviewing code, the acting OpenCode agent should load the relevant graph-backed rules for the task.

Minimum rule retrieval by task:

- app and feature generation: architecture, domain, data, presentation, navigation, resources, DI
- review: architecture, anti-patterns, exceptions, defaults
- repair: failing module rules plus any anti-patterns implicated by the failure

The acting agent should prefer explicit graph rules over vague inference.

## Template Usage Rules

Dream templates are not disposable examples.
They are the structural baseline for generated projects.

The `implementer` agent should:

- preserve valid module boundaries unless the requested product truly requires structural change
- replace sample feature content with real feature content when appropriate
- reuse shared core, design system, navigation, and build-logic modules where possible
- avoid collapsing the modular template into a simpler but less enforceable structure
- remove the `feature/sample` module from the generated app once the first real feature fully replaces it, unless a temporary scaffolding exception is explicitly documented

The `implementer` agent should not:

- move business logic into host app modules
- bypass shared infrastructure just to make a feature compile faster
- introduce cross-feature `impl`-to-`impl` coupling by default

## Generation Workflow

The default OpenCode-first Dream workflow is:

1. `general` reads the product request.
2. Dream starts from the user's plain natural-language request, not from manually precreated app-specific contract files.
3. `general` infers whether backend is needed.
4. `general` infers storage choice or asks for it if unclear.
5. `general` selects the appropriate Dream template.
6. `general` produces or coordinates `.mvp/app-contract.yaml`.
7. `general` produces or coordinates `.mvp/decisions.yaml`.
8. `general` produces or coordinates the first feature contract.
9. `dream-explore` or `dream-context-scout` gathers exact implementation context when needed.
10. `implementer` implements the feature inside the template.
11. `implementer` generates or updates tests.
12. `general` runs or coordinates verification.
13. `implementer` repairs failures until green or blocked.
14. `inspector` reviews substantial generation or repair work before validated success is reported.
15. `general` writes or coordinates `.mvp/verification-report.md` and feature implementation notes.

The user should not need to hand-author an expense tracker contract, app contract, or feature contract before Dream can start.
Those artifacts are Dream outputs, not Dream prerequisites.

## Contract Rules

Feature contracts must be created before implementation starts. `dream-general` owns producing or coordinating the contract, and `dream-implementer` must stop if no relevant contract exists.

Every feature contract should define:

- feature name
- goal
- entities and field names, including meaningful timestamp names such as `createdAtMillis` or `occurredAtMillis`
- actions and expected inputs
- screens
- loading, empty, content, and error states
- time-sensitive behavior, using the appropriate `TimeProvider` API whenever current time, dates, logical periods, formatting, or timestamps are involved
- design expectations for each state
- required tests by layer: domain, data, and presentation

If backend is enabled, contracts should also define:

- API base path
- endpoints
- request and response shapes

Implementation must map each contract action, state, entity field, and required test to code and test coverage explicitly. Green commands alone are not sufficient stop conditions. Dream should confirm contract-to-code parity, required test coverage, and final review status before reporting validated success.

Dependency assembly must stay in stable composition roots. Generated feature modules should use Koin compiler plugin DSL registration for repositories, ViewModels, and Navigation 3 entries. Do not use annotation-based Koin DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan` by default.

Navigation must stay centralized while route ownership stays local. Shared navigation should operate on `androidx.navigation3.runtime.NavKey`, and concrete destination route types should live in `feature/<name>/api`. Screens, ViewModels, and Koin Navigation 3 entry providers should live in `feature/<name>/impl`. Other features or the app shell navigate to this feature by depending only on `feature/<name>/api`; nothing outside the feature depends on `feature/<name>/impl`.

Feature paths, Gradle module names, and Kotlin packages must follow one convention. For multi-word feature concepts, prefer package-mirroring directories such as `feature/expense/details` with `com.example.feature.expense.details`, or use one slug consistently everywhere. Do not mix `feature/expense-details` folders with `feature.expense.details` packages unless a documented migration reason exists.

Feature UI must use Dream design-system components and `DreamTheme` tokens by default. Use raw Material3 or ad hoc Compose UI primitives only when no design-system component exists or when the feature needs behavior the wrapper does not support yet; document the exception and consider adding a shared wrapper.

Time-sensitive behavior must inject `TimeProvider` instead of reading system time directly. Use `nowMillis()` for persisted timestamps, `today()` for current-date behavior, `currentYearMonth()` for logical period filters, and `formatDate(...)` or `formatDateTime(...)` for deterministic English shared UI formatting. The only allowed direct `Clock.System.now()` call is inside the production `SystemTimeProvider` implementation. Tests for time-sensitive behavior should use `FakeTimeProvider` from `shared/core/testing` or an equivalent fixed-time fake.

Feature implementation should:

- create or update `feature/<name>/domain`
- create or update `feature/<name>/data`
- create or update `feature/<name>/api` with `NavKey` route types; no Koin definitions in `api`
- create or update `feature/<name>/impl` with screens, ViewModels, UI state/actions/events, and the Koin module that installs Navigation 3 entry providers
- keep domain-to-UI mapping in `impl`
- keep feature screens wired into the centralized shell through `impl` Koin navigation entries

If shared behavior appears across features, move it into the appropriate shared module instead of duplicating it. A feature may reuse another feature's domain contract when it is an explicit aggregator or dependent workflow, but `feature/<name>/impl` must not depend directly on another feature's `impl`. Cross-feature navigation goes through the target feature's `api`. Cross-feature data implementation reuse is a temporary exception for MVP work and must be recorded in `.mvp/decisions.yaml`; extract to shared modules when two features equally own the same persisted concept.

Editable forms must be reachable with the software keyboard open. Prefer compact single-column forms on narrow screens, grouped fields on wider screens, adaptive chip wrapping, and primary actions close to active fields. Do not rely on `imePadding()` alone as proof that a save action is reachable.

## Git Rules

After a completed feature generation flow, Dream must inspect and commit the completed feature automatically.

Before the flow depends on git, verify the generated project is a git repository and that `git status`, `git diff`, and `git log --oneline -10` work. If git is unavailable or the project is not initialized, stop early with a precise environment/setup blocker instead of discovering it at commit time.

Required git flow:

- run `git status`
- run `git diff`
- run `git log --oneline -10`
- stage only the intended files for the generated feature and related `.mvp/` reports
- create a concise commit message, such as `Add expenses feature`

Do not auto-commit when verification is blocked, review is not approved, required reports are missing, or unrelated worktree changes make safe staging unclear. Never amend, force push, reset, or revert unrelated changes unless the user explicitly requests it.

## Verification Rules

At minimum, local-only Dream projects should pass the baseline build and test checks:

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
```

For modular projects, `allTests` should aggregate shared and feature tests.

Before implementation is treated as verification-ready, run environment preflights for Android SDK readiness, git readiness, and Maestro availability. Missing tooling is an environment blocker, not a successful validation result.

Final validation should then exercise the real product targets:

1. Android CLI (`https://developer.android.com/tools/agents/android-cli`) for Android environment checks, emulator/device setup, APK install, launch, layout/screenshot inspection, and log capture.
2. `xcodebuild` plus `xcrun simctl` for iOS simulator build, install, launch, logs, and crash capture.
3. Feature-owned Maestro Android smoke flows for every user-visible generated or touched feature after Android launches cleanly.
4. Feature-owned Maestro iOS smoke flows for every user-visible generated or touched feature where reliable after iOS launches cleanly.
5. `./gradlew :desktopApp:compileKotlinJvm` plus `./gradlew :desktopApp:hotRunJvm` when `desktop` is included in the app contract.

Desktop is a supported target when the app contract includes it, but Desktop validation does not replace Android/iOS readiness checks. Dream must not report final success without the required platform validation, smoke coverage, and contract review, or a documented blocker.

Verification reports should use explicit status buckets for each check: `passed`, `blocked by environment`, `not run`, and `not applicable`. Do not blur an unrun smoke flow into a passed build result.

Maestro smoke flows should be feature-owned under `maestro/features/<feature-name>/android.yaml` and `maestro/features/<feature-name>/ios.yaml`. Every user-visible generated or touched feature should have at least one Android flow and one iOS flow when the feature is supported on that platform. Do not rely on one global smoke file to cover multiple features. Maestro smoke flows should run only after the target emulator/simulator is open, the app is installed, and platform tooling has confirmed the app launches. Maestro validates user behavior after runtime launch is healthy; it should not be treated as the install step. Start every smoke flow with `clearState` before `launchApp` unless the contract explicitly tests persistence across app launches. Maestro smoke flows should prove user outcomes with stable selectors. For iOS single-line edits, prefer `tapOn`, `eraseText`, `inputText`, `pressKey: Enter`, then the save action when `hideKeyboard` is unreliable. For detail screens, prefer full rendered-row assertions such as `Occurred: .*` instead of standalone labels. If no Android device or emulator is attached, the iOS app is not installed, or Maestro `launchApp` fails on a physical device while `adb install` and `adb shell am start` work, record a precise environment/tooling blocker and try an emulator-backed smoke run when feasible.

## Repair Rules

When verification fails, Dream should use this OpenCode pattern:

1. `general` identifies the failing module or runtime surface
2. `dream-explore` or `dream-context-scout` gathers the relevant context if needed
3. `implementer` loads the relevant graph rules for that layer
4. `implementer` prefers minimal fixes over broad rewrites
5. `implementer` preserves architecture boundaries while fixing the failure
6. `general` reruns verification

Dream should keep repairing until:

- the project is green
- a real environment or platform blocker is identified
- or the requested behavior is impossible under current constraints

## Stop Conditions

The current agent flow may stop only when one of these is true:

- requested work is implemented and verified
- a blocker is real, specific, and documented
- the user changed or narrowed the task

## Required Outputs

For generation tasks, Dream should leave behind:

- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- `.mvp/features/<feature>/contract.yaml`
- `.mvp/features/<feature>/test-plan.md`
- `.mvp/features/<feature>/implementation-report.md`
- `.mvp/verification-report.md`

Use `.opencode/guidance/templates/verification-report-template.md` when writing `.mvp/verification-report.md`.

For review or repair tasks, the responsible OpenCode agents should update the relevant report files when they are part of the current flow.
