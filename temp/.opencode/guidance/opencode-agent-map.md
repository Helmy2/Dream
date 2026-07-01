# Dream OpenCode Agent Map

## Purpose

Defines which OpenCode agent owns each Dream workflow stage.

## Available OpenCode Agents

### Orchestrator

- `dream-general`: Primary orchestrator — interprets requests, breaks work into phases, delegates to subagents, manages context, and reports final status.

### Read-Only Subagents

- `dream-discovery`: Fast codebase discovery and focused context packs — locates modules, files, symbols, and builds context for tasks.
- `dream-search`: External technical research — library docs, API references, dependency compatibility, error troubleshooting.

### Spec-Generation Subagent

- `dream-designer`: Translates feature contracts into cross-platform UI/UX specs. Writes spec files, not code.

### Code-Writing Subagents

- `dream-implementer`: KMP business foundation — domain, data, api, wiring. No UI.
- `dream-implementer-android`: Jetpack Compose presentation layer — screens, ViewModels, Koin navigation.
- `dream-implementer-ios`: Native SwiftUI layer — views, ViewModels, bridge, XCTest.

### Verification Subagent

- `dream-tester`: Full verification ladder — tests, builds, device, Maestro.

### Review Subagent

- `dream-inspector`: Final QA and architecture review gate.

## Default Ownership

### `dream-general`

Owns: workflow orchestration, contracts, delegation, verification coordination.

Should do:
- Interpret user request
- Launch appropriate subagent per phase
- Ensure Dream doesn't report success before verification is green

Should not: edit implementation files, run verification directly, skip inspector for substantial work.

### `dream-discovery`

Owns: fast codebase discovery and focused context packs.

Modes:
- **Quick**: Find specific files, symbols, patterns. Return paths only.
- **Deep**: Build focused context pack for a task. Return paths + summaries + key snippets.

Use when: locating something, building context before implementation or repair.

### `dream-search`

Owns: external technical research.

Use when: unfamiliar library, cryptic error, API research needed.

### `dream-designer`

Owns: UI/UX specification generation.

Use when: feature contract exists and UI implementation is about to start.

Output: `.features/<feature>/ui-spec.md`

### `dream-implementer`

Owns: KMP business layer — domain, data, api, wiring.

Use when: creating or repairing KMP business layer.

Must preserve: `Presentation -> Domain <- Data`, pure Kotlin domain.

### `dream-implementer-android`

Owns: Jetpack Compose presentation layer.

Use when: creating or updating Compose screens, ViewModels.

Must: read UI spec, use Dream design system, implement all states.

### `dream-implementer-ios`

Owns: native iOS SwiftUI presentation layer.

Use when: creating or updating SwiftUI views, bridge code.

Must: read UI spec, load iOS best-practice files (35-39), run sync-pbxproj.rb.

### `dream-tester`

Owns: full verification ladder.

Use when: verification needed after implementation or repair.

Must: run preflights, record status buckets, inspect platform logs.

### `dream-inspector`

Owns: architecture and behavior review.

Use when: substantial feature generated or repair claims green status.

Output: `APPROVED` or itemized defect list.

## Default Workflow Map

### Project Creation

1. `dream-general` interprets the request.
2. `dream-discovery` gathers relevant context if needed.
3. `dream-general` generates contracts.
4. `dream-implementer` instantiates template.
5. `dream-implementer` removes `feature/sample` after first real feature.

### Feature Generation

1. `dream-general` selects feature to generate.
2. `dream-discovery` gathers context (deep mode).
3. `dream-general` produces feature contract.
4. `dream-search` if external research needed.
5. `dream-designer` produces UI spec.
6. `dream-implementer` generates KMP business layer.
7. `dream-implementer-android` generates Compose UI layer.
8. `dream-implementer-ios` generates SwiftUI layer.
9. `dream-tester` runs verification ladder.
10. `dream-inspector` reviews substantial work.

### Verification And Repair

1. `dream-general` selects failing surface.
2. `dream-discovery` narrows failure surface if needed.
3. Relevant implementer applies minimal fix.
4. `dream-tester` reruns verification.
5. `dream-inspector` final review gate.

## Handoff Rules

- `dream-general` → `dream-discovery` for discovery (not broad search itself)
- `dream-general` → `dream-search` for external research
- `dream-general` → `dream-designer` for UI spec
- `dream-general` → `dream-implementer` for KMP business layer
- `dream-general` → `dream-implementer-android` for Compose UI
- `dream-general` → `dream-implementer-ios` for SwiftUI
- `dream-general` → `dream-tester` for verification
- `dream-general` → `dream-inspector` for review
