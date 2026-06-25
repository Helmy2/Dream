---
description: Verify and repair a Dream project
agent: dream-general
---

Use Dream's verification and repair workflow on the current project.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `AGENTS.md`
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- relevant feature contracts

Then:
- run early preflights before verification depends on the environment:
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker
- verify Maestro is installed for smoke-flow validation, or record a precise Maestro environment blocker
- run the required verification commands
- diagnose any failures
- use `dream-implementer` for minimal architecture-safe fixes
- rerun verification until green, blocked, or a skip condition triggers (see Skip Conditions in dream-agent-operating-guide.md)
- use `dream-inspector` as the final QA gate for substantial work
- run `git status`, `git diff`, and `git log --oneline -10` after a completed feature flow
- if this command is part of a completed feature generation and review is approved, stage only intended feature files and create a concise feature commit automatically
- update `.mvp/verification-report.md` with the final validated or blocked result using `.opencode/guidance/templates/verification-report-template.md`

Do not auto-commit blocked, partial, unreviewed, or unsafe worktree states.
