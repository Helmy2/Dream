# Plan Template

Use this template when generating `.features/<feature>/plan.md` during Phase 0 (Plan & User Review).

**Note:** This template applies to `/dream-create` and `/dream-feature` workflows (COMPLEX complexity). For trivial and simple workflows, see `.opencode/guidance/reference/complexity-routing.md`.

## Phase 0 Depth

Choose the appropriate depth based on change scope:

### `minimal` (Trivial: 1-3 files, <50 lines, no new modules)
- 2-line plan in conversation only
- No plan.md file created
- Example: "Fix typo in strings.xml, update 1 Compose preview"

### `standard` (Simple: 1 feature, <5 screens, no new shared modules)
- Brief plan.md with only: Goal, Modules to Create/Modify, Screens, Phases
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

## Modules

### Create
- `feature/<name>/domain` — entities, repository interfaces, use cases
- `feature/<name>/data` — data sources, DTOs, mappers, repository implementations
- `feature/<name>/api` — NavKey route types (no Koin)
- `feature/<name>/impl` — Compose screens, ViewModels, UI state/events, Koin module

### Modify
- `shared/umbrella-core` — register domain/data DI modules
- `shared/umbrella-ui` — register UI DI modules and navigation entries
- `shared/core/strings` — add user-visible string resources
- `androidApp` / `iosApp-swiftui` — if host wiring changes are needed

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

## Phases

| Phase | Scope | Agent |
|-------|-------|-------|
| Phase 1 | External research (if needed) | dream-research |
| Phase 1.5 | UI spec generation | dream-designer |
| Phase 2 | Domain, data, api modules + tests | dream-implementer |
| Phase 3 | Compose UI + iOS SwiftUI (parallel) | dream-implementer-ui (platform: both) |
| Phase 4 | Wiring: umbrellas, DI, navigation | dream-implementer |
| Phase 5 | Verification ladder | dream-tester |
| Final | Inspector review | dream-inspector |
```

## Instructions

1. Fill in every section. Mark unknowns as `TBD` rather than leaving sections blank.
2. Keep the goal concise — one sentence, no technical detail.
3. List every module path that will be touched.
4. Each screen must list all four states (Loading, Empty, Content, Error).
5. Architecture decisions should be concrete — if storage is Room, say Room; if DataStore, say DataStore.
6. Dependencies section is for new dependencies only; omit if none.
7. After writing, present to user and ask: **questions? edits? confirm? deny?**
