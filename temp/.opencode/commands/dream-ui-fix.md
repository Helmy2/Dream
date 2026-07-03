---
description: Fix visual, layout, and rendering issues in Compose and SwiftUI screens
agent: dream-general
---

Use Dream's UI fix workflow to repair visual, layout, or rendering issues in the current project.

Treat `$ARGUMENTS` as the UI issue description.

Read:
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- relevant `.opencode/knowledge-graph/best-practices/*.md`
- `.opencode/guidance/new-feature-checklist.md`
- `.features/app-contract.yaml`
- `.features/decisions.yaml`
- relevant feature contracts and UI specs

Then:

### Phase 0: Plan & User Review (mandatory first step)

- gather context using `dream-discovery` (deep mode) — understand affected features, UI layer (Compose/SwiftUI/both), and relevant architecture rules
- clarify with the user:
  - which platform(s) are affected (Android / iOS / both)
  - which screen(s) are affected
  - whether a screenshot or element tree is available
- produce a fix plan and present to the user (use `.opencode/guidance/templates/plan-template.md` for structure)
- explicitly ask: **questions? edits? confirm? deny?**
- on **Confirm**: create feature branch `dream/ui-fix/<short-description>` from main and proceed
- on **Edit**: update plan, re-present, repeat
- on **Deny**: stop and document
- do not proceed to capture, diagnosis, or fix until the plan is confirmed

### Phase 1: Capture (visual state before fix)

- if a device/simulator is available, use mobile-mcp tools to capture current visual state:
  - `mobile_take_screenshot` or `mobile_save_screenshot` — save to `.features/ui-fix/before-screenshot.png`
  - `mobile_list_elements_on_screen` — capture element tree for structural comparison
- if no device is available, ask the user to provide a screenshot and description
- record the device, screen, and timestamp of capture

### Phase 2: Diagnose (identify root cause layer)

- use `dream-discovery` to find the relevant UI source files:
  - for Compose: find the screen composable, ViewModel, and UI state model in `feature/<name>/impl`
  - for SwiftUI: find the view, ViewModel, and model files in `iosApp-swiftui/iosApp/<FeatureName>/`
- compare current visual state against the feature's UI spec (`.features/<feature>/ui-spec.md`)
  - identify which states (Loading/Empty/Content/Error) are rendering incorrectly
  - identify missing, mispositioned, or malformed components
- determine the fix layer:
  - **Compose-only issue** → `dream-implementer-android`
  - **SwiftUI-only issue** → `dream-implementer-ios`
  - **Cross-platform issue** → both (check if shared ViewModel/state is the root cause first)

### Phase 3: Apply Fix

- delegate to the layer-appropriate implementer:
  - `dream-implementer-android` for Compose screen composables, ViewModels, UI state/events, navigation entries
  - `dream-implementer-ios` for SwiftUI views, ViewModels, models, bridge code
- if the fix affects both platforms, launch parallel subagent calls
- include in the fix prompt:
  - the UI spec and expected rendering
  - the before-screenshot or description of what looks wrong
  - the element tree output if available
  - the specific files to modify
- do not modify domain, data, or api modules — UI fix commands only touch presentation layer

### Phase 4: Verify (visual state after fix)

- re-launch the app on the same device/simulator
- capture new screenshot and element tree:
  `mobile_take_screenshot` — save to `.features/ui-fix/after-screenshot.png`
  `mobile_list_elements_on_screen` for structural comparison
- present **before** and **after** evidence to the user
- ask: **does this fix the visual issue?**
  - **Yes** → mark as fixed, proceed to write report
  - **No** → check iteration count (max 5), loop back to Phase 2
  - **Partially** → document remaining issues, loop back to Phase 2
- if device/simulator is unavailable, ask the user to verify visually and report back

### Report

- write `.features/ui-fix/report.md` documenting:
  - issue description
  - before/after screenshots (file paths)
  - files modified
  - fix verification status (fixed / partial / blocked)
- write phase checkpoints to `.features/ui-fix/checkpoints.md` after each phase completes
- if verification passed, present the merge summary to the user and request explicit confirmation
- on confirm: squash merge feature branch to main, delete feature branch
- on deny: keep branch, document reason in report

Use `dream-implementer-android` and `dream-implementer-ios` for code changes (never domain/data/api) and `dream-inspector` for final review if >2 files were changed.

Do not commit blocked, partial, unreviewed, or unsafe worktree states. Do not commit until the user explicitly approves.
