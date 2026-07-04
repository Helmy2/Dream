---
description: Apply unapplied feature retrospectives to improve the Dream template and apply token optimization guidelines
agent: dream-general
---

Apply all unapplied feature retrospectives to the Dream template and ensure the project follows token optimization guidelines. Use this when you want the AI to learn from past feature generations and improve the template, when you want to optimize the project for token efficiency, or when the user requests specific workflow improvements.

Treat `$ARGUMENTS` as optional user-requested improvements (e.g., "improve agent handoff", "add complexity routing examples", "update verification commands").

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `.opencode/knowledge-graph/README.md`
- `.opencode/knowledge-graph/rules-condensed.md`
- `.opencode/knowledge-graph/rules-index.md`
- `.opencode/guidance/reference/token-optimization-guidelines.md`

Then:

## Step 1: Gather Improvements

### 1.1 From Retrospectives
- find all `.features/*/retrospective.md` files where `Applied: not yet` in the Application Status section
- if none found, report that all retrospectives are applied and continue to Step 1.2
- collect all recommended improvements across unapplied retros, grouped by target area:
  - guidance changes (`.opencode/guidance/`)
  - best-practices additions (`.opencode/knowledge-graph/best-practices/`)
  - anti-patterns (existing or new entry in `26-anti-patterns-to-avoid-when-reusing-this-architecture.md`)
  - template code changes (starter project files)
  - prompt or workflow changes (`.opencode/guidance/prompts/`, commands)
  - token optimization improvements (`.opencode/knowledge-graph/rules-condensed.md`, agent definitions)

### 1.2 From User Request
- if `$ARGUMENTS` contains user-requested improvements, parse them
- for each user request, identify:
  - target files (which `.opencode/` files would be edited)
  - change type (addition, modification, deletion, reorganization)
  - complexity (Low / Medium / High)
  - value (High / Medium / Low)

## Step 2: Generate Suggestion List

For each proposed improvement (from retrospectives AND user requests), generate a suggestion entry with:

```markdown
### Suggestion <N>: <title>

**Source:** Retrospective <feature-name> | User request

**Complexity:** Low | Medium | High
**Value:** High | Medium | Low

**Files to edit:**
- `.opencode/...` (what changes)
- `.opencode/...` (what changes)

**What changes:**
<brief description of the edit>

**Impact:**
- **Improves:** <what gets better>
- **Risk:** <what might break or need adjustment>
- **Dependencies:** <other files or workflows affected>
```

### Complexity Definitions

**Low:**
- Single file change
- Adding new content (no restructuring)
- Updating references or links
- Adding examples or documentation
- Token optimization (no logic changes)

**Medium:**
- 2-3 files to change
- Some restructuring of existing content
- Minor logic or workflow changes
- Updating multiple related files

**High:**
- 3+ files to change
- Restructuring existing content significantly
- Changing agent workflows or handoff logic
- Modifying verification commands
- Changes that affect multiple commands or agents
- Cross-cutting changes

### Value Definitions

**High:**
- Fixes workflow bottleneck or recurring issue
- Addresses critical gap in agent workflow
- Improves verification reliability
- Reduces token usage significantly

**Medium:**
- Improves quality but not blocking
- Reduces friction in common tasks
- Better documentation or examples
- Moderate token optimization

**Low:**
- Nice-to-have or cosmetic
- Minor documentation improvements
- Edge case handling
- Minimal impact on workflow

## Step 3: Present Suggestion List

Present the complete suggestion list in conversation, grouped by Value (High → Medium → Low), then by Complexity (Low → High):

```markdown
## Improvement Suggestions

Found <N> improvements from retrospectives and <M> user-requested changes.

### High Value (<count>)
<Suggestion 1>
<Suggestion 2>
...

### Medium Value (<count>)
<Suggestion N>
<Suggestion N+1>
...

### Low Value (<count>)
<Suggestion X>
<Suggestion X+1>
...

---

**How would you like to proceed?**
1. Apply all High Value suggestions
2. Apply only Low Complexity suggestions
3. Review and select individual suggestions
4. Skip all (no changes)
```

## Step 4: Apply Approved Changes

Based on user selection:
- mark which suggestions are approved
- for each approved suggestion:
  - apply the changes to the target files
  - if from retrospective: update `Applied:` to current date and `What was updated:` to summary
- for rejected suggestions:
  - if from retrospective: note in retrospective why skipped
  - if from user request: acknowledge and move on

## Step 5: Apply Token Optimization Guidelines

Check and apply the following token optimization guidelines:

### 5.1 Agent Definitions

- [ ] All agents use 3-tier context loading (Tier 1: always loaded, Tier 2: before work, Tier 3: if needed)
- [ ] Agent definitions reference `rules-condensed.md` as Tier 1
- [ ] Agent definitions list specific Tier 2 files relevant to their scope
- [ ] Agent definitions reference `rules-index.md` for Tier 3 lookups
- [ ] No agent loads the full 41-file knowledge graph by default

### 5.2 Operating Guide

- [ ] Operating guide is under 200 lines
- [ ] Operating guide references reference files instead of inline rules
- [ ] Operating guide includes context caching instructions
- [ ] Operating guide includes context compaction workflow
- [ ] Operating guide includes incremental verification guidance

### 5.3 Knowledge Graph

- [ ] `rules-condensed.md` exists and contains critical rules from all layers
- [ ] `rules-index.md` exists and maps topics to full files
- [ ] Agents reference condensed rules by default

### 5.4 Reference Files

- [ ] `non-negotiable-rules.md` exists
- [ ] `agent-constraints.md` exists
- [ ] `verification-commands.md` exists
- [ ] `skip-conditions.md` exists
- [ ] `sample-cleanup-checklist.md` exists

### 5.5 Output Structure

- [ ] Combined templates exist (contract, spec, report)
- [ ] Operating guide references streamlined outputs (5 files instead of 10)
- [ ] Feature contracts use combined format

### 5.6 Verification

- [ ] `dream-tester` includes incremental verification rules
- [ ] Verification skips unnecessary checks based on changed modules

### 5.7 Agent Consolidation

- [ ] `dream-discovery` agent exists (merged explore + context-scout)
- [ ] Operating guide references `dream-discovery` instead of separate agents
- [ ] Agent map references `dream-discovery`

### 5.8 Compaction Settings

- [ ] `opencode.json` includes compaction settings:
  ```json
  {
    "compaction": {
      "auto": true,
      "prune": true,
      "reserved": 10000
    }
  }
  ```

## Step 6: Verify Changes

- run `./gradlew :androidApp:assembleDebug` and `./gradlew allTests` (or at minimum `./gradlew projects`) to verify the template is still structurally valid after changes
- run `git status`, `git diff`, and `git log --oneline -10`
- present the commit summary to the user and request explicit confirmation before staging and committing template improvement files

Do not apply changes that would break the current project. If a recommendation is ambiguous, skip it and note why in the retro. Do not commit until the user explicitly approves.
