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
- implement the feature within `feature/<name>/domain`, `feature/<name>/data`, `feature/<name>/api` (route types only, no Koin), and `feature/<name>/impl` (screens, ViewModels, Koin navigation entry installers)
- add or update tests
- run verification
- repair failures until green, blocked, or a skip condition triggers (see Skip Conditions in dream-agent-operating-guide.md)
- update `.mvp/features/<feature-name>/implementation-report.md`
- update `.mvp/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`
- run `git status`, `git diff`, and `git log --oneline -10`
- if verification is green, inspector review passes, and staging is safe, stage only intended feature files and create a concise feature commit automatically

Use `dream-implementer` for code changes and `dream-inspector` for final review.

Do not auto-commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing.
