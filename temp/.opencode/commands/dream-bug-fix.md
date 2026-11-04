---
description: Fix logic, crash, runtime, data, and state bugs in any layer
agent: dream-general
---

Use Dream's bug fix workflow to repair logic errors, crashes, runtime failures, data bugs, and state machine issues in the current project.

Treat `$ARGUMENTS` as the bug description, crash log, or reproduction steps.

Read:
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- relevant `.opencode/knowledge-graph/best-practices/*.md`
- `.opencode/guidance/new-feature-checklist.md`
- `specs/app-contract.yaml`
- `specs/decisions.yaml`
- relevant feature contracts

Then:

### Phase 0: Plan & User Review (mandatory first step)

- gather context using `dream-discovery` (deep mode) — understand affected features, layers, and modules
- clarify with the user:
  - what is the exact symptom (crash, wrong data, wrong navigation, no response, etc.)
  - is there a crash log, stack trace, or error message?
  - what are the reproduction steps?
  - does it happen on both platforms or one?

**If the user provides a crash log or stack trace (priority: hotfix):**
- skip the plan and go straight to Phase 1 (Diagnostics)
- generate the plan after root cause is identified in Phase 2
- create the feature branch (`dream/bug-fix/<desc>`) before any code changes

**If the user describes a logic bug without a trace (priority: normal):**
- produce a fix plan with a suspected root-cause layer (domain/data/api/presentation/bridge) and present to the user
- explicitly ask: **questions? edits? confirm? deny?**
- on **Confirm**: create feature branch and proceed to Phase 1
- on **Edit**: update plan, re-present, repeat
- on **Deny**: stop and document
- do not proceed to diagnostics or fix until the plan is confirmed

### Phase 1: Gather Diagnostics

Use the following techniques based on the bug type:

**For crashes:**
- check available crash reports via `mobile-mcp mobile_list_crashes` + `mobile_get_crash`
- check Android `adb logcat` for FATAL EXCEPTION or stack traces
- check iOS `xcrun simctl spawn booted log stream --level error` for crash logs
- ask the user for reproduction steps if none provided

**For runtime/logic bugs (wrong data, wrong state, wrong navigation):**
- run `./kotlin test -m <name>-domain --platform=android` and `./kotlin test -m <name>-data --platform=android` for targeted test feedback
- if tests pass but behavior is wrong, inspect the feature's contract and trace the data flow
- use `dream-search` for cryptic error messages, unfamiliar exceptions, or dependency issues

**For build/compilation bugs:**
- run `./kotlin build -m androidApp --platform=android` or `./kotlin build -m iosApp --platform=iosSimulatorArm64` and capture the error output
- delegate to `dream-search` for unfamiliar compiler errors

### Phase 2: Root Cause Analysis

- use `dream-discovery` to find the affected source files based on diagnostic output
- identify the failing layer by mapping error output to module boundaries:

  | Error Pattern | Likely Layer |
  |---------------|-------------|
  | SQL/DAO/entity mapping | `data` |
  | Domain model validation, use case logic | `domain` |
  | API contract, NavKey, navigation setup | `api` |
  | ViewModel state, screen rendering, compose | `impl` (Android) |
  | SwiftUI view, bridge protocol, @Observable | iosApp (iOS) |
  | DI resolution, missing module | `umbrella-*` |

- if the error could be in multiple layers, start with the innermost (domain → data → api → impl → bridge)
- document the suspected root cause and the files to modify in `specs/bug-fix/diagnosis.md`

### Phase 3: Apply Fix

- delegate to the layer-appropriate implementer:
  - `dream-implementer` for domain, data, api, or wiring bugs
  - `dream-implementer-android` for Compose/ViewModel bugs in `impl`
  - `dream-implementer-ios` for SwiftUI or bridge bugs
- include in the fix prompt:
  - the bug description and reproduction steps
  - the diagnostic output (stack trace, error message, failing test)
  - the suspected root cause
  - the specific files to modify
  - the fix scope (minimal change — fix the bug, don't refactor)

### Phase 4: Re-Verify

After the fix is applied, run targeted verification based on the fixed layer:

| Fixed Layer | Verification |
|-------------|-------------|
| domain only | `./kotlin test -m <name>-domain --platform=android` |
| data only | `./kotlin test -m <name>-data --platform=android` |
| api only | `./kotlin test -m <name>-api --platform=android` |
| impl (Android) | `./kotlin test -m <name>-impl --platform=android` + `./kotlin build -m androidApp --platform=android` |
| iosApp | `./kotlin build -m iosApp --platform=iosSimulatorArm64` + `xcrun simctl` install/launch |
| umbrella/wiring | `./kotlin build -m androidApp --platform=android` + platform launch |

**For crash bugs:** re-launch the app and reproduce the original steps. Check logs for no new crash.

**For logic bugs:** verify the previous failing behavior now produces the correct result.

**For build bugs:** verify the project compiles cleanly on the affected platform.

- repair until green, a real blocker is identified, or a skip condition triggers (max 5 iterations)
- if the fix introduces a regression in a different layer, include that layer in the next repair round

### Report

- write `specs/bug-fix/report.md` documenting:
  - bug description and reproduction steps
  - diagnostic output (crash log, error message, failing test)
  - root cause and files modified
  - fix verification status (fixed / partial / blocked)
- run `git status`, `git diff`, and `git log --oneline -10`
- if verification is green, inspector review passes (if >2 files changed), and staging is safe, present the commit summary to the user and request explicit confirmation before staging and committing
- on confirm: squash merge to main, delete feature branch
- on deny: keep branch, document reason in report

Use `dream-implementer`, `dream-implementer-android`, and `dream-implementer-ios` for code changes (layer-appropriate) and `dream-inspector` for final review when >2 files changed.

Do not commit blocked, partial, unreviewed, or unsafe worktree states. Do not commit until the user explicitly approves the commit after Dream reports verification and review are green.
