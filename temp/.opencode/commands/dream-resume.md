---
description: Resume a Dream session from saved state
agent: dream-general
---

Resume a Dream workflow session from where it left off. Use this when a previous session hit the context limit, was interrupted, or needs to be continued.

Treat `$ARGUMENTS` as the feature name or path to the resume file.

Read:
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `.features/<feature>/resume.md` (if it exists)
- `.features/<feature>/plan.md` (if it exists)
- `.features/<feature>/checkpoints.md` (if it exists)
- `.features/<feature>/report.md` (if it exists)

Then:

### Step 1: Locate Resume State

- if `$ARGUMENTS` is a feature name, look for `.features/<feature>/resume.md`
- if `$ARGUMENTS` is a path, read that file directly
- if no resume file exists, check `.features/<feature>/checkpoints.md` for the last completed phase
- if neither exists, report: "No resume state found for <feature>. Start a new session with /dream-feature or /dream-create."

### Step 2: Read Resume Context

Read the resume file and extract:
- **Current phase**: which phase was in progress when the session stopped
- **Completed phases**: which phases finished successfully (with git hashes from checkpoints)
- **Iteration count**: how many repair rounds have been attempted
- **Failures**: what failed in the last attempt
- **Next action**: what the agent planned to do next
- **Relevant state**: pointers to contracts, specs, reports, verification status

### Step 3: Present Resume Summary to User

Present a clear summary:
```
Resuming: <feature name>
Current phase: <phase name>
Completed: <list of completed phases with hashes>
Iteration: <N> of max 5
Last failure: <what went wrong>
Next action: <what Dream will do next>
```

Ask the user:
1. **Continue** — pick up from where it left off
2. **Rollback** — go back to a previous checkpoint (show available checkpoints)
3. **Restart** — start the workflow over from Phase 0
4. **Abort** — stop and document the current state

### Step 4: Execute Based on User Choice

**On Continue:**
- verify the feature branch exists and is checked out
- if the last phase was interrupted mid-execution, re-delegate to the appropriate subagent
- if the last phase completed but the next phase hasn't started, begin the next phase
- write new checkpoints as phases complete
- continue tracking iterations against the max 5 limit

**On Rollback:**
- present available checkpoints with git hashes
- user selects a checkpoint to roll back to
- run `git checkout <hash>` to restore state
- update resume file to reflect the rollback
- continue from the rolled-back phase

**On Restart:**
- confirm with user that all progress will be lost
- delete or archive the existing resume file
- start fresh from Phase 0

**On Abort:**
- write final state to `.features/<feature>/report.md`
- document why the session was aborted
- do not merge or commit

### Step 5: Update Resume File

After each significant action, update `.features/<feature>/resume.md` with:
- current phase
- completed phases with git hashes
- iteration count
- any new failures or blockers
- next planned action

This ensures the session can be resumed again if needed.

### Resume File Format

The resume file should follow this structure:

```markdown
# Resume: <Feature Name>

## Current State
- Phase: <current phase name>
- Branch: <feature branch name>
- Iteration: <N> of 5
- Status: <in-progress | blocked | waiting-for-user>

## Completed Phases
- Phase 0: Plan & User Review — completed — <git-hash> — <timestamp>
- Phase 1: External Research — completed — <git-hash> — <timestamp>
- ...

## Last Action
- What was attempted: <description>
- Result: <success | failure | interrupted>
- Details: <error message or summary>

## Next Action
- <what Dream should do next>

## Blockers
- <any blockers preventing progress>

## Relevant Files
- Plan: `.features/<feature>/plan.md`
- Contract: `.features/<feature>/contract.yaml`
- UI Spec: `.features/<feature>/ui-spec.md`
- Report: `.features/<feature>/report.md`
- Checkpoints: `.features/<feature>/checkpoints.md`
```

Do not commit until the user explicitly approves. Do not merge without user confirmation.
