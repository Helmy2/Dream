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
- `.features/app-contract.yaml`
- `.features/decisions.yaml`
- existing feature contracts if present

Then:
- run early preflights before implementation depends on the environment:
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
- verify Maestro is installed for smoke-flow validation, or record a precise Maestro environment blocker
- generate or update `.features/<feature-name>/contract.yaml`
- stop and inspect the contract before implementation; confirm entity fields, action names, state names, time behavior, storage choices, design expectations, and required tests are explicit
- do not let `dream-implementer` write feature code until the contract exists and is the source of truth
- if external research is needed for dependencies or APIs, delegate to `dream-search`
- before platform UI implementation, delegate to `dream-designer` to produce `.features/<feature>/ui-spec.md` from the feature contract
- update `.features/decisions.yaml` with any new architecture decisions, storage choices, or cross-feature reuse exceptions
- decompose implementation into phases; delegate each phase to the appropriate subagent:
  - **Phase 1 (domain + data)**: delegate to `dream-implementer` for `feature/<name>/domain` (entities, repository interfaces, domain tests), `feature/<name>/data` (data sources, entity mapping, repository impl, data tests), and `feature/<name>/api` with `NavKey` route types only (no Koin)
  - **Phase 2 (UI — parallel)**: launch two independent subagent calls simultaneously:
    - *Compose UI*: delegate to `dream-implementer-android` for `feature/<name>/impl` (screens, ViewModels, UI state/actions/events, Koin navigation entry installers, presentation tests)
    - *SwiftUI* (only when `iosUiStrategy` is `swiftui`): delegate to `dream-implementer-ios` for `iosApp/` with native SwiftUI views (`Views/`), `@Observable` ViewModels (`ViewModels/`), Swift models (`Models/` if needed), bridge extension code (`Core/`), and XCTest coverage (`iosAppTests/`)
  - **Phase 3 (wiring)**: delegate to `dream-implementer` to integrate feature modules into umbrella imports, DI registration, and navigation entries
- delegate to `dream-tester` for the verification ladder: unit tests, platform builds, device install/launch, log inspection, Maestro flows; when `iosUiStrategy` is `swiftui`, include `xcodebuild build` and `xcodebuild test`
- repair failures until green, blocked, or a skip condition triggers (see Skip Conditions in dream-agent-operating-guide.md)
- update `.features/<feature-name>/implementation-report.md`
- write `.features/<feature-name>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`
- update `.features/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`
- run `git status`, `git diff`, and `git log --oneline -10`
- if verification is green, inspector review passes, and staging is safe, present the commit summary to the user and request explicit confirmation before staging and committing intended feature files

Use `dream-implementer`, `dream-implementer-android`, and `dream-implementer-ios` for code changes (layer-appropriate) and `dream-inspector` for final review.

Do not commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing. Do not commit until the user explicitly approves the commit after Dream reports verification and review are green.
