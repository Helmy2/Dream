# Dream First Try Report

## Purpose

This file records the outcome of Dream's first real end-to-end generation attempt.

It should capture:

- prompt used
- generated output
- verification result
- time and cost observations
- output quality
- tool failures
- follow-up improvements for Dream itself

## Run Summary

- Run type: first `/dream-create` smoke test
- Prompt: `Build a simple expense tracker where users can add expenses, categorize them, and see a monthly total.`
- Result: partial success

Why partial success:

- Dream generated a runnable project and passed the required verification commands
- the generated output still had contract, quality, and review-gap issues, so the run should not be treated as fully approved product quality

## Time And Cost
- Model: GPT 5.4
- Context used: `172,527` tokens
- Context usage shown: `43% used`
- Reported cost: `$3.30`

### Time

- Exact wall-clock duration was not captured in the recorded output
- Future Dream run reports should record:
  - start time
  - end time
  - total elapsed time
  - verification time
  - repair-loop count

## Generated Output

### Files Reported As Created Or Updated

- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- `.mvp/features/expenses/contract.yaml`
- `.mvp/features/expenses/test-plan.md`
- `.mvp/features/expenses/implementation-report.md`
- `.mvp/verification-report.md`

### Implementation Reported

- Dream template instantiation from `templates/dream-kmp-base`
- sample feature replaced with modular `feature/expenses/{domain,data,presentation}`
- centralized shell and navigation preserved
- shared strings moved to shared Compose resources
- tests added for domain and presentation behavior

### Verification Reported

- `./gradlew allTests` passed
- `./gradlew :androidApp:assembleDebug` passed
- `./gradlew :desktopApp:run` launched and was stopped intentionally after startup observation

### Additional Notes Reported

- project package root was `com.dream.expensetracker`
- `local.properties` was added with the detected Android SDK path for local verification
- delegated `dream-inspector` review failed in this environment because of a task/session database error

## Output Quality Assessment

### Structural Quality

- good

What was good:

- modular feature structure was preserved
- app shell stayed centralized
- required `.mvp/` metadata was generated
- basic verification loop worked

### Behavioral Quality

- mixed

What was weak:

- contract states were not fully implemented
- some required behaviors were not fully covered by tests
- generated logic used a hardcoded month value instead of a real time abstraction

### UI Quality

- weak

Observed issue:

- the UI was not good enough even though the project was structurally valid and runnable

Implication for Dream:

- Dream currently over-optimizes for architecture and compile success relative to UX quality and visual polish

### Identity Quality

- weak

Observed issue:

- package naming was wrong for the generated app identity

Implication for Dream:

- project instantiation rules need stricter app identity normalization

## Review Findings Captured From The First Try

These findings came from the manual review after delegated inspector review failed.

1. Feature contract states were not fully implemented.
- Contract required loading, empty, content, and error states.
- Actual implementation did not model all of those states explicitly.

2. Current-month behavior was hardcoded.
- The feature used a fixed month key instead of a real time provider flow.

3. The composition root was unstable.
- Repository and ViewModel creation happened inside a composable body.

4. Required test coverage was incomplete.
- Some contract and test-plan behaviors were not explicitly covered.

5. Verification status overstated real quality.
- Build and launch verification was green, but the contract and behavior review still had gaps.

## Tooling Failure

### Delegated Review Failure

- Tool: `dream-inspector`
- Failure: `NOT NULL constraint failed: session_message.seq`
- Effect:
  - delegated QA review could not complete
  - Dream had to rely on manual review instead

### What Dream Should Do Next Time

- record delegated review failure explicitly
- run manual review fallback
- avoid claiming full approval when delegated review cannot complete

## What The First Try Proved

The first try proved that Dream can:

- start from a plain natural-language request
- generate a modular KMP project
- create the expected `.mvp/` artifacts
- replace the sample feature with a real feature
- run the minimum local verification flow

The first try did not yet prove that Dream can:

- reliably satisfy contract-to-code parity
- reliably produce good UI quality
- normalize app identity and package naming correctly
- enforce all architecture and review rules strongly enough for final approval

## Improvements To Make Before The Next Try

1. tighten final success criteria
2. tighten contract-to-implementation mapping
3. tighten package and app identity normalization
4. enforce `TimeProvider` usage for time-based behavior
5. enforce stable composition-root rules
6. raise minimum UI quality requirements
7. define safe fallback behavior when inspector delegation fails
8. start tracking exact run time in addition to token and cost data

## Reporting Rules For Future Runs

Every future Dream run report should capture:

- prompt
- start time
- end time
- elapsed time
- token usage
- dollar cost
- generated files
- verification commands and outcomes
- review status
- output quality summary
- tool failures
- decision: `approved`, `partial`, or `blocked`
