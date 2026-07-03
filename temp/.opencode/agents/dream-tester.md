---
description: Full verification ladder — tests, builds, device install/launch, Maestro smoke flows.
mode: subagent
permission:
  edit: deny
  bash: allow
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
- Patterns: `.opencode/guidance/reference/verification-patterns.md`

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

## Parallel Verification

When both Android and iOS platforms changed, use parallel verification:

### Parallel Mode

```yaml
verification:
  mode: parallel
  android:
    status: passed | blocked | not run
    duration: 3m 42s
  ios:
    status: passed | blocked | not run
    duration: 4m 15s
  total_duration: 4m 20s  # wall clock, not sum
```

### How to Parallelize

1. Detect if both platforms changed: `git diff --name-only | grep -E "(androidApp|iosApp-swiftui|impl/)"`
2. If both changed, run Android and iOS verification concurrently:
   - Spawn Android verification: `./gradlew :androidApp:assembleDebug` + Android CLI checks
   - Spawn iOS verification: `xcodebuild` + `xcrun simctl` checks
3. Merge results into single verification-report.md
4. Report wall-clock time (max of both), not sum

### Sequential Mode (Default)

When only one platform changed, use sequential verification as before.

## Verification Pattern Tracking

After verification completes, update `.opencode/guidance/reference/verification-patterns.md`:

1. **Check for known patterns**: If failure matches a pattern, apply mitigation
2. **Record new patterns**: If failure is new, add it to patterns file
3. **Update frequencies**: Increment frequency count for matched patterns
4. **Update last_seen**: Set last_seen to today's date

### Pattern Matching

Before reporting a failure as "new", check if it matches any pattern in `verification-patterns.md`:

- DI resolution failures → `di-resolution` pattern
- iOS bridge export missing → `ios-bridge-export` pattern
- String resource missing → `string-resource-missing` pattern
- Navigation entry not registered → `navigation-entry-missing` pattern
- Test mock missing → `test-mock-missing` pattern

If pattern matches, include mitigation in failure report.

## Spec Compliance Verification

After UI implementation, verify the implemented UI matches the UI spec:

- Read `.features/<feature>/ui-spec.md` for expected states and components
- For each screen state (Loading, Empty, Content, Error):
  - Capture screenshot via `mobile_take_screenshot`
  - Compare against spec: are expected components present? Are states rendered correctly?
  - Record compliance: `matches spec` / `deviates from spec` / `state not implemented`
- Report spec compliance in verification report
- If significant deviations found, report as verification failure with specific details

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
