---
description: Create a Dream MVP project
agent: dream-general
---

Use Dream's OpenCode-first workflow to create a new MVP from the user's request.

Treat `$ARGUMENTS` as the product request.

Do not require the user to precreate app contracts or feature contracts.
Dream must derive those artifacts from the prompt as part of the workflow.

Read:
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `AGENTS.md`
- relevant `.opencode/knowledge-graph/best-practices/*.md`

Then:

### Phase 0: Plan & User Review (mandatory first step)

- gather context using `dream-discovery` (deep mode) — understand project structure, existing features, applicable rules
- produce `specs/<feature>/plan.md` using the template at `.opencode/guidance/templates/plan-template.md`
  - define feature goal, modules to create/modify, screens with states, architecture decisions, dependencies, phases
  - include priority field: `hotfix`, `high`, `normal`, or `low`
- present the plan to the user in the conversation
- explicitly ask: **questions? edits? confirm? deny?**
- on **Confirm**: create feature branch `dream/feature/<feature-name>` from main and proceed
- on **Edit**: update `specs/<feature>/plan.md`, re-present, repeat
- on **Deny**: stop and document in `specs/<feature>/report.md`
- do not proceed to implementation, research, or design until the plan is confirmed

### Preflights
- run early preflights before implementation depends on the environment:
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the destination project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
### Contract & Implementation
- generate or update `specs/app-contract.yaml` (do not overwrite if it already exists with content)
- generate or update `specs/decisions.yaml`
- instantiate the appropriate Dream template (this command handles its own initialization — `/dream-init` is a prerequisite for `/dream-feature` and `/dream-verify` only)
- generate the first feature contract before any feature code is written
- inspect the feature contract for entity fields, actions, states, time behavior, storage choices, and required tests
- if external research is needed for dependencies or APIs, delegate to `dream-search`
- before platform UI implementation, delegate to `dream-designer` to produce `specs/<feature>/ui-spec.md` from the feature contract
- only then hand implementation to the appropriate subagents using the phased approach:
  - **Phase 1 (domain + data)**: delegate to `dream-implementer` for entities, repository interfaces, data mappings, domain/data tests, and `feature/<name>-api` with `NavKey` route types only.
  - **Phase 2 (UI — parallel)**: launch Compose UI (delegate to `dream-implementer-android` for `feature/<name>-impl` with screens, ViewModels, presentation tests) and SwiftUI (delegate to `dream-implementer-ios` for views, ViewModels, models, bridge) as independent subagent calls.
  - **Phase 3 (wiring)**: delegate to `dream-implementer` for umbrella imports, DI registration, navigation entries.
- delegate to `dream-tester` for the verification ladder: unit tests, platform builds, device install/launch, log inspection; include `./kotlin build -m iosApp --platform=iosSimulatorArm64` and `xcrun simctl` install/launch
- repair failures until green, blocked, or a skip condition triggers (see Skip Conditions in dream-agent-operating-guide.md)
- update `specs/<feature-name>/implementation-report.md`
- write `specs/<feature>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`
- write phase checkpoints to `specs/<feature>/checkpoints.md` after each phase completes
- run `git status`, `git diff`, and `git log --oneline -10`
- when verification is green, inspector review passes, and only intended files are staged, present the merge summary to the user and request explicit confirmation
- on confirm: squash merge feature branch to main, delete feature branch
- on deny: keep branch, document reason in report
- finish with `specs/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`

Use `dream-implementer`, `dream-implementer-android`, and `dream-implementer-ios` for code changes (layer-appropriate) and `dream-inspector` as the final QA gate for substantial work.

Do not commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing. Do not commit until the user explicitly approves the commit after Dream reports verification and review are green.
