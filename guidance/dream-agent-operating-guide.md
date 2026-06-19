# Dream OpenCode Operating Guide

## Purpose

This guide defines the default working behavior for OpenCode agents generating, reviewing, verifying, or repairing Dream projects.

Use this guide together with:

- `guidance/opencode-agent-map.md`
- `knowledge-graph/PRODUCT.md`
- `knowledge-graph/README.md`
- relevant files in `knowledge-graph/best-practices/`
- `AI_MVP_BUILDER_PLAN.md`
- the selected template README and source files

## Required Read Order

Unless a narrower task makes some steps irrelevant, use this read order:

1. `knowledge-graph/PRODUCT.md`
2. `guidance/opencode-agent-map.md`
3. `AI_MVP_BUILDER_PLAN.md`
4. `knowledge-graph/README.md`
5. relevant `knowledge-graph/best-practices/*.md` files for the current concern
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
- introduce cross-feature presentation coupling by default

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
 route time-sensitive behavior through shared time abstractions
 keep dependency assembly in stable composition roots
 not report validated success until verification is green, contract behavior is implemented, required tests exist, and review status is documented
- feature name
- goal
- entities
- actions
 map each contract action, state, and required test to code and tests explicitly
 avoid creating repositories, use cases, or ViewModels inside recomposing composables
 inject `TimeProvider` for time-sensitive behavior instead of reading system time directly
- API base path
- endpoints
- request and response shapes

 Green commands alone are not sufficient stop conditions.
 Dream should also confirm contract-to-code parity, required test coverage, and final review status before reporting validated success.

- create or update `feature/<name>/domain`
- create or update `feature/<name>/data`
- keep domain-to-UI mapping in presentation
- keep feature screens wired into the centralized shell using callbacks or controlled routing points

If shared behavior appears across features, move it into the appropriate shared module instead of duplicating it.

## Verification Rules

At minimum, local-only Dream projects should pass the baseline build and test checks:

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
```

For modular projects, `allTests` should aggregate shared and feature tests.

Final validation should then exercise the real product targets:

1. Android CLI (`https://developer.android.com/tools/agents/android-cli`) for Android environment checks, emulator/device setup, APK install, launch, layout/screenshot inspection, and log capture.
2. `xcodebuild` plus `xcrun simctl` for iOS simulator build, install, launch, logs, and crash capture.
3. Maestro Android smoke flows for user-visible behavior after Android launches cleanly.
4. Maestro iOS smoke flows where reliable after iOS launches cleanly.
5. `./gradlew :desktopApp:compileKotlinJvm` plus `./gradlew :desktopApp:hotRunJvm` when `desktop` is included in the app contract.

Desktop is a supported target when the app contract includes it, but Desktop validation does not replace Android/iOS readiness checks. Dream must not report final success without the required platform validation, smoke coverage, and contract review, or a documented blocker.

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

For review or repair tasks, the responsible OpenCode agents should update the relevant report files when they are part of the current flow.
