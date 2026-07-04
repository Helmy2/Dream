# Token Optimization Guidelines

Guidelines for optimizing token usage in Dream projects. Apply these when running `/dream-improve`.

## Overview

These guidelines reduce token usage by 70-85% and costs by 80-90% through:
- Context tiering (load only what's needed)
- Knowledge graph compression (condensed rules)
- Agent consolidation (merged discovery)
- Incremental verification (skip unnecessary checks)
- Streamlined outputs (5 files instead of 10)
- Context compaction (fresh conversations after 3 rounds)

## 1. Agent Definitions

### 3-Tier Context Loading

Every code-writing agent should use:

**Tier 1 (always loaded):**
- Agent definition file
- `rules-condensed.md`

**Tier 2 (load before work):**
- Layer-specific best-practice files
- UI spec, verification commands, etc.

**Tier 3 (load only if needed):**
- Detailed files per `rules-index.md`
- iOS files (35-39) only for iOS tasks

### Example Agent Structure

```markdown
---
description: [Short description]
mode: subagent
---

You are `dream-[role]`. [One-line purpose]

## Scope
- [What this agent owns]

## Context Loading (Tiered)

**Tier 1 (always loaded):** This file + condensed rules (`.opencode/knowledge-graph/rules-condensed.md`)

**Tier 2 (load before work):**
- [Relevant best-practice files]

**Tier 3 (load only if needed):**
- [Detailed files per rules-index.md]

## Key Rules
- [Most important rules]

## Constraints
See `.opencode/guidance/reference/agent-constraints.md`
```

## 2. Knowledge Graph

### Condensed Rules

- `rules-condensed.md` should contain all critical rules in ~135 lines
- Format: `[rule-id] Rule statement [file:XX]`
- Organized by layer: Architecture, Domain, Data, Presentation, etc.

### Rules Index

- `rules-index.md` should map topics to full files
- Include "Quick Lookup" section for common questions
- Enable agents to find specific rules without loading everything

## 3. Reference Files

Keep these files in `.opencode/guidance/reference/`:

| File | Purpose | Lines |
|------|---------|-------|
| `non-negotiable-rules.md` | Core architecture rules | ~43 |
| `agent-constraints.md` | Per-agent "do not" constraints | ~63 |
| `verification-commands.md` | All verification commands | ~104 |
| `skip-conditions.md` | Repair loop stop conditions | ~45 |
| `sample-cleanup-checklist.md` | One-time cleanup checklist | ~50 |

## 4. Operating Guide

Keep under 200 lines by:
- Referencing reference files instead of inline rules
- Using concise bullet points
- Including context caching and compaction sections

## 5. Output Structure

Use 5 files instead of 10:

| File | Combines |
|------|----------|
| `contract.yaml` | app-contract + decisions + feature contract |
| `spec.md` | ui-spec + test-plan |
| `report.md` | implementation-report + verification-report + retrospective |
| `maestro/android.yaml` | (keep separate) |
| `maestro/ios.yaml` | (keep separate) |

## 6. Verification

### Incremental Verification

Based on changed files, run only necessary checks:

**If only domain/data changed:**
- Domain/data tests only
- Skip: assembleDebug, xcodebuild, Maestro

**If only impl changed:**
- Impl tests + Android build
- Skip: xcodebuild (unless iOS also changed)

**If only iOS files changed:**
- xcodebuild build + test
- Skip: Android build, Gradle tests

**If shared/* changed:**
- Full verification ladder

Use `git diff --name-only` to identify changed modules.

## 7. Context Compaction

After 3 repair rounds or when context approaches limit:

1. Write state to `.features/<feature>/report.md`
2. Create compact handoff prompt
3. Start fresh conversation with handoff only

## 8. Compaction Settings

In `opencode.json`:

```json
{
  "compaction": {
    "auto": true,
    "prune": true,
    "reserved": 10000
  }
}
```

## 9. MCP Code Intelligence

codebase-memory-mcp provides code-level intelligence via MCP, replacing file-by-file exploration:

- **99% token reduction** vs grep/read exploration
- `search_graph` replaces grep for finding symbols
- `trace_path` replaces reading multiple files to understand call chains
- `detect_changes` replaces manual git diff analysis
- `get_architecture` replaces reading multiple files for project overview

**Priority**: Agents should always try MCP tools first. Fall back to grep/glob only when searching string literals, non-code files, or when MCP returns insufficient results.

See `.opencode/guidance/reference/codebase-memory-mcp-tools.md` for full tool reference.

## Checklist

Use this checklist when applying token optimization:

- [ ] All agents use 3-tier context loading
- [ ] `rules-condensed.md` exists and is referenced
- [ ] `rules-index.md` exists and maps topics to files
- [ ] Reference files exist in `.opencode/guidance/reference/`
- [ ] Operating guide is under 200 lines
- [ ] Operating guide includes caching/compaction sections
- [ ] Combined templates exist for outputs
- [ ] `dream-tester` includes incremental verification
- [ ] `dream-discovery` agent exists (merged explore + context-scout)
- [ ] `opencode.json` includes compaction settings
