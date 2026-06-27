# Dream OpenCode Operating Guide

## Purpose

This guide defines the default working behavior for OpenCode agents generating, reviewing, verifying, or repairing Dream projects.

Use this guide together with:

- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/knowledge-graph/README.md`
- relevant files in `.opencode/knowledge-graph/best-practices/`
- `AGENTS.md`
- the selected template README and source files

## Required Read Order

Unless a narrower task makes some steps irrelevant, use this read order:

1. `.opencode/knowledge-graph/PRODUCT.md`
2. `.opencode/guidance/opencode-agent-map.md`
3. `AGENTS.md`
4. `.opencode/knowledge-graph/README.md`
5. relevant `.opencode/knowledge-graph/best-practices/*.md` files for the current concern
6. selected template README
7. target project `.features/` metadata if it already exists

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

Dream uses these OpenCode agents, organized by role:

**Orchestrator:**
- `dream-general` orchestrates the workflow, interprets requests, breaks work into phases, delegates to subagents

**Read-Only Subagents:**
- `dream-explore` performs fast codebase discovery
- `dream-context-scout` builds focused read-only context packs
- `dream-search` handles external research, API docs, and troubleshooting

**Spec-Generation Subagent:**
- `dream-designer` translates feature contracts into UI/UX specs (no code)

**Code-Writing Subagents:**
- `dream-implementer` owns the KMP business layer: domain, data, api, and wiring (umbrella imports, DI registration, navigation entries)
- `dream-implementer-android` owns the Jetpack Compose presentation layer
- `dream-implementer-ios` owns the native SwiftUI presentation layer

**Verification Subagent:**
- `dream-tester` owns the full verification ladder: tests, builds, device, Maestro, and test-plan.md creation

**Review Subagent:**
- `dream-inspector` performs the final QA gate

Use the smallest capable agent for each subtask.

`dream-general` must not edit implementation files directly. It delegates all code edits (wiring, bug fixes, build fixes) to the relevant implementer subagent:
- `dream-implementer` for domain, data, api, and wiring (umbrella imports, DI registration, navigation entries)
- `dream-implementer-android` for Compose UI and `impl` failures
- `dream-implementer-ios` for SwiftUI and bridge failures

## Substantial Work Threshold

Dream-inspector review is required for substantial work. Substantial work includes:
- any new feature generation
- any repair that changes more than 2 files
- any change to shared modules (`shared/core/*`, `shared/designsystem`, `shared/navigation`, `shared/umbrella-*`)
- any change that modifies contracts, specs, or architecture rules

Non-substantial work (inspector optional): single-line corrections, documentation updates, or typo fixes that do not affect behavior.

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
- treat Android and iOS as generated app product targets
- use Android CLI for Android environment checks, emulator/device setup, APK install, launch, layout/screenshot inspection, logs, and repair loops
- use `xcodebuild` and `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture
- use Maestro for black-box smoke and end-to-end user-flow checks after platform launch is healthy
- support dual iOS UI strategies: when the project contains both `iosApp-compose` and `iosApp-swiftui`, the agent removes the unused folder and renames the kept one to `iosApp` during project instantiation
- when the project uses the native SwiftUI iOS strategy, load the dedicated iOS best-practice files for host structure, bridge rules, Xcode configuration, and XCTest packaging before editing native iOS code
- not report success until verification is green or a blocker is clearly documented

## Mandatory Graph Usage

Before generating or reviewing code, the acting OpenCode agent should load the relevant graph-backed rules for the task.

Minimum rule retrieval by task:

- app and feature generation: architecture, domain, data, presentation, navigation, resources, DI
- app and feature generation with native SwiftUI: also retrieve iOS project structure, KMP bridge, Xcode configuration, and iOS testing rules
- review: architecture, anti-patterns, exceptions, defaults
- repair: failing module rules plus any anti-patterns implicated by the failure

The acting agent should prefer explicit graph rules over vague inference.

## Template Usage Rules

Dream templates are not disposable examples.
They are the structural baseline for generated projects.

The code-writing agents (`dream-implementer`, `dream-implementer-android`, `dream-implementer-ios`) should:

- preserve valid module boundaries unless the requested product truly requires structural change
- replace sample feature content with real feature content when appropriate
- reuse shared core, design system, navigation, and build-logic modules where possible
- avoid collapsing the modular template into a simpler but less enforceable structure
- remove the `feature/sample` module from the generated app once the first real feature fully replaces it, unless a temporary scaffolding exception is explicitly documented

The code-writing agents should not:

- move business logic into host app modules
- bypass shared infrastructure just to make a feature compile faster
- introduce cross-feature `impl`-to-`impl` coupling by default

## Generation Workflow

The default OpenCode-first Dream workflow is:

1. `dream-general` reads the product request.
2. Dream starts from the user's plain natural-language request, not from manually precreated app-specific contract files.
3. `dream-general` infers whether backend is needed.
4. `dream-general` infers storage choice or asks for it if unclear.
5. `dream-general` selects the appropriate Dream template.
6. `dream-general` produces or coordinates `.features/app-contract.yaml`.
7. `dream-general` produces or coordinates `.features/decisions.yaml`.
8. `dream-general` produces or coordinates the first feature contract.
9. `dream-explore` or `dream-context-scout` gathers exact implementation context when needed.
10. `dream-search` is called if external research is needed for dependencies, APIs, or troubleshooting.
11. `dream-designer` produces `.features/<feature>/ui-spec.md` from the feature contract.
12. `dream-implementer` implements the KMP business layer: domain, data, api, and their tests, then wires feature modules into umbrella imports and DI registration.
13. `dream-implementer-android` implements the Jetpack Compose UI layer: screens, ViewModels, Koin navigation, tests.
14. `dream-implementer-ios` implements the native SwiftUI layer: views, ViewModels, bridge, XCTest.
15. `dream-tester` runs the full verification ladder: unit tests, platform builds, device install/launch, Maestro flows.
16. `dream-implementer`, `dream-implementer-android`, or `dream-implementer-ios` repairs failures (per layer) until green, blocked, or a skip condition is met (see Skip Conditions).
17. `dream-inspector` reviews substantial generation or repair work before validated success is reported.
18. `dream-general` writes or coordinates `.features/verification-report.md` and feature implementation notes.
19. `dream-general` writes `.features/<feature>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`.

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

If shared behavior appears across features, move it into the appropriate shared module instead of duplicating it. A feature may reuse another feature's domain contract when it is an explicit aggregator or dependent workflow, but `feature/<name>/impl` must not depend directly on another feature's `impl`. Cross-feature navigation goes through the target feature's `api`. Cross-feature data implementation reuse is a temporary exception for MVP work and must be recorded in `.features/decisions.yaml`; extract to shared modules when two features equally own the same persisted concept.

Editable forms must be reachable with the software keyboard open. Prefer compact single-column forms on narrow screens, grouped fields on wider screens, adaptive chip wrapping, and primary actions close to active fields. Do not rely on `imePadding()` alone as proof that a save action is reachable.

## Git Rules

After a completed feature generation flow, Dream must inspect the completed feature, prepare the commit summary, and ask the user for explicit approval before committing.

Before the flow depends on git, verify the generated project is a git repository and that `git status`, `git diff`, and `git log --oneline -10` work. If git is unavailable or the project is not initialized, stop early with a precise environment/setup blocker instead of discovering it at commit time.

Required git flow:

- run `git status`
- run `git diff`
- run `git log --oneline -10`
- stage only the intended files for the generated feature and related `.features/` reports
- create a concise commit message proposal, such as `Add expenses feature`

Do not commit when verification is blocked, review is not approved, required reports are missing, the user has not explicitly approved the commit, or unrelated worktree changes make safe staging unclear. Never amend, force push, reset, or revert unrelated changes unless the user explicitly requests it.

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

Dream must not report final success without the required platform validation, smoke coverage, and contract review, or a documented blocker.

When the native SwiftUI iOS host is present, structural validation includes both `xcodebuild build` and `xcodebuild test` against the shared scheme so the packaged XCTest target stays healthy.

Verification reports should use explicit status buckets for each check: `passed`, `blocked by environment`, `not run`, and `not applicable`. Do not blur an unrun smoke flow into a passed build result.

Maestro smoke flows should be feature-owned under `maestro/features/<feature-name>/android.yaml` and `maestro/features/<feature-name>/ios.yaml`. Every user-visible generated or touched feature should have at least one Android flow and one iOS flow when the feature is supported on that platform. Do not rely on one global smoke file to cover multiple features. Maestro smoke flows should run only after the target emulator/simulator is open, the app is installed, and platform tooling has confirmed the app launches. Maestro validates user behavior after runtime launch is healthy; it should not be treated as the install step. Start every smoke flow with `clearState` before `launchApp` unless the contract explicitly tests persistence across app launches. Maestro smoke flows should prove user outcomes with stable selectors. For iOS single-line edits, prefer `tapOn`, `eraseText`, `inputText`, `pressKey: Enter`, then the save action when `hideKeyboard` is unreliable. For detail screens, prefer full rendered-row assertions such as `Occurred: .*` instead of standalone labels. If no Android device or emulator is attached, the iOS app is not installed, or Maestro `launchApp` fails on a physical device while `adb install` and `adb shell am start` work, record a precise environment/tooling blocker and try an emulator-backed smoke run when feasible.

## Repair Rules

When verification fails, Dream should use this OpenCode pattern:

1. `dream-general` identifies the failing module or runtime surface
2. `dream-explore` or `dream-context-scout` gathers the relevant context if needed
3. the relevant implementer (`dream-implementer`, `dream-implementer-android`, or `dream-implementer-ios`) loads the relevant graph rules for that layer
4. the implementer prefers minimal fixes over broad rewrites
5. the implementer preserves architecture boundaries while fixing the failure
6. `dream-tester` reruns verification

Dream should keep repairing until:

- the project is green
- a real environment or platform blocker is identified
- the requested behavior is impossible under current constraints
- a skip condition triggers (documented below)

## Iteration Tracking

`dream-general` must maintain an implicit iteration counter across repair rounds:

1. Before the first repair, record the initial failure count and set iteration = 1.
2. After each repair, delegate `dream-tester` to run verification and record: iteration number, failure count, list of failing tests or checks, and whether any new failures appeared.
3. Before starting the next repair, check all skip conditions against the tracked data.
4. When the loop ends (green or blocked), include the iteration history in `.features/verification-report.md`.

## Skip Conditions

To prevent infinite repair loops, `dream-general` must enforce these skip conditions during the repair cycle:

- **Max iterations exceeded (5)**: After 5 repair rounds, if verification is still not green, stop and report `blocked: max repair attempts reached` with a summary of what was tried and what failures remain.
- **No progress detected**: If the total failure count has not decreased after 2 consecutive repair rounds, stop and report `blocked: no progress after N rounds — same failures persist`.
- **Regression detected**: If a repair introduces new test failures (a previously passing test now fails), stop and report `blocked: regression detected — fix caused new failures`. Do not continue fixing on top of a regression.
- **Environment loop**: If the same environment blocker (e.g., no Android device, missing SDK) appears in consecutive verification rounds, stop immediately and report the blocker — do not retry the same failing command.

When a skip condition triggers, `dream-general` must:

1. stop repairing immediately
2. record the skip condition, iteration count, and failure history in `.features/verification-report.md`
3. report status as `blocked` with a clear reason string

## Context Management

The LLM context window is a finite resource. Each repair round adds tool calls, file reads, and analysis to the conversation. `dream-general` must actively manage context to prevent degradation before the iteration limit is reached.

### Fresh Subagent Per Round

Each repair round must delegate to a fresh implementer subagent invocation (`dream-implementer`, `dream-implementer-android`, or `dream-implementer-ios` depending on the failing layer). Do not accumulate multiple repair rounds in a single long-running conversation. The subagent reads the current state from `.features/` metadata files instead of relying on implicit conversation history.

### State Persistence Before Delegation

Before delegating to any subagent, `dream-general` must write a compact context summary to `.features/verification-report.md`. The summary must include:

- current iteration number and total iterations so far
- failures identified and their module locations
- past fix attempts and their outcomes (what worked, what didn't, what regressed)
- what the subagent is expected to do this round
- relevant architecture rules or graph references for the failing module

The subagent receives the iteration number and a pointer to the state file, not the full conversation history.

### Context Budget Awareness

`dream-general` must monitor for context pressure:

- If the conversation exceeds approximately 3 repair rounds, or earlier if tool-call volume is already high, write the full iteration state to `.features/`, then stop the current conversation and start a fresh one with a compact handoff prompt.
- A fresh-conversation handoff prompt must include: current iteration, what was tried, what still fails, and the next module to fix. It must not include the full tool-call history of previous rounds.

### Subagent Handoff Pattern

When launching a subagent, `dream-general` must provide:

1. the iteration number and round purpose
2. a reference to `.features/verification-report.md` for full context
3. the specific module or file to fix
4. the relevant architecture rules — not the entire conversation history

### General's Self-Context Management

After each repair round, `dream-general` must write a compact summary of its own state into `.features/verification-report.md`. This ensures that even if the conversation context degrades, the essential state survives in the file system and can be picked up by a fresh conversation or subagent.

## Template Self-Improvement

Each feature generation captures what worked, what didn't, and what should change
in a retrospective at `.features/<feature>/retrospective.md`. These retros
are the template's learning mechanism.

- Retros are written automatically after every feature (step 19 of Generation Workflow).
- Retros are never applied automatically. Application is user-driven.
- To apply unapplied retros, use the `dream-improve` command (`/dream-improve`).

When `dream-improve` runs, `dream-general` scans all unapplied retros, collects
their recommended improvements, and applies actionable changes to the template
(guidance, best-practices, anti-patterns, prompts, commands, starter code).
Each retro is then marked as applied with the date and a summary of changes.

## Stop Conditions

The current agent flow may stop only when one of these is true:

- requested work is implemented and verified
- a blocker is real, specific, and documented
- a skip condition triggered (max iterations, no progress, regression, or environment loop)
- the user changed or narrowed the task

## Required Outputs

For generation tasks, Dream should leave behind:

- `.features/app-contract.yaml`
- `.features/decisions.yaml`
- `.features/<feature>/contract.yaml`
- `.features/<feature>/ui-spec.md` (authored by `dream-designer`)
- `.features/<feature>/test-plan.md`
- `.features/<feature>/implementation-report.md`
- `.features/<feature>/retrospective.md`
- `.features/verification-report.md`
- `maestro/features/<feature-name>/android.yaml` (authored by `dream-tester`)
- `maestro/features/<feature-name>/ios.yaml` (authored by `dream-tester`)

Use `.opencode/guidance/templates/verification-report-template.md` when writing `.features/verification-report.md`.
Use `.opencode/guidance/templates/template-improvement-retrospective-template.md` when writing `.features/<feature>/retrospective.md`.

For review or repair tasks, the responsible OpenCode agents should update the relevant report files when they are part of the current flow.
