# Complexity Routing Examples

This document provides concrete examples of how `dream-general` evaluates complexity and routes to the appropriate workflow.

## Example 1: Trivial — Typo Fix

### User Request
```
/dream-feature
Fix typo in expense_title string: "Expnese" should be "Expense"
```

### Complexity Evaluation

```markdown
## Complexity Evaluation

**Classification:** TRIVIAL

**Criteria:**
- Files to change: 1 (strings.xml)
- New screens: 0
- New modules: 0
- New dependencies: 0
- Architectural changes: no

**Selected workflow:** /dream-quick-fix

**Artifacts to create:**
- [x] verification-report.md

**Proceeding with /dream-quick-fix...**
```

### Phase 0 (2-line conversation)
```
dream-general: "Issue: Typo in expense_title string. Fix: Change 'Expnese' to 'Expense' in shared/core/strings/src/commonMain/composeResources/values/strings.xml line 12. Proceed?"
User: "yes"
```

### Workflow
1. dream-implementer applies fix to strings.xml
2. dream-tester runs: `./gradlew :shared:core:strings:allTests`
3. Verification passes
4. Commit to current branch (no feature branch)
5. Done

### Artifacts Created
- verification-report.md (1 artifact)

---

## Example 2: Simple — Single Screen Feature

### User Request
```
/dream-feature
Add expense list screen with loading, empty, and content states
```

### Complexity Evaluation

```markdown
## Complexity Evaluation

**Classification:** SIMPLE

**Criteria:**
- Files to change: 6 (ExpenseListScreen.kt, ExpenseListViewModel.kt, ExpenseListUiState.kt, ExpenseListMapper.kt, ExpenseListTest.kt, strings.xml)
- New screens: 1
- New modules: 1 (feature/expense/impl — domain/data/api already exist or not needed)
- New dependencies: 0
- Architectural changes: no

**Selected workflow:** Lightweight feature

**Artifacts to create:**
- [x] Conversation plan (no file)
- [x] verification-report.md

**Proceeding with lightweight workflow...**
```

### Phase 0 (Brief conversation)
```
dream-general: "Plan: Add expense list screen with 3 states (Loading, Empty, Content). Will create feature/expense/impl module with ExpenseListScreen, ExpenseListViewModel, ExpenseListUiState, ExpenseListMapper, and tests. Uses existing ExpenseRepository from domain. No new dependencies. Proceed?"
User: "yes"
```

### Workflow
1. dream-research gathers context (if needed)
2. dream-implementer creates feature/expense/impl module with domain/data/api (if not exists)
3. dream-implementer-ui creates Compose UI + SwiftUI UI
4. dream-tester runs verification
5. Commit to feature branch
6. Done (no inspector)

### Artifacts Created
- Conversation plan (no file, just in chat)
- verification-report.md (2 artifacts)

---

## Example 3: Complex — Complete Feature

### User Request
```
/dream-feature
Add expense tracker with list, detail, add screens — domain/data/api/impl, Room persistence, tests, both platforms
```

### Complexity Evaluation

```markdown
## Complexity Evaluation

**Classification:** COMPLEX

**Trigger:** Command `/dream-feature` invoked

**Criteria:**
- Files to change: 15+
- New screens: 3
- New modules: 4 (feature/expense/domain, feature/expense/data, feature/expense/api, feature/expense/impl)
- New dependencies: 1 (Room database)
- Architectural changes: no
- Complete feature: yes (all layers + both platforms)

**Selected workflow:** Full /dream-feature

**Artifacts to create:**
- [x] plan.md
- [x] contract.yaml
- [x] ui-spec.md
- [x] report.md
- [x] retrospective.md
- [x] verification-report.md
- [x] checkpoints.md

**Proceeding with full /dream-feature workflow...**
```

### Phase 0 (Full plan.md)
```markdown
# Plan: Expense Tracker

## Priority
`normal`

## Goal
Build a complete expense tracking feature with 3 screens, Room database persistence, and full test coverage.

## Modules

### Create
- `feature/expense/domain` — entities, repository interfaces, use cases
- `feature/expense/data` — Room database, DAOs, repository implementations
- `feature/expense/api` — NavKey route types
- `feature/expense/impl` — 3 Compose screens, ViewModels, UI state/events

### Modify
- `shared/umbrella-core` — register domain/data DI modules
- `shared/umbrella-ui` — register UI DI modules and navigation entries
- `shared/core/strings` — add expense-related strings

## Screens

| Screen | Purpose | States |
|--------|---------|--------|
| ExpenseList | View all expenses | Loading / Empty / Content / Error |
| ExpenseDetail | View single expense | Loading / Content / Error |
| AddExpense | Add new expense | Content / Error |

## Architecture Decisions

- **Data flow**: repository-based with Room database
- **Storage**: Room database with ExpenseEntity
- **Time provider**: true — expense dates use TimeProvider
- **Backend**: disabled
- **Cross-feature reuse**: none

## Dependencies

- Room — local database persistence

## Phases

| Phase | Scope | Agent |
|-------|-------|-------|
| Phase 1 | External research (Room KMP patterns) | dream-research |
| Phase 1.5 | UI spec generation | dream-designer |
| Phase 2 | Domain, data, api modules + tests | dream-implementer |
| Phase 3 | Compose UI + iOS SwiftUI (parallel) | dream-implementer-ui (platform: both) |
| Phase 4 | Wiring: umbrellas, DI, navigation | dream-implementer |
| Phase 5 | Verification ladder | dream-tester |
| Final | Inspector review | dream-inspector |
```

### Workflow
1. Phase 0: Full plan.md created and presented
2. Phase 1: dream-research researches Room KMP patterns
3. Phase 1.5: dream-designer creates ui-spec.md
4. Phase 2: dream-implementer creates domain/data/api + tests
5. Phase 3: dream-implementer-ui creates 3 screens (Android + iOS)
6. Phase 4: dream-implementer wires umbrellas, DI, navigation
7. Phase 5: dream-tester runs full verification
8. Final: dream-inspector reviews work
9. Commit to feature branch, present merge summary

### Artifacts Created
- plan.md
- contract.yaml
- ui-spec.md
- report.md
- retrospective.md
- verification-report.md
- checkpoints.md
- Total: 7 artifacts

---

## Example 4: User Override — Downgrade

### User Request
```
/dream-feature
Add a settings screen with 2 toggles: dark mode and notifications. I know this is simple, just do it quickly.
```

### Complexity Evaluation
```markdown
## Complexity Evaluation

**Classification:** SIMPLE (auto-detected)

**Criteria:**
- Files to change: 4
- New screens: 1
- New modules: 1
- New dependencies: 0
- Architectural changes: no

**User override:** "just do it quickly" → downgrade to lightweight workflow

**Selected workflow:** Lightweight feature (user override respected)
```

### Phase 0 (Brief conversation)
```
dream-general: "Plan: Add settings screen with 2 toggles (dark mode, notifications). Will create feature/settings/impl with SettingsScreen, SettingsViewModel, and tests. Uses existing DataStore for preferences. Proceed?"
User: "yes"
```

---

## Example 5: User Override — Upgrade

### User Request
```
/dream-feature
Fix the crash in ExpenseListScreen when the list is empty. But I want you to treat this as complex because I want full verification and inspector review.
```

### Complexity Evaluation
```markdown
## Complexity Evaluation

**Classification:** TRIVIAL (auto-detected)

**Criteria:**
- Files to change: 1
- New screens: 0
- New modules: 0
- New dependencies: 0
- Architectural changes: no

**User override:** "treat this as complex" → upgrade to full workflow

**Selected workflow:** Full /dream-feature (user override respected)

**Warning:** Upgrading trivial fix to full workflow will create 8+ artifacts and run full verification. This may be overkill for a single-line fix. Proceed?
```

### User Response
```
User: "yes, I want the full workflow"
```

### Workflow
Full /dream-feature workflow with all phases and artifacts.

---

## Decision Tree

```
Start
  ↓
Check for command trigger
  ├─ /dream-create or /dream-feature → COMPLEX (skip evaluation)
  └─ otherwise → continue to evaluation
  ↓
Count files to change
  ├─ 1-3 files → likely trivial
  ├─ 4-10 files → likely simple
  └─ >10 files → likely complex
  ↓
Count new screens
  ├─ 0 screens → trivial or simple
  ├─ 1-4 screens → simple
  └─ >5 screens → complex
  ↓
Count new modules
  ├─ 0 modules → trivial or simple
  ├─ 1-3 feature modules → simple
  └─ new shared modules → complex
  ↓
Count new dependencies
  ├─ 0 deps → trivial or simple
  ├─ 1-2 minor deps → simple
  └─ 3+ deps or major deps → complex
  ↓
Check architectural changes
  ├─ no → trivial or simple
  └─ yes → complex
  ↓
Check for complete feature
  ├─ requires domain/data/api/impl + tests + both platforms → complex
  └─ otherwise → continue
  ↓
Final classification
  ├─ all trivial → TRIVIAL
  ├─ any simple, none complex → SIMPLE
  └─ any complex → COMPLEX
  ↓
User override?
  ├─ yes → respect override, warn if downgrade
  └─ no → use auto-detected classification
  ↓
Route to workflow
  ├─ TRIVIAL → /dream-quick-fix
  ├─ SIMPLE → lightweight feature
  └─ COMPLEX → full /dream-feature
```
