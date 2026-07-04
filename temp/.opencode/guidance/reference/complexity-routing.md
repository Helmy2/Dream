# Complexity-Based Workflow Routing

This guide defines how `dream-general` automatically selects the appropriate workflow based on change scope.

## Purpose

Not all changes require the same level of ceremony. A typo fix shouldn't go through the same 8-artifact workflow as a complete feature. This routing system automatically selects the right workflow based on objective criteria.

## Command-Triggered COMPLEX

**If the user invoked `/dream-create` or `/dream-feature`, always use the COMPLEX workflow regardless of scope.**

These commands explicitly request a complete Dream feature with full artifacts, so skip the complexity evaluation and proceed directly to the COMPLEX workflow.

## Complexity Levels

### Trivial
**Criteria:**
- 1-3 files changed
- <50 lines of code
- No new modules created
- No new screens added
- No new dependencies
- No architectural changes

**Examples:**
- Typo fix in strings.xml
- Single-line bug fix
- UI tweak (spacing, color, text)
- Import statement fix
- Rename variable

**Workflow:** `/dream-quick-fix`
- No plan.md
- No contract.yaml
- No ui-spec.md
- No feature branch
- No inspector review
- Only verification-report.md created

**Artifacts:** 1 (verification-report.md)

---

### Simple
**Criteria:**
- 1 feature module
- <5 screens
- No new shared modules
- No new dependencies (or 1-2 minor ones)
- No architectural changes
- Uses existing infrastructure

**Examples:**
- Add expense list screen with 3 states
- Add settings screen with 2 toggles
- Add single-screen feature with local storage
- Extend existing feature with 1-2 screens

**Workflow:** Lightweight feature workflow
- Brief plan in conversation (no plan.md file)
- No contract.yaml (or minimal inline contract)
- No ui-spec.md (or minimal inline spec)
- Feature branch created
- Inspector review skipped (unless >2 files changed)
- verification-report.md created

**Artifacts:** 2-3 (conversation plan, verification-report.md, optional retrospective.md)

---

### Complex
**Criteria:**
- Complete feature requiring domain/data/api/impl modules
- Tests for all layers
- Both platforms (Android Compose + iOS SwiftUI)
- Full artifacts and verification

**Examples:**
- Add expense tracker with list, detail, add screens — domain/data/api/impl, Room persistence, tests, both platforms
- Build settings feature with preferences — domain/data/api/impl, DataStore, tests, both platforms
- Create user profile feature — domain/data/api/impl, tests, both platforms

**Workflow:** Full feature workflow
- Full plan.md with all sections
- Full contract.yaml
- Full ui-spec.md
- Feature branch created
- Inspector review required
- All artifacts created (plan, contract, spec, report, retrospective, verification-report, checkpoints)

**Artifacts:** 7+ (plan.md, contract.yaml, ui-spec.md, report.md, retrospective.md, verification-report.md, checkpoints.md)

## Evaluation Algorithm

`dream-general` evaluates complexity using this decision tree:

```
1. Check for command trigger
   - If /dream-create or /dream-feature → COMPLEX (skip evaluation)
   - Otherwise → continue to step 2

2. Count files to change
   - If 1-3 files → likely trivial
   - If 4-10 files → likely simple
   - If >10 files → likely complex

3. Count new screens
   - If 0 screens → trivial or simple
   - If 1-4 screens → simple
   - If >5 screens → complex

4. Check for new modules
   - If no new modules → trivial or simple
   - If new feature modules only → simple
   - If new shared modules → complex

5. Check for new dependencies
   - If no new deps → trivial or simple
   - If 1-2 minor deps → simple
   - If 3+ deps or major deps → complex

6. Check for architectural changes
   - If no arch changes → trivial or simple
   - If arch changes needed → complex

7. Check for complete feature (all layers)
   - If requires domain/data/api/impl + tests + both platforms → complex

8. Final classification:
   - All criteria point to trivial → TRIVIAL
   - Any criterion points to simple, none to complex → SIMPLE
   - Any criterion points to complex → COMPLEX
```

## Evaluation Output

When evaluating complexity, `dream-general` outputs:

```markdown
## Complexity Evaluation

**Classification:** TRIVIAL | SIMPLE | COMPLEX

**Criteria:**
- Files to change: N
- New screens: N
- New modules: N
- New dependencies: N
- Architectural changes: yes/no

**Selected workflow:** /dream-quick-fix | lightweight | full

**Artifacts to create:**
- [ ] plan.md
- [ ] contract.yaml
- [ ] ui-spec.md
- [ ] verification-report.md
- [ ] retrospective.md
- [ ] checkpoints.md

**Proceeding with workflow...**
```

## Override Rules

The user can override the automatic classification:

- User says "this is simple" → downgrade to simple workflow
- User says "treat this as complex" → upgrade to full workflow
- User says "just fix it" → force trivial workflow

`dream-general` respects user overrides but warns if downgrade might miss important steps.

## Workflow Mapping

| Complexity | Command | Phase 0 | Artifacts | Inspector |
|------------|---------|---------|-----------|-----------|
| Trivial | `/dream-quick-fix` | 2-line conversation | 1 | No |
| Simple | Lightweight feature | Brief conversation | 2-3 | No (unless >2 files) |
| Complex | `/dream-create`, `/dream-feature`, or auto-detected complete feature | Full plan.md | 8+ | Yes |

## Examples

### Example 1: Trivial

**User request:** "Fix typo in expense_title string"

**Evaluation:**
- Files: 1 (strings.xml)
- Screens: 0
- Modules: 0
- Dependencies: 0
- Arch changes: No

**Classification:** TRIVIAL
**Workflow:** `/dream-quick-fix`
**Artifacts:** verification-report.md only

---

### Example 2: Simple

**User request:** "Add expense list screen with loading, empty, and content states"

**Evaluation:**
- Files: 5-8 (screen, viewmodel, ui state, mapper, tests, strings)
- Screens: 1
- Modules: 1 (feature/expense/impl)
- Dependencies: 0
- Arch changes: No

**Classification:** SIMPLE
**Workflow:** Lightweight feature
**Artifacts:** conversation plan, verification-report.md

---

### Example 3: Complex

**User request:** "Add expense tracker with list, detail, add screens — domain/data/api/impl, Room persistence, tests, both platforms"

**Evaluation:**
- Files: 15+
- Screens: 3
- Modules: 4 (domain, data, api, impl)
- Dependencies: 1 (Room)
- Arch changes: No
- Complete feature: Yes (all layers + both platforms)

**Classification:** COMPLEX
**Workflow:** Full `/dream-feature`
**Artifacts:** All 8+ artifacts

## Integration with Phase 0

For **trivial** and **simple** workflows, Phase 0 is adapted:

### Trivial Phase 0
```
dream-general: "Issue: Typo in expense_title string. Fix: Change 'Expnese' to 'Expense' in strings.xml line 12. Proceed?"
User: "yes"
```

### Simple Phase 0
```
dream-general: "Plan: Add expense list screen with 3 states (Loading, Empty, Content). Will create feature/expense/impl module with ExpenseListScreen, ExpenseListViewModel, and tests. No new dependencies. Proceed?"
User: "yes"
```

### Complex Phase 0
Full plan.md created and presented for review.

## Benefits

1. **Reduces ceremony** for simple changes (70% less artifacts for trivial)
2. **Maintains rigor** for complex changes (full workflow preserved)
3. **Automatic routing** (no manual workflow selection needed)
4. **User override** (can upgrade/downgrade if needed)
5. **Clear criteria** (objective, not subjective)
