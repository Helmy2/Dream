---
description: Generate or update a Dream feature
agent: dream-general
---

Use Dream's OpenCode-first workflow to add or update a feature in the current project.

Treat `$ARGUMENTS` as the feature request.

Read:
- `guidance/opencode-agent-map.md`
- `guidance/dream-agent-operating-guide.md`
- relevant `knowledge-graph/best-practices/*.md`
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- existing feature contracts if present

Then:
- generate or update `.mvp/features/<feature-name>/contract.yaml`
- implement the feature within `feature/<name>/domain`, `feature/<name>/data`, and `feature/<name>/presentation`
- add or update tests
- run verification
- repair failures until green or blocked
- update `.mvp/features/<feature-name>/implementation-report.md`
- update `.mvp/verification-report.md`

Use `dream-implementer` for code changes and `dream-inspector` for final review.
