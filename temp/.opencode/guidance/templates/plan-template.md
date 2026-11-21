# Plan Template

Use this template when generating `specs/<feature>/plan.md` during Phase 0 (Plan & User Review).

**Note:** This template applies to `/dream-create` and `/dream-feature` workflows (COMPLEX complexity). For trivial and simple workflows, see `.opencode/guidance/reference/complexity-routing.md`.

## Phase 0 Depth

Choose the appropriate depth based on change scope:

### `minimal` (Trivial: 1-3 files, <50 lines, no new modules)
- 2-line plan in conversation only
- No plan.md file created
- Example: "Fix typo in strings.xml, update 1 Compose preview"

### `standard` (Simple: 1 feature, <5 screens, no new shared modules)
- Brief plan.md with only: Goal, Modules to Create/Modify, Screens, Slices
- Skip: Architecture Decisions, Dependencies sections
- Example: "Add expense list screen with 3 states"

### `full` (Complex: complete feature requiring domain/data/api/impl, tests, both platforms)
- Full plan.md with all sections below
- Example: "Add expense tracker with list, detail, add screens — domain/data/api/impl, Room persistence, tests, both platforms"

## Auto-Detection Logic

`dream-general` evaluates change scope and selects depth:
- **Command-triggered**: If `/dream-create` or `/dream-feature` is invoked, always use `full` depth regardless of scope
- Count files to change
- Count new screens
- Check if new shared modules needed
- Check if complete feature (domain/data/api/impl + tests + both platforms)
- Apply auto-detection, but user can override

## Structure (for `standard` and `full` depth)

```markdown
# Plan: <Feature Name>

## Priority

`hotfix` | `high` | `normal` | `low`

- `hotfix`: Skip Phase 0, go straight to diagnostics; plan after root cause identified
- `high`: Full workflow, expedited verification
- `normal`: Full workflow (default)
- `low`: May skip on first pass (document in verification report)

## Goal

One-line summary of what this feature does and why it exists.

## Archetype

`<name>` | `none`

- **Why it fits / why none fits**: <one or two sentences, referring to the archetype's `when_to_use` / `when_not_to_use`>
- **Deviations**: <what will differ from the archetype>
- **Demo content to drop**: <seed data, demo enums, demo strings>

## Modules

### Create
- `feature/<name>-domain` — entities, repository interfaces, use cases
- `feature/<name>-data` — data sources, DTOs, mappers, repository implementations
- `feature/<name>-api` — NavKey route types (no Koin)
- `feature/<name>-impl` — Compose screens, ViewModels, UI state/events, Koin module

### Modify
- `shared/umbrella-core` — register domain/data DI modules
- `shared/umbrella-ui` — register UI DI modules and navigation entries
- `shared/core-strings` — add user-visible string resources
- `androidApp` / `iosApp` — if host wiring changes are needed

## Screens

| Screen | Purpose | States |
|--------|---------|--------|
| ScreenName | What the user does here | Loading / Empty / Content / Error |

## Architecture Decisions

- **Data flow**: <one-way / repository-based / event-driven>
- **Storage**: <Room / DataStore / none / in-memory>
- **Time provider**: <true / false> — describe time-sensitive behavior
- **Backend**: <enabled / disabled>
- **Cross-feature reuse**: <none / describe any shared modules>

## Dependencies

- <library or service> — <reason>
- <library or service> — <reason>

## Slices

Ordered, small, vertical increments. Each slice is built, verified on Android and iOS, and committed before the next starts. `dream-general` decides the slices; the user sees them here and may edit them during plan review.

| # | Slice | Adds | Acceptance (observable) | Status |
|---|-------|------|-------------------------|--------|
| 1 | Foundation: <smallest end-to-end path> | all four modules, DI/navigation wiring, SwiftUI + bridge, tests, Maestro flow | <what the user can do, on both platforms> | pending |
| 2 | <next increment> | <fields/screens/behavior> | <what the user can do> | pending |

Status values: `pending`, `in-progress`, `done` (with commit hash), `blocked` (with reason).

## Per-Slice Loop

Every slice runs the same steps (see `dream-general`, "Slice Loop"):

| Step | Scope | Agent |
|------|-------|-------|
| a | Contract amendment for this slice (record changes, never silently drift) | dream-general |
| b | External research, only if the slice needs a new dependency or API | dream-research |
| c | UI spec for new or changed screens | dream-designer |
| d | Domain, data, api changes + tests | dream-implementer |
| e | Compose UI + iOS SwiftUI (parallel) | dream-implementer-ui (platform: android / ios) |
| f | Wiring changes: umbrellas, DI, navigation | dream-implementer |
| g | Gate: `verify-feature.sh <name>` (full, Android and iOS), then device install/launch on both | dream-tester |
| h | Commit on the feature branch, update slice status and `checkpoints.md` | dream-general |

After the last slice: inspector review (`dream-inspector`) over the whole feature, then the merge summary.
```

## Instructions

1. Fill in every section. Mark unknowns as `TBD` rather than leaving sections blank.
2. Keep the goal concise — one sentence, no technical detail.
3. List every module path that will be touched.
4. Each screen must list all four states (Loading, Empty, Content, Error).
5. Architecture decisions should be concrete — if storage is Room, say Room; if DataStore, say DataStore.
6. Dependencies section is for new dependencies only; omit if none.
7. Choose an archetype from `.opencode/archetypes/README.md`, or write `none` with the reason.
8. Slice the feature (`standard` and `full` depth). Rules:
   - Slice vertically (a path the user can run on both platforms), never by layer. "All domain, then all data, then all UI" is not slicing.
   - Slice 1 is the foundation: the smallest end-to-end path. It creates all four modules, tests, SwiftUI views, bridge, and the Maestro flow, because the gate requires them from the first run. Give it the full data model shape so later slices extend it rather than reshape it.
   - Each later slice adds one cohesive increment (a field group, a screen, a behavior) and ends with something observable.
   - Aim for 3-6 slices. A feature that is really one increment gets one slice; do not pad.
   - Every slice has an acceptance line a person could check by hand.
9. After writing, present to user and ask: **questions? edits? confirm? deny?**
