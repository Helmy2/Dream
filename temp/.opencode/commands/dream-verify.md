---
description: Verify and repair a Dream project
agent: dream-general
---

Use Dream's verification and repair workflow on the current project.

Read:
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `AGENTS.md`
- `features/app-contract.yaml`
- `features/decisions.yaml`
- relevant feature contracts

Then:

### Phase 0: Verification Plan (show user what will be verified)

- gather context: review `features/app-contract.yaml`, `features/decisions.yaml`, relevant feature contracts
- create a brief verification plan inline or in `features/verification-plan.md`:
  - what test suites will be run
  - what platform builds will be attempted
  - what device launch/log inspection steps will be performed
- present the verification plan to the user
- ask: **does this verification plan match your expectations?**
- get explicit confirmation before running the verification ladder
- if repairs are needed, create branch `dream/repair/<desc>` from main

### Preflights
- run early preflights before verification depends on the environment:
- run `./scripts/dream-preflight.sh` from the project root when it exists
- verify the project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work, or record a precise git blocker
- verify Android SDK readiness through `ANDROID_HOME` or the configured SDK path, or record a precise Android environment blocker

### Verification Ladder
- delegate to `dream-tester` for the verification ladder: unit tests, platform builds, device install/launch, log inspection
- diagnose any failures from tester output
- before each repair round, write iteration state to `features/verification-report.md` (current iteration, failures, past attempts, next module to fix)
- delegate fixes to the layer-appropriate implementer based on the failing surface:
  - `dream-implementer` for domain, data, api, or wiring failures
  - `dream-implementer-android` for Compose UI or `impl` failures
  - `dream-implementer-ios` for SwiftUI or bridge failures
- delegate reverification to `dream-tester` after each repair
- check skip conditions after each round: iteration ≤ 5? failure count decreased? no regressions? environment blocker repeated? (see Skip Conditions in dream-agent-operating-guide.md)
- use `dream-inspector` as the final QA gate for substantial work
- write phase checkpoints to `features/<feature>/checkpoints.md` after each phase completes
- run `git status`, `git diff`, and `git log --oneline -10` after a completed feature flow
- if this command is part of a completed feature generation and review is approved, present the merge summary to the user and request explicit confirmation
- on confirm: squash merge feature branch to main, delete feature branch
- on deny: keep branch, document reason in report
- update `features/verification-report.md` with the final validated or blocked result using `.opencode/guidance/templates/verification-report-template.md`

Do not commit blocked, partial, unreviewed, or unsafe worktree states. Do not commit until the user explicitly approves the commit after Dream reports verification and review are green.
