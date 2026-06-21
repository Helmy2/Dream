---
description: Generate or update a Dream feature
agent: dream-general
---

Use Dream's OpenCode-first workflow to add or update a feature in the current project.

Treat `$ARGUMENTS` as the feature request.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- relevant `.knowledge-graph/best-practices/*.md`
- `NEW-FEATURE-CHECKLIST.md`
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- existing feature contracts if present

Then:
- generate or update `.mvp/features/<feature-name>/contract.yaml`
- stop and inspect the contract before implementation; confirm entity fields, action names, state names, time behavior, storage choices, design expectations, and required tests are explicit
- do not let `dream-implementer` write feature code until the contract exists and is the source of truth
- implement the feature within `feature/<name>/domain`, `feature/<name>/data`, and `feature/<name>/presentation`
- add or update tests
- run verification
- repair failures until green or blocked
- update `.mvp/features/<feature-name>/implementation-report.md`
- update `.mvp/verification-report.md`
- run `git status`, `git diff`, and `git log --oneline -10`
- if verification is green, inspector review passes, and staging is safe, stage only intended feature files and create a concise feature commit automatically

Use `dream-implementer` for code changes and `dream-inspector` for final review.

Do not auto-commit blocked, partial, unreviewed, or unsafe worktree states. If unrelated changes make safe staging unclear, stop and report the blocker instead of committing.
