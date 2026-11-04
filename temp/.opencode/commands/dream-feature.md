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
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
### Contract & Implementation
- generate or update `specs/<feature-name>/contract.yaml`
- stop and inspect the contract before implementation; confirm entity fields, action names, state names, time behavior, storage choices, design expectations, and required tests are explicit
- do not let `dream-implementer` write feature code until the contract exists and is the source of truth
- if external research is needed for dependencies or APIs, delegate to `dream-search`
- before platform UI implementation, delegate to `dream-designer` to produce `specs/<feature>/ui-spec.md` from the feature contract
- update `specs/decisions.yaml` with any new architecture decisions, storage choices, or cross-feature reuse exceptions
- decompose implementation into phases; delegate each phase to the appropriate subagent:
  - **Phase 1 (domain + data)**: delegate to `dream-implementer` for `feature/<name>-domain` (entities, repository interfaces, domain tests), `feature/<name>-data` (data sources, entity mapping, repository impl, data tests), and `feature/<name>-api` with `NavKey` route types only (no Koin)
  - **Phase 2 (UI — parallel)**: launch two independent subagent calls simultaneously:
    - *Compose UI*: delegate to `dream-implementer-android` for `feature/<name>-impl` (screens, ViewModels, UI state/actions/events, Koin navigation entry installers, presentation tests)
    - *SwiftUI*: delegate to `dream-implementer-ios` for `iosApp/src` with native SwiftUI views (`Views/`), `@Observable` ViewModels (`ViewModels/`), Swift models (`Models/` if needed), bridge extension code (`Core/`)
  - **Phase 3 (wiring)**: delegate to `dream-implementer` to integrate feature modules into umbrella imports, DI registration, and navigation entries
- delegate to `dream-tester` for the verification ladder: unit tests, platform builds, device install/launch, log inspection; include `./kotlin build -m iosApp --platform=iosSimulatorArm64` and `xcrun simctl` install/launch
- repair failures until green, blocked, or a skip condition triggers (see Skip Conditions in dream-agent-operating-guide.md)
- update `specs/<feature-name>/implementation-report.md`
- write `specs/<feature-name>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`
- update `specs/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`
- write phase checkpoints to `specs/<feature>/checkpoints.md` after each phase completes
- run `git status`, `git diff`, and `git log --oneline -10`
- if verification is green, inspector review passes, and staging is safe, present the merge summary to the user and request explicit confirmation
- on confirm: squash merge feature branch to main, delete feature branch
- on deny: keep branch, document reason in report

Use `dream-implementer`, `dream-implementer-android`, and `dream-implementer-ios` for code changes (layer-appropriate) and `dream-inspector` for final review.

Do not commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing. Do not commit until the user explicitly approves the commit after Dream reports verification and review are green.
