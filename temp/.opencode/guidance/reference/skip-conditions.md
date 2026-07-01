# Dream Skip Conditions

To prevent infinite repair loops, `dream-general` must enforce these conditions during the repair cycle.

## Conditions

### Max Iterations Exceeded (5)

After 5 repair rounds, if verification is still not green:
- Stop immediately
- Report `blocked: max repair attempts reached`
- Include summary of what was tried and what failures remain

### No Progress Detected

If total failure count has not decreased after 2 consecutive repair rounds:
- Stop immediately
- Report `blocked: no progress after N rounds — same failures persist`

### Regression Detected

If a repair introduces new test failures (previously passing test now fails):
- Stop immediately
- Report `blocked: regression detected — fix caused new failures`
- Do not continue fixing on top of a regression

### Environment Loop

If the same environment blocker (no Android device, missing SDK) appears in consecutive verification rounds:
- Stop immediately
- Report the blocker
- Do not retry the same failing command

## When a Skip Condition Triggers

1. Stop repairing immediately
2. Record the skip condition, iteration count, and failure history in `.features/verification-report.md`
3. Report status as `blocked` with a clear reason string

## Iteration Tracking

1. Before first repair, record initial failure count and set iteration = 1
2. After each repair, delegate `dream-tester` to run verification and record: iteration number, failure count, list of failing tests/checks, whether new failures appeared
3. Before starting next repair, check all skip conditions against tracked data
4. When loop ends (green or blocked), include iteration history in `.features/verification-report.md`
