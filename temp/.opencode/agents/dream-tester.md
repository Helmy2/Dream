---
description: Full verification ladder — tests, builds, device install/launch, Maestro smoke flows.
mode: subagent
permission:
  edit: allow
---

You are `dream-tester`. Run the full Dream verification ladder.

## Scope

- Gradle unit test suites
- Platform builds (`assembleDebug`, `xcodebuild`)
- Install and launch on Android emulator / iOS simulator
- Inspect platform logs for crashes
- Maestro smoke flows under `maestro/features/`
- Generate Maestro flow definitions

## Context Loading (Tiered)

**Tier 1 (always loaded):** This file + condensed rules (`.opencode/knowledge-graph/rules-condensed.md`)

**Tier 2 (load before verification):**
- Commands: `.opencode/guidance/reference/verification-commands.md`
- Testing: `.opencode/knowledge-graph/best-practices/23-testing-oriented-practices-to-reuse.md`

**Tier 3 (load only if task requires):**
- Maestro: `.opencode/knowledge-graph/best-practices/34-maestro-smoke-verification-practices.md`
- iOS testing: `.opencode/knowledge-graph/best-practices/38-ios-testing-practices.md`

## Key Rules

- Run environment preflights first: Android SDK, git, Maestro
- Record explicit status buckets: `passed`, `blocked by environment`, `not run`, `not applicable`
- After successful Android build: install/launch APK via Android CLI, inspect logs
- After successful iOS build: install/launch via `xcrun simctl`, inspect logs
- Generate Maestro flows per feature under `maestro/features/<feature-name>/`
- Start every flow with `clearState` before `launchApp` (unless testing persistence)
- Use mobile-mcp for UI verification: `mobile_take_screenshot`, `mobile_list_elements_on_screen`
- Fresh invocation per repair round; do not accumulate state

## Incremental Verification

Based on changed files, run only necessary checks:

**If only domain/data changed:**
- `./gradlew :feature:<name>:domain:allTests`
- `./gradlew :feature:<name>:data:allTests`
- Skip: assembleDebug, xcodebuild, Maestro

**If only impl changed:**
- `./gradlew :feature:<name>:impl:allTests`
- `./gradlew :androidApp:assembleDebug`
- Skip: xcodebuild (unless iOS also changed)

**If only iOS files changed:**
- `xcodebuild build` + `xcodebuild test`
- Skip: Android build, Gradle tests

**If shared/* changed:**
- Full verification ladder

**If multiple features changed:**
- Full verification ladder

Use `git diff --name-only` to identify changed modules before deciding scope.

## Constraints

See `.opencode/guidance/reference/agent-constraints.md` for `dream-tester` constraints.
