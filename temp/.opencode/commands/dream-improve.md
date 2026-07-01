---
description: Apply unapplied feature retrospectives to improve the Dream template and apply token optimization guidelines
agent: dream-general
---

Apply all unapplied feature retrospectives to the Dream template and ensure the project follows token optimization guidelines. Use this when you want the AI to learn from past feature generations and improve the template, or when you want to optimize the project for token efficiency.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `.opencode/knowledge-graph/README.md`
- `.opencode/knowledge-graph/rules-condensed.md`
- `.opencode/knowledge-graph/rules-index.md`
- `.opencode/guidance/reference/token-optimization-guidelines.md`

Then:

## Step 1: Apply Retrospectives

- find all `.features/*/retrospective.md` files where `Applied: not yet` in the Application Status section
- if none found, report that all retrospectives are applied and continue to Step 2
- collect all recommended improvements across unapplied retros, grouped by target area:
  - guidance changes (`.opencode/guidance/`)
  - best-practices additions (`.opencode/knowledge-graph/best-practices/`)
  - anti-patterns (existing or new entry in `26-anti-patterns-to-avoid-when-reusing-this-architecture.md`)
  - template code changes (starter project files)
  - prompt or workflow changes (`.opencode/guidance/prompts/`, commands)
  - token optimization improvements (`.opencode/knowledge-graph/rules-condensed.md`, agent definitions)
- for each group, apply the recommended changes to the corresponding template files
- update each retro: set `Applied:` to the current date and `What was updated:` to a summary of changes made

## Step 2: Apply Token Optimization Guidelines

Check and apply the following token optimization guidelines:

### 2.1 Agent Definitions

- [ ] All agents use 3-tier context loading (Tier 1: always loaded, Tier 2: before work, Tier 3: if needed)
- [ ] Agent definitions reference `rules-condensed.md` as Tier 1
- [ ] Agent definitions list specific Tier 2 files relevant to their scope
- [ ] Agent definitions reference `rules-index.md` for Tier 3 lookups
- [ ] No agent loads the full 41-file knowledge graph by default

### 2.2 Operating Guide

- [ ] Operating guide is under 200 lines
- [ ] Operating guide references reference files instead of inline rules
- [ ] Operating guide includes context caching instructions
- [ ] Operating guide includes context compaction workflow
- [ ] Operating guide includes incremental verification guidance

### 2.3 Knowledge Graph

- [ ] `rules-condensed.md` exists and contains critical rules from all layers
- [ ] `rules-index.md` exists and maps topics to full files
- [ ] Agents reference condensed rules by default

### 2.4 Reference Files

- [ ] `non-negotiable-rules.md` exists
- [ ] `agent-constraints.md` exists
- [ ] `verification-commands.md` exists
- [ ] `skip-conditions.md` exists
- [ ] `sample-cleanup-checklist.md` exists

### 2.5 Output Structure

- [ ] Combined templates exist (contract, spec, report)
- [ ] Operating guide references streamlined outputs (5 files instead of 10)
- [ ] Feature contracts use combined format

### 2.6 Verification

- [ ] `dream-tester` includes incremental verification rules
- [ ] Verification skips unnecessary checks based on changed modules

### 2.7 Agent Consolidation

- [ ] `dream-discovery` agent exists (merged explore + context-scout)
- [ ] Operating guide references `dream-discovery` instead of separate agents
- [ ] Agent map references `dream-discovery`

### 2.8 Compaction Settings

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

## Step 3: Verify Changes

- run `./gradlew :androidApp:assembleDebug` and `./gradlew allTests` (or at minimum `./gradlew projects`) to verify the template is still structurally valid after changes
- run `git status`, `git diff`, and `git log --oneline -10`
- present the commit summary to the user and request explicit confirmation before staging and committing template improvement files

Do not apply changes that would break the current project. If a recommendation
is ambiguous, skip it and note why in the retro. Do not commit until the user explicitly approves.
