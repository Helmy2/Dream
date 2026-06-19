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
- run the required verification commands
- diagnose any failures
- use `dream-implementer` for minimal architecture-safe fixes
- rerun verification until green or blocked
- use `dream-inspector` as the final QA gate for substantial work
- update `.mvp/verification-report.md` with the final validated or blocked result
