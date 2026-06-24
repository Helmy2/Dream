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
- `.knowledge-graph/PRODUCT.md`
- relevant `.knowledge-graph/best-practices/*.md`

Then:
- run early preflights before implementation depends on the environment:
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the destination project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
- verify Maestro is installed for smoke-flow validation, or record a precise Maestro environment blocker
- generate or update `.mvp/app-contract.yaml`
- generate or update `.mvp/decisions.yaml`
- instantiate the appropriate Dream template
- generate the first feature contract before any feature code is written
- inspect the feature contract for entity fields, actions, states, time behavior, storage choices, and required tests
- only then hand implementation to `dream-implementer`
- implement the first feature using Dream's modular architecture
- run verification
- repair failures until green or blocked
- run `git status`, `git diff`, and `git log --oneline -10`
- when verification is green, inspector review passes, and only intended files are staged, create a feature commit automatically
- finish with `.mvp/verification-report.md` using `.opencode/guidance/templates/verification-report-template.md`

Use `dream-implementer` for code changes and `dream-inspector` as the final QA gate for substantial work.

Do not auto-commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing.
