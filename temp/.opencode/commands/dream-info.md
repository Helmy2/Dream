---
description: Display Dream tool overview, available commands, scripts, agents, and implemented features
agent: dream-general
---

Display a comprehensive overview of the Dream tool for the user.

## Instructions

You are to gather and display the following information about Dream. Read the specified sources and present a concise, well-organized summary.

### 1. Dream Overview

Read `ONBOARDING.md` (first 25 lines) to extract the project description and mental model.

### 2. Available Commands

Read all `.opencode/commands/*.md` files. For each, extract the `description:` from the frontmatter and the agent field. Present as a table.

### 3. Available Scripts

From the repository map in `ONBOARDING.md` and `AGENTS.md`, list:
- `scripts/dream-preflight.sh` — local readiness check for git, Android SDK, and tooling
- `scripts/remove-sample.sh` — removes sample feature files, strips sample deps, and checks for leftovers

### 4. Agents

Read `.opencode/guidance/opencode-agent-map.md` to list all agents with their one-line role summary. Present as a table.

### 5. Implemented Features

Read all `specs/*/retrospective.md` files in the target project. For each, extract:
- Feature name (from directory name)
- Short one-line description of what was implemented/improved
- Application status (Applied / Not Applied)

If no retrospective files exist, report: "No feature retrospectives applied yet."

---

## Output Format

Present as a clean terminal-friendly summary:

```
╔════════════════════════════════════════════════════════════════════╗
║                           DREAM OVERVIEW                           ║
╠════════════════════════════════════════════════════════════════════╣
<one-paragraph description from ONBOARDING.md>
╚════════════════════════════════════════════════════════════════════╝

┌──────────────────┬─────────────────────────────────────────────────┐
│ COMMAND          │ DESCRIPTION                                      │
├──────────────────┼─────────────────────────────────────────────────┤
│ /dream-create    │ Create a Dream MVP project from user request    │
│ /dream-feature   │ Add or update a feature in existing project     │
│ /dream-ui-fix    │ Fix visual, layout, and rendering issues        │
│ /dream-bug-fix   │ Fix logic, crash, runtime, data, state bugs     │
│ /dream-verify    │ Verification and repair workflow                │
│ /dream-e2e       │ Run Maestro end-to-end smoke flows for features │
│ /dream-review    │ Architecture and contract compliance review     │
│ /dream-improve   │ Apply unapplied retrospectives to template      │
│ /dream-resume    │ Resume a Dream session from saved state         │
│ /dream-init      │ Initialize specs/ directory structure       │
│ /dream-info      │ Display this overview                           │
└──────────────────┴─────────────────────────────────────────────────┘

┌───────────────────────────┬────────────────────────────────────────┐
│ SCRIPT                    │ PURPOSE                                │
├───────────────────────────┼────────────────────────────────────────┤
│ scripts/dream-preflight.sh│ git, Android SDK, tooling readiness   │
│ scripts/remove-sample.sh  │ Remove sample feature and check leftovers│
└───────────────────────────┴────────────────────────────────────────┘

┌──────────────────────┬───────────────────────────────────────────┐
│ AGENT                │ ROLE                                      │
├──────────────────────┼───────────────────────────────────────────┤
│ dream-general        │ Orchestrator — interprets requests...     │
│ dream-discovery      │ Fast codebase discovery + context packs   │
│ dream-search         │ External technical research               │
│ dream-designer       │ Translates contracts to UI specs          │
│ dream-implementer    │ KMP business foundation (domain/data/api) │
│ dream-implementer-android│ Jetpack Compose presentation layer  │
│ dream-implementer-ios│ Native SwiftUI layer                     │
│ dream-tester         │ Full verification ladder                  │
│ dream-inspector      │ Final QA and architecture review gate     │
└──────────────────────┴───────────────────────────────────────────┘

┌───────────────────────────┬────────────────────────────────────────┐
│ MCP SERVER                │ PURPOSE                                │
├───────────────────────────┼────────────────────────────────────────┤
│ mobile-mcp                │ Device interaction for verification    │
│                           │ and UI inspection (screenshots,        │
│                           │ element trees, crash logs)             │
├───────────────────────────┼────────────────────────────────────────┤
│ codebase-memory-mcp       │ Code intelligence via persistent       │
│                           │ knowledge graph (symbol search, call   │
│                           │ tracing, impact analysis)              │
└───────────────────────────┴────────────────────────────────────────┘

Implemented Template Improvements:
  • <feature-name> — <short description> [Applied/Not Applied]
  ...
```