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

When any skip condition triggers, `dream-general` must:

1. **Stop repairing immediately**
2. **Record the skip condition** in `.features/verification-report.md`:
   - Skip condition type
   - Iteration count
   - Failure history
   - What was tried
   - What failures remain
3. **Present recovery options to user**:

### Recovery Options

When a phase fails or skip condition triggers, present these options:

#### 1. **Repair**
- Delegate to appropriate implementer with fix suggestion
- Provide context: what failed, what was tried, what to try next
- Continue workflow after repair

#### 2. **Rollback**
- Use `git checkout <last-checkpoint>` to revert to last good state
- Checkpoints are in `.features/<feature>/checkpoints.md`
- Allows user to try a different approach

#### 3. **Skip**
- Document blocker in `verification-report.md`
- Proceed to next phase
- Mark skipped checks as `blocked by environment` or `not run`

#### 4. **Stop**
- End workflow immediately
- Preserve feature branch for manual fix
- Document reason in `.features/<feature>/report.md`
- User can resume later with `/dream-resume`

### Example Error Recovery Message

```markdown
## Phase 3 Failed: iOS Build Error

**Root cause**: Missing Swift bridge export in `iosApp-swiftui/iosApp/Core/`

**Iteration**: 2 of 5
**Failures**: 3 (2 iOS build errors, 1 iOS test failure)

### Recovery Options

1. **Repair**: Delegate to `dream-implementer-ui` (platform: ios) with fix suggestion:
   - Add missing export to `SharedBridge.swift`
   - Run `ruby scripts/sync-pbxproj.rb`

2. **Rollback**: `git checkout a1b2c3d` (last checkpoint: Phase 2 completed)

3. **Skip**: Document iOS blocker, proceed to inspection with Android-only verification

4. **Stop**: End workflow, preserve branch `dream/feature/expense-tracker` for manual fix

**What would you like to do?** (repair / rollback / skip / stop)
```

## Iteration Tracking

1. Before first repair, record initial failure count and set iteration = 1
2. After each repair, delegate `dream-tester` to run verification and record: iteration number, failure count, list of failing tests/checks, whether new failures appeared
3. Before starting next repair, check all skip conditions against tracked data
4. When loop ends (green or blocked), include iteration history in `.features/verification-report.md`
