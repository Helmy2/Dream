# Dream Second Try Report

## Purpose

This file records the second end-to-end expense-tracker run after the first repair pass.

It captures:

- what improved since the first try
- what still failed at the Dream workflow level
- whether the generated code now matches the repaired template rules
- what should be fixed before the next run is treated as a clean approval candidate

## Run Summary

- Run type: second expense-tracker regression run after first-try repairs
- Prompt family: expense tracker MVP with add expense, category selection, monthly browsing, and monthly total
- Reported model: GPT 5.4
- Reported context usage: `188,434` tokens
- Reported cost: `$2.93`
- Reported duration: `23m`
- Result: improved but still partial

Why still partial:

- verification was green enough for the required surfaces
- manual review fallback found no blocking feature-code defects
- delegated tooling still failed
- Dream still generated directly inside `templates/dream-kmp-base` instead of instantiating a new destination project copy

## What Improved

- current-month behavior now flows through `TimeProvider`
- loading, empty, content, and error states are now modeled explicitly
- the generated app shell uses a stable composition-root pattern with caller-owned coroutine scope
- app identity is normalized to `com.dream.expensetracker`
- domain and presentation tests now cover the main required behaviors
- `.mvp/verification-report.md` records the delegated-tool failure and manual fallback instead of overstating approval

## Code Review Outcome

No blocking architecture or contract defects were found in the generated `feature/expenses` code during this review.

What the code gets right:

- `Presentation -> Domain <- Data` boundaries are preserved
- current month is derived through `GetCurrentExpenseMonthUseCase`
- `ExpensesState` models loading, empty, content, and error states explicitly
- `ExpenseTrackerApp` assembles dependencies at a stable remembered root instead of constructing them in a recomposing body
- tests cover current-month derivation, monthly totals, empty state, content transition, invalid input, and load failure

Residual quality risks:

- UI quality is structurally better but still plain; no stronger visual review or screenshot gate was applied
- desktop verification still relied on startup observation rather than a stronger behavioral desktop check

## Remaining Dream Workflow Defects

### 1. Template instantiation behavior is still wrong

The run output says Dream created and updated files directly inside `templates/dream-kmp-base`.

Why this matters:

- Dream templates are supposed to be copied into a destination app baseline and then customized
- mutating the template in place corrupts the reusable baseline and makes future runs less trustworthy
- this violates the current instantiation guidance even though the generated feature code itself looks better

Related defect:

- `feature/sample/` still exists alongside `feature/expenses/` in the generated tree
- because the run mutated the template in place, the sample module was not removed from the generated app surface the way Dream reports imply it should be
- once Dream instantiates into a destination copy correctly, `feature/sample` should be deleted from that generated app as soon as the first real feature replaces it

### 2. Delegated tooling failure is still unresolved

- `dream-implementer` delegation failed with `NOT NULL constraint failed: session_message.seq`
- `dream-inspector` delegation failed with the same error

Why this matters:

- Dream still depends on manual fallback for substantial runs
- approval remains operationally fragile even when code generation succeeds

### 3. Verification and approval semantics are still inconsistent

The generated verification report records a clean manual fallback review but still leaves the final result as `partial` purely because delegated review failed.

Why this matters:

- current guidance says delegated-tool failure should not automatically force a non-validated result when a clean manual review is completed and documented
- Dream still needs one consistent approval rule across prompts, reports, and run summaries

### 4. Generated implementation report did not use the new required template

`.mvp/features/expenses/implementation-report.md` contains a loose summary instead of the required contract mapping table with `implemented|partial|blocked` status rows.

Why this matters:

- the new reporting rule exists in guidance but was not reflected in this run output
- Dream still has a gap between prompt rules and emitted artifacts

### 5. Knowledge-graph artifacts were behind the markdown guidance before this review

The best-practice markdown files for identity normalization, stable composition roots, and UI quality existed, but `knowledge-graph/graph.json` and `knowledge-graph/source-chunks.json` had not been updated yet.

Why this matters:

- graph-backed retrieval could miss the repaired rules during later generation or review tasks
- Dream’s strongest rules should exist in the retrieval graph, not only in prose files

## Decision

- Feature-code quality: acceptable for this regression scenario
- Dream workflow quality: still not approved
- Final decision: `partial`

## What To Fix Before The Next Try

1. Make project instantiation copy the template into a destination app instead of mutating `templates/dream-kmp-base`.
2. Fix delegated tool/session persistence failures so implementer and inspector can complete reliably.
3. Align approval semantics so clean manual fallback review can produce a validated outcome when the guidance allows it.
4. Force generated implementation reports to use the required template structure.
5. Keep graph artifacts in sync with newly added best-practice files as part of the same repair change.