# Dream OpenCode Agent Map

## Purpose

Defines which OpenCode agent owns each Dream workflow stage.

## Available OpenCode Agents

### Orchestrator

- `dream-general`: Primary orchestrator — interprets requests, breaks work into phases, delegates to subagents, manages context, and reports final status.

### Read-Only Subagents

- `dream-research`: Fast codebase discovery and external research — locates modules, files, symbols, builds context packs, and fetches external documentation/APIs. Modes: `codebase` (quick/deep), `external` (docs/APIs/troubleshooting), `combined`.

### Spec-Generation Subagent

- `dream-designer`: Translates feature contracts into cross-platform UI/UX specs. Writes spec files, not code.

### Code-Writing Subagents

- `dream-implementer`: KMP business foundation — domain, data, api, wiring. No UI.
- `dream-implementer-ui`: Cross-platform UI implementation — Jetpack Compose (Android) and SwiftUI (iOS). Receives `platform` parameter: `android`, `ios`, or `both`.

### Verification Subagent

- `dream-tester`: Full verification ladder — tests, builds, device, Maestro.

### Review Subagent

- `dream-inspector`: Final QA and architecture review gate.

## Default Ownership

### `dream-general`

Owns: workflow orchestration, contracts, **plan generation**, delegation, verification coordination.

Should do:
- Interpret user request
- **Create the plan (`.features/<feature>/plan.md`) and present it to the user during Phase 0**
- **Ask for questions, edits, or confirmation before proceeding**
- Launch appropriate subagent per phase
- Ensure Dream doesn't report success before verification is green

Should not: edit implementation files, run verification directly, skip inspector for substantial work, **skip Phase 0 (Plan & User Review)**.

### `dream-research`

Owns: fast codebase discovery and external research.

Modes:
- **codebase quick**: Find specific files, symbols, patterns. Return paths only.
- **codebase deep**: Build focused context pack for a task. Return paths + summaries + key snippets.
- **external**: Fetch external documentation, API references, troubleshoot errors.
- **combined**: Both codebase and external research in one pass.

Use when: locating something, building context before implementation or repair, researching external libraries or APIs.

### `dream-designer`

Owns: UI/UX specification generation.

Use when: feature contract exists and UI implementation is about to start.

Output: `.features/<feature>/ui-spec.md`

### `dream-implementer`

Owns: KMP business layer — domain, data, api, wiring.

Use when: creating or repairing KMP business layer.

Must preserve: `Presentation -> Domain <- Data`, pure Kotlin domain.

### `dream-implementer-ui`

Owns: cross-platform UI implementation — Jetpack Compose (Android) and SwiftUI (iOS).

Use when: creating or updating UI screens, ViewModels, bridge code.

Receives `platform` parameter: `android`, `ios`, or `both`.

Must: read UI spec, use Dream design system, implement all states, load platform-specific best practices.

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
2. `dream-research` gathers relevant context if needed.
3. **Phase 0: `dream-general` produces `.features/<feature>/plan.md`, presents it to the user, and gets explicit confirmation before proceeding.**
4. `dream-general` generates contracts.
5. `dream-implementer` instantiates template.
6. `dream-implementer` removes `feature/sample` after first real feature.

### Feature Generation

1. `dream-general` selects feature to generate.
2. `dream-research` gathers context (deep mode).
3. **Phase 0: `dream-general` produces `.features/<feature>/plan.md`, presents it to the user, and gets explicit confirmation.**
4. `dream-general` produces feature contract.
5. `dream-research` if external research needed.
6. `dream-designer` produces UI spec.
7. `dream-implementer` generates KMP business layer.
8. `dream-implementer-ui` generates UI layer (platform: both).
9. `dream-tester` runs verification ladder.
10. `dream-inspector` reviews substantial work.

### UI Fix

1. `dream-general` interprets the UI issue description.
2. **Phase 0: `dream-general` produces a fix plan, presents it to the user, and gets explicit confirmation.**
3. `dream-tester` captures screenshot + element tree via mobile-mcp (Phase 1: Capture).
4. `dream-research` identifies affected UI source files (Phase 2: Diagnose).
5. `dream-implementer-ui` applies fix (Phase 3: Fix).
6. `dream-tester` re-captures screenshot + element tree for before/after comparison (Phase 4: Verify).

### Bug Fix

1. `dream-general` interprets the bug description or crash log.
2. **Phase 0: `dream-general` produces a fix plan with suspected root-cause layer, gets user confirmation.**
3. `dream-tester` gathers diagnostics (crash logs, logcat, build output) + `dream-research` for error research (Phase 1: Diagnostics).
4. `dream-research` narrows to affected files; `dream-general` identifies failing layer (Phase 2: Root Cause).
5. Layer-appropriate implementer applies fix (Phase 3: Fix).
6. `dream-tester` runs targeted re-verification per fixed layer (Phase 4: Verify).

### Verification And Repair

1. `dream-general` selects failing surface.
2. `dream-research` narrows failure surface if needed.
3. Relevant implementer applies minimal fix.
4. `dream-tester` reruns verification.
5. `dream-inspector` final review gate.

### Plan Review (Phase 0 in all workflows)

Introduced before every generation or creation workflow:
1. `dream-general` gathers context (project, existing features, rules).
2. `dream-general` produces `.features/<feature>/plan.md` using the plan template.
3. `dream-general` presents the plan to the user in the conversation.
4. `dream-general` asks for questions, edits, or confirmation.
5. On **Confirm**: proceed to next phase. On **Edit**: update plan, re-present. On **Deny**: stop and document.

### Inspector Repair Loop

When `dream-inspector` returns an itemized defect list:
1. `dream-general` presents defects to user with severity: **critical** / **warning** / **info**.
2. User decides: **repair? accept with defects? deny?**
3. On **repair**: delegate to appropriate implementer, re-run inspector.
4. On **accept**: document defects in report, proceed to merge.
5. On **deny**: stop workflow, document reason.
6. Max 2 inspector repair rounds — if still failing after 2 rounds, escalate to user.

## Handoff Rules

- `dream-general` → `dream-research` for discovery (not broad search itself)
- `dream-general` → `dream-research` for external research
- `dream-general` → `dream-designer` for UI spec
- `dream-general` → `dream-implementer` for KMP business layer
- `dream-general` → `dream-implementer-ui` for UI layer (platform: android, ios, or both)
- `dream-general` → `dream-tester` for verification
- `dream-general` → `dream-inspector` for review
- `dream-general` → `dream-implementer-ui` for UI fix (platform-specific)
- `dream-general` → `dream-implementer` / `dream-implementer-ui` for bug fix (layer-appropriate)
- `dream-general` → `dream-implementer` / `dream-implementer-ui` for inspector repair (layer-appropriate)

## Phase 0 Handoff

Phase 0 is owned entirely by `dream-general`. It does not delegate to subagents — the plan is produced by `dream-general` itself, drawing from `dream-discovery` context if needed. The user interaction (present, question, confirm) happens directly in the conversation.
