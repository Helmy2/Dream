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
- generate or update `.mvp/app-contract.yaml`
- generate or update `.mvp/decisions.yaml`
- instantiate the appropriate Dream template
- generate the first feature contract
- implement the first feature using Dream's modular architecture
- run verification
- repair failures until green or blocked
- finish with `.mvp/verification-report.md`

Use `dream-implementer` for code changes and `dream-inspector` as the final QA gate for substantial work.
