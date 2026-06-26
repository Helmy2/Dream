---
description: Create a Dream MVP project
agent: dream-general
---

Use Dream's OpenCode-first workflow to create a new MVP from the user's request.

Treat `$ARGUMENTS` as the product request.

Do not require the user to precreate app contracts or feature contracts.
Dream must derive those artifacts from the prompt as part of the workflow.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `AGENTS.md`
- `.opencode/knowledge-graph/PRODUCT.md`
- relevant `.opencode/knowledge-graph/best-practices/*.md`

Then:
- run early preflights before implementation depends on the environment:
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the destination project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
- verify Maestro is installed for smoke-flow validation, or record a precise Maestro environment blocker
- generate or update `.features/app-contract.yaml`
- generate or update `.features/decisions.yaml`
- instantiate the appropriate Dream template
- generate the first feature contract before any feature code is written
- inspect the feature contract for entity fields, actions, states, time behavior, storage choices, and required tests
- only then hand implementation to `dream-implementer` using the phased approach:
  - **Phase 1 (domain + data)**: implement entities, repository interfaces, data mappings, domain/data tests. Create `feature/<name>/api` with `NavKey` route types only.
  - **Phase 2 (UI — parallel)**: launch Compose UI (`feature/<name>/impl` with screens, ViewModels, shared strings, presentation tests) and SwiftUI (views, ViewModels, models, bridge, XCTest when `iosUiStrategy` is `swiftui`) as independent subagent calls.
  - **Phase 3 (wiring)**: umbrella imports, DI registration, navigation entries.
- run verification; when `iosUiStrategy` is `swiftui`, include `xcodebuild build` and `xcodebuild test`
- repair failures until green, blocked, or a skip condition triggers (see Skip Conditions in dream-agent-operating-guide.md)
- write `.features/<feature>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`
- run `git status`, `git diff`, and `git log --oneline -10`
- when verification is green, inspector review passes, and only intended files are staged, present the commit summary to the user and request explicit confirmation before committing
- finish with `.features/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`

Use `dream-implementer` for code changes and `dream-inspector` as the final QA gate for substantial work.

Do not commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing. Do not commit until the user explicitly approves the commit after Dream reports verification and review are green.
