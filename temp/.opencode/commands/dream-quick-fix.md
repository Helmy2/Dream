---
description: Quick fix workflow for trivial changes — skips Phase 0 ceremony, goes straight to diagnosis → fix → verify.
---

# /dream-quick-fix

Quick fix workflow for trivial changes (1-3 files, <50 lines, no new modules).

## When to Use

- Typo fixes in strings or comments
- Single-line bug fixes
- UI tweaks (spacing, colors, text)
- Import statement fixes
- Minor refactoring (rename variable, extract constant)

## When NOT to Use

- New features (use `/dream-feature`)
- Multi-screen changes (use `/dream-feature`)
- Changes requiring new modules (use `/dream-feature`)
- Changes affecting >3 files (use `/dream-feature` or `/dream-bug-fix`)
- Changes requiring new dependencies (use `/dream-feature`)

## Workflow

### Step 1: Interpret Issue (2-line plan in conversation)

`dream-general` interprets the issue and presents a 2-line plan:

```
Issue: <what's wrong>
Fix: <what to change>
```

Example:
```
Issue: Typo in expense_title string
Fix: Change "Expnese" to "Expense" in strings.xml line 12
```

**No plan.md file created.** Plan exists only in conversation.

### Step 2: Gather Context (if needed)

If the issue is unclear, `dream-research` gathers minimal context:
- Locate the affected file(s)
- Identify the exact line(s) to change
- Return paths only (no deep context pack)

Skip this step if the issue is obvious from the user's description.

### Step 3: Apply Fix

Delegate to appropriate implementer:

- **UI changes** (Compose/SwiftUI): `dream-implementer-ui` (platform: android/ios/both)
- **Business logic** (domain/data): `dream-implementer`
- **Mixed**: `dream-implementer` for business logic, then `dream-implementer-ui` for UI

Implementer applies minimal fix. No contract.yaml, no ui-spec.md.

### Step 4: Verify

`dream-tester` runs targeted verification:

- If only domain/data changed: `./gradlew :feature:<name>:domain:allTests` + `./gradlew :feature:<name>:data:allTests`
- If only impl changed: `./gradlew :feature:<name>:impl:allTests` + `./gradlew :androidApp:assembleDebug`
- If only iOS changed: `xcodebuild build`
- If shared/* changed: Full verification ladder

**No Maestro flows** unless the change affects user-facing behavior.

### Step 5: Done

If verification passes:
- Commit directly to current branch (no feature branch needed for trivial fixes)
- Report: "Fixed and verified"

If verification fails:
- Escalate to `/dream-bug-fix` workflow
- Or retry with full `/dream-feature` workflow if fix is more complex than expected

## Skip Conditions

- **No inspector review** unless >2 files changed
- **No contract.yaml** created
- **No ui-spec.md** created
- **No plan.md** created (plan exists in conversation only)
- **No Maestro flows** unless user-facing behavior changed
- **No feature branch** (commit to current branch)

## Artifacts Created

Only:
- `verification-report.md` (if verification ran)

That's it. No other artifacts.

## Example Usage

### User
```
/dream-quick-fix
Typo in expense_title string: "Expnese" should be "Expense"
```

### dream-general
```
Issue: Typo in expense_title string
Fix: Change "Expnese" to "Expense" in shared/core/strings/src/commonMain/composeResources/values/strings.xml line 12
```

### dream-implementer
Applies fix to strings.xml.

### dream-tester
Runs: `./gradlew :shared:core:strings:allTests`
Result: Passed

### dream-general
```
Fixed and verified. Committed to main.
```

## Constraints

- Do not use for changes affecting >3 files
- Do not use for new features
- Do not use for changes requiring new modules
- Do not use for changes requiring new dependencies
- If fix turns out to be complex, escalate to `/dream-bug-fix` or `/dream-feature`
