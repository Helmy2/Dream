---
description: Generate or update a Dream feature
agent: dream-general
---

Use Dream's OpenCode-first workflow to add or update a feature in the current project.

Treat `$ARGUMENTS` as the feature request.

Read:
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- relevant `.opencode/knowledge-graph/best-practices/*.md`
- `.opencode/guidance/new-feature-checklist.md`
- `specs/app-contract.yaml`
- `specs/decisions.yaml`
- existing feature contracts if present

Then:

### Phase 0: Plan & User Review (mandatory first step)

- gather context using `dream-discovery` (deep mode) — understand existing features, project structure, applicable rules
- choose an archetype from `.opencode/archetypes/README.md` (read each `archetype.yaml`) and record it in the plan's Archetype section, or record `none` with the reason
- produce `specs/<feature-name>/plan.md` using the template at `.opencode/guidance/templates/plan-template.md`
  - define what modules will be created/modified, screens with states, architecture decisions, dependencies, and phases
  - include priority field: `hotfix`, `high`, `normal`, or `low`
- present the plan to the user in the conversation
- explicitly ask: **questions? edits? confirm? deny?**
- on **Confirm**: create feature branch `dream/feature/<feature-name>` from main and proceed
- on **Edit**: update `specs/<feature-name>/plan.md`, re-present, repeat
- on **Deny**: stop and document in `specs/<feature-name>/report.md`
- do not proceed to implementation, research, or design until the plan is confirmed

### Preflights
- run early preflights before implementation depends on the environment:
- run `./.opencode/scripts/dream-preflight.sh` from the project root when it exists
- verify the project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
### Contract & Implementation
- build the feature **one slice at a time** using the Slice Loop in `.opencode/agents/dream-general.md`, in the order of the plan's Slices table; do not start slice N+1 until slice N is green and committed
- `contract.yaml` (`specs/<feature-name>/contract.yaml`) grows with the slices: amend it for each slice, record changes to earlier-slice definitions in its change notes, and confirm the slice's entity fields, action names, state names, time behavior, storage choices, design expectations, and required tests are explicit before implementation
- do not let `dream-implementer` write slice code until the contract covers that slice
- update `specs/decisions.yaml` with any new architecture decisions, storage choices, or cross-feature reuse exceptions
- for each slice:
  - if the slice needs a new dependency or API, delegate to `dream-research`
  - for new or changed screens, delegate to `dream-designer` to update `specs/<feature>/ui-spec.md`
  - **domain + data + api**: delegate to `dream-implementer` for the slice's changes in `feature/<name>-domain`, `feature/<name>-data` and `feature/<name>-api` (`NavKey` route types only, no Koin), with tests
  - **UI (parallel)**: launch two independent subagent calls simultaneously:
    - *Compose UI*: `dream-implementer-ui` (platform: android) for `feature/<name>-impl` (screens, ViewModels, UI state/actions/events, Koin navigation entry installers, presentation tests)
    - *SwiftUI*: `dream-implementer-ui` (platform: ios) for `iosApp/src` (`Views/`, `ViewModels/`, `Models/`, `<Feature>/Bridge/`)
  - **wiring**: `dream-implementer` for umbrella imports, DI registration, and navigation entries the slice needs
  - iterate with focused commands (`./kotlin test -m <module> --platform=...`, `verify-feature.sh <feature-name> --quick`); run the full gate only when those pass
  - **gate**: run `.opencode/scripts/verify-feature.sh <feature-name>` (full: Android and iOS); if the slice changed UI, also have `dream-tester` install and launch on Android and on an iOS simulator (`xcrun simctl`) and inspect logs. Android-only is not enough
  - repair until the slice is green or a skip condition triggers (see `.opencode/guidance/reference/skip-conditions.md`; the iteration counter is per slice)
  - commit the green slice on the feature branch, update its status in `plan.md`, append to `specs/<feature>/checkpoints.md`, and continue to the next slice without asking the user
- stop and ask the user only on failure (skip condition, repeated environment blocker, a slice that cannot be made green)
- after the last slice: run `dream-inspector` over the whole feature
- update `specs/<feature-name>/implementation-report.md`
- write `specs/<feature-name>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`
- update `specs/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`
- run `git status`, `git diff`, and `git log --oneline -10`
- if verification is green, inspector review passes, and staging is safe, present the merge summary (list the slices and their commits) to the user and request explicit confirmation
- on confirm: squash merge feature branch to main, delete feature branch
- on deny: keep branch, document reason in report

Use `dream-implementer`, `dream-implementer-ui` (platform: android), and `dream-implementer-ui` (platform: ios) for code changes (layer-appropriate) and `dream-inspector` for final review.

Do not commit blocked, partial, or unsafe worktree states, and never commit to main. A slice is committed on the feature branch only after its full gate is green; if unrelated changes make safe staging unclear, stop and report the blocker instead of committing. Merging the feature branch to main requires the user's explicit approval after Dream reports verification and review are green.
