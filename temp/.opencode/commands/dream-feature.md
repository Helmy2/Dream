---
description: Generate or update a Dream feature
agent: dream-general
---

Use Dream's OpenCode-first workflow to add or update a feature in the current project.

Treat `$ARGUMENTS` as the feature request.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- relevant `.opencode/knowledge-graph/best-practices/*.md`
- `.opencode/guidance/new-feature-checklist.md`
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- existing feature contracts if present

Then:
- run early preflights before implementation depends on the environment:
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
- verify Maestro is installed for smoke-flow validation, or record a precise Maestro environment blocker
- generate or update `.mvp/features/<feature-name>/contract.yaml`
- stop and inspect the contract before implementation; confirm entity fields, action names, state names, time behavior, storage choices, design expectations, and required tests are explicit
- do not let `dream-implementer` write feature code until the contract exists and is the source of truth
- decompose implementation into phases; delegate each phase to a separate `dream-implementer` call:
  - **Phase 1 (domain + data)**: implement `feature/<name>/domain` (entities, repository interfaces, domain tests) and `feature/<name>/data` (data sources, entity mapping, repository impl, data tests). Also create `feature/<name>/api` with `NavKey` route types only (no Koin).
  - **Phase 2 (UI — parallel)**: launch two independent subagent calls simultaneously:
    - *Compose UI*: implement `feature/<name>/impl` (screens, ViewModels, UI state/actions/events, Koin navigation entry installers, shared strings, presentation tests)
    - *SwiftUI* (only when `iosUiStrategy` is `swiftui`): update `iosApp/` with native SwiftUI views (`Views/`), `@Observable` ViewModels (`ViewModels/`), Swift models (`Models/` if needed), bridge extension code (`Core/`), and XCTest coverage (`iosAppTests/`)
  - **Phase 3 (wiring)**: integrate feature modules into umbrella imports, DI registration, and navigation entries
- run verification; when `iosUiStrategy` is `swiftui`, run `xcodebuild build` and `xcodebuild test` as part of verification
- repair failures until green, blocked, or a skip condition triggers (see Skip Conditions in dream-agent-operating-guide.md)
- update `.mvp/features/<feature-name>/implementation-report.md`
- write `.mvp/features/<feature-name>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`
- update `.mvp/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`
- run `git status`, `git diff`, and `git log --oneline -10`
- if verification is green, inspector review passes, and staging is safe, stage only intended feature files and create a concise feature commit automatically

Use `dream-implementer` for code changes and `dream-inspector` for final review.

Do not auto-commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing.
