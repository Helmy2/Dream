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
- choose an archetype from `.opencode/archetypes/README.md` (read each `archetype.yaml`) and record it in the plan's Archetype section, or record `none` with the reason
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
- run `./.opencode/scripts/dream-preflight.sh` from the project root when it exists
- verify the destination project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
### Contract & Implementation
- generate or update `specs/app-contract.yaml` (do not overwrite if it already exists with content)
- generate or update `specs/decisions.yaml`
- instantiate the appropriate Dream template (this command handles its own initialization — `/dream-init` is a prerequisite for `/dream-feature` and `/dream-verify` only)
- generate the first feature contract (`specs/<feature>/contract.yaml`) before any feature code is written; it covers slice 1 first and grows with each later slice
- if the product request names several features, treat each as its own feature in plan order, and run the Slice Loop for each one
- build each feature **one slice at a time** using the Slice Loop in `.opencode/agents/dream-general.md`, in the order of the plan's Slices table; do not start slice N+1 until slice N is green and committed
- before each slice's implementation, amend the contract (record changes to earlier-slice definitions in its change notes) and confirm the slice's entity fields, actions, states, time behavior, storage choices, and required tests are explicit
- for each slice:
  - if the slice needs a new dependency or API, delegate to `dream-research`
  - for new or changed screens, delegate to `dream-designer` to update `specs/<feature>/ui-spec.md`
  - **domain + data + api**: delegate to `dream-implementer` for the slice's entities, repository interfaces, data mappings, domain/data tests, and `feature/<name>-api` with `NavKey` route types only
  - **UI (parallel)**: launch Compose UI (`dream-implementer-ui` platform: android, for `feature/<name>-impl`) and SwiftUI (`dream-implementer-ui` platform: ios, for views, ViewModels, models, bridge) as independent subagent calls
  - **wiring**: `dream-implementer` for umbrella imports, DI registration, navigation entries
  - iterate with focused commands (`./kotlin test -m <module> --platform=...`, `verify-feature.sh <feature-name> --quick`); run the full gate only when those pass
  - **gate**: run `.opencode/scripts/verify-feature.sh <feature-name>` (full: Android and iOS); if the slice changed UI, also have `dream-tester` install and launch on Android and on an iOS simulator (`xcrun simctl`) and inspect logs. Android-only is not enough
  - repair until the slice is green or a skip condition triggers (see `.opencode/guidance/reference/skip-conditions.md`; the iteration counter is per slice)
  - commit the green slice on the feature branch, update its status in `plan.md`, append to `specs/<feature>/checkpoints.md`, and continue to the next slice without asking the user
- stop and ask the user only on failure (skip condition, repeated environment blocker, a slice that cannot be made green)
- after the last slice: run `dream-inspector` as the final QA gate over the whole feature
- update `specs/<feature-name>/implementation-report.md`
- write `specs/<feature>/retrospective.md` using `.opencode/guidance/templates/template-improvement-retrospective-template.md`
- run `git status`, `git diff`, and `git log --oneline -10`
- when verification is green, inspector review passes, and only intended files are staged, present the merge summary (list the slices and their commits) to the user and request explicit confirmation
- on confirm: squash merge feature branch to main, delete feature branch
- on deny: keep branch, document reason in report
- finish with `specs/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`

Use `dream-implementer`, `dream-implementer-ui` (platform: android), and `dream-implementer-ui` (platform: ios) for code changes (layer-appropriate) and `dream-inspector` as the final QA gate for substantial work.

Do not commit blocked, partial, or unsafe worktree states, and never commit to main. A slice is committed on the feature branch only after its full gate is green; if unrelated changes make safe staging unclear, stop and report the blocker instead of committing. Merging the feature branch to main requires the user's explicit approval after Dream reports verification and review are green.
