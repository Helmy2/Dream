# Dream OpenCode Operating Guide

## Purpose

Defines default behavior for OpenCode agents generating, reviewing, verifying, or repairing Dream projects.

**Use with**: `opencode-agent-map.md`, `PRODUCT.md`, `README.md`, `best-practices/`, `AGENTS.md`, `.features/` metadata.

## Default Mission

Produce KMP MVPs that are: runnable, verifiable, readable, repairable, aligned with Dream architecture.

Optimize for correct generation + future repairability, not just speed.

## Complexity-Based Workflow Routing

**Before starting any workflow, `dream-general` evaluates complexity to select the right level of ceremony.**

### Command-Triggered COMPLEX

**If the user invoked `/dream-create` or `/dream-feature`, always use the COMPLEX workflow regardless of scope.**

These commands explicitly request a complete Dream feature with full artifacts, so skip the complexity evaluation and proceed directly to the COMPLEX workflow.

### Complexity Levels

| Level | Criteria | Workflow | Phase 0 | Artifacts | Inspector |
|-------|----------|----------|---------|-----------|-----------|
| **Trivial** | 1-3 files, <50 lines, no new modules/screens | `/dream-quick-fix` | 2-line conversation | 1 | No |
| **Simple** | 1 feature, <5 screens, no new shared modules | Lightweight feature | Brief conversation | 2-3 | No (unless >2 files) |
| **Complex** | Complete feature requiring domain/data/api/impl, tests, both platforms | Full `/dream-feature` | Full plan.md | 8+ | Yes |

**Evaluation criteria**: Files to change, new screens, new modules, new dependencies, architectural changes, complete feature (domain/data/api/impl + tests + both platforms).

See `.opencode/guidance/reference/complexity-routing.md` for full details and examples.

## Agent Role Model

| Role | Agent | Owns |
|------|-------|------|
| Orchestrator | `dream-general` | Workflow, contracts, delegation, verification coordination |
| Discovery | `dream-discovery` | Fast codebase exploration + context building |
| Research | `dream-research` | External research + focused context packs |
| Design | `dream-designer` | UI/UX specs (no code) |
| KMP Business | `dream-implementer` | Domain, data, api, wiring |
| UI Implementation | `dream-implementer-ui` | Cross-platform UI — Compose (Android) + SwiftUI (iOS) |
| Verification | `dream-tester` | Tests, builds, device, Maestro |
| Review | `dream-inspector` | Final QA gate |

**Rule**: Use smallest capable agent per subtask. `dream-general` must not edit implementation files directly.

## Model Selection

Each agent uses a model optimized for its role. The default mapping balances cost, context window, and capability:

- **Orchestration & Implementation** (dream-general, dream-implementer, dream-inspector): DeepSeek V4 Pro — strongest reasoning, 1M context
- **UI & Design** (dream-designer, dream-implementer-ui): Qwen3.7 Plus — image support for visual tasks
- **Exploration & Research** (dream-discovery, dream-research): Big Pickle — free, sufficient for read-only tasks
- **Verification** (dream-tester): MiMo V2.5 — low cost with image support for screenshot comparison

Users can customize model assignments during `/dream-init` or by editing `opencode.json`. See `opencode-agent-map.md` for the full mapping and override instructions.

## Substantial Work Threshold

`dream-inspector` review required for:
- New feature generation
- Repair changing >2 files
- Changes to shared modules (`shared/core/*`, `shared/designsystem`, `shared/navigation`, `shared/umbrella-*`)
- Changes to contracts, specs, or architecture rules

Optional for: single-line corrections, documentation updates, typo fixes.

## Tooling

### MCP Servers

Two MCP servers are configured in `opencode.json`:

**mobile-mcp** — Device interaction for verification and UI inspection. Used by `dream-tester` and `dream-inspector` for screenshots, element trees, and crash logs.

**codebase-memory-mcp** — Code intelligence via persistent knowledge graph. Used by `dream-discovery`, `dream-research`, `dream-implementer`, `dream-implementer-ui`, and `dream-inspector` for symbol search, call tracing, impact analysis, and architecture overview.

**Rule**: Agents should prefer MCP queries over grep/read for codebase exploration. See `.opencode/guidance/reference/codebase-memory-mcp-tools.md` for tool reference and priority order.

### Ignore Files

- `.cbmignore` — Excludes files from codebase-memory-mcp indexing (build artifacts, generated files, Dream metadata)

## Non-Negotiable Rules

See `.opencode/guidance/reference/non-negotiable-rules.md`

## Template Usage Rules

Code-writing agents should:
- Preserve valid module boundaries
- Replace sample feature content with real content
- Reuse shared core, design system, navigation, build-logic modules
- Avoid collapsing modular template
- Remove `feature/sample` after first real feature (run `./scripts/remove-sample.sh`)

Should not: move logic to app shells, bypass shared infrastructure, introduce cross-feature `impl` coupling.

## Workflows

### Generation Workflow

1. `dream-general` reads product request → produces `.features/<feature>/plan.md` → presents plan to user → gets explicit confirmation
2. `dream-discovery` gathers codebase context
3. `dream-designer` produces UI spec
4. `dream-implementer` implements KMP business layer + tests + wiring
5. `dream-implementer-ui` implements UI layer (platform: both)
6. `dream-tester` runs verification; implementers repair until green/blocked/skip
7. `dream-inspector` reviews substantial work
8. `dream-general` writes report and retrospective

### UI Fix Workflow

1. `dream-general` interprets the UI issue → produces fix plan → user confirms.
2. `dream-tester` captures screenshot + element tree via mobile-mcp (Phase 1: Capture).
3. `dream-discovery` identifies affected UI source files (Phase 2: Diagnose).
4. `dream-implementer-ui` applies fix (Phase 3: Fix).
5. `dream-tester` re-captures screenshot + element tree for before/after comparison (Phase 4: Verify).
6. `dream-general` presents before/after evidence to user; repair loop until fixed or skip condition triggers.

**Scope**: UI layer only — Compose screens, SwiftUI views, ViewModels, UI state models. Never domain, data, or api.

### Bug Fix Workflow

1. `dream-general` interprets the bug or crash log → produces fix plan with suspected root-cause layer → user confirms.
2. `dream-tester` gathers diagnostics (crash logs, logcat, build output) + `dream-research` for error research (Phase 1: Diagnostics).
3. `dream-discovery` narrows to affected files; `dream-general` identifies failing layer (Phase 2: Root Cause).
4. Layer-appropriate implementer applies fix (Phase 3: Fix).
5. `dream-tester` runs targeted re-verification per fixed layer (Phase 4: Verify).
6. Repair loop until green, blocked, or skip condition triggers (max 5 iterations).

**Scope**: Any layer — domain, data, api, presentation, bridge, or wiring. Fix is minimal: fix the bug, don't refactor.

## Plan & Contract Rules

Plans must exist and be confirmed by the user before any implementation phase begins. Feature contracts must exist before implementation. See `.opencode/knowledge-graph/rules-condensed.md` for contract structure rules ([feat-1] through [feat-7]) and architecture rules ([arch-1] through [arch-5]).

Implementation must map each contract item to code and tests explicitly. Green commands alone are not sufficient stop conditions.

## Git Rules

See `.opencode/knowledge-graph/rules-condensed.md` for the full Git & Workflow rules ([git-1] through [git-7], [cmt-1] through [cmt-5]).

**Summary**: Create a feature branch after Phase 0, commit to the branch (not main), present merge summary to user, get explicit confirmation before squash-merging to main.

## Verification Rules

**Baseline**: `./gradlew allTests` + `./gradlew :androidApp:assembleDebug`

**Final validation**:
1. Android CLI: env check, emulator setup, APK install/launch, logs
2. `xcodebuild` + `xcrun simctl`: build, install, launch, logs
3. Maestro Android smoke flows per feature
4. Maestro iOS smoke flows per feature

**Status buckets**: `passed`, `blocked by environment`, `not run`, `not applicable`

**Maestro rules**: Feature-owned flows under `maestro/features/<feature>/`, `clearState` before `launchApp`, stable selectors, run after platform launch is healthy.

See `.opencode/guidance/reference/verification-commands.md` for commands.

## Repair Rules

1. `dream-general` identifies failing module
2. `dream-discovery` gathers context if needed
3. Relevant implementer loads graph rules, applies minimal fix
4. `dream-tester` reruns verification

Repair until: green, real blocker identified, behavior impossible, or skip condition triggers.

## Skip Conditions & Iteration Tracking

See `.opencode/guidance/reference/skip-conditions.md`

## Context Management

**Fresh subagent per round**: Each repair round uses fresh implementer invocation. Subagent reads state from `.features/`, not conversation history.

**State persistence**: Before delegating, write to `.features/<feature>/report.md`: iteration number, failures, past fixes, expected action, relevant rules.

**Context budget**: If conversation exceeds ~3 repair rounds, write state to `.features/`, start fresh conversation with compact handoff.

### Context Compaction Workflow

When context window approaches limit or after 3 repair rounds:

1. **Write state to file** `.features/<feature>/report.md`
2. **Create compact handoff prompt** with: iteration, what was tried, what fails, next module, relevant rules
3. **Start fresh conversation** with handoff prompt only

### Fresh Subagent Invocation

Each repair round must use a fresh subagent invocation. Subagent reads current state from `.features/` files, not conversation history. Pass iteration number and pointer to state file.

## Context Caching (Token Optimization)

**Tiered loading**: Agents use 3-tier context loading to minimize tokens:
- **Tier 1** (always loaded): Agent definition + condensed rules (`rules-condensed.md`)
- **Tier 2** (load before work): Layer-specific best-practice files
- **Tier 3** (load only if needed): Detailed files per `rules-index.md`

**Orchestrator caching**: `dream-general` should read frequently-needed files once and pass content to subagents in task prompts.

**Subagent handoff**: When launching subagents, include relevant file content in the task prompt instead of pointing to files.

**Condensed rules**: Use `.opencode/knowledge-graph/rules-condensed.md` as the default rules reference.

**Rules index**: Use `.opencode/knowledge-graph/rules-index.md` to find which file contains specific rules.

## Template Self-Improvement

Each feature captures retrospective at `.features/<feature>/retrospective.md`. Written automatically after every feature. Applied via `/dream-improve` command.

## Stop Conditions

Flow stops when: work implemented and verified, real blocker documented, skip condition triggered, or user changed task.

## Required Outputs

| File | Author | Template |
|------|--------|----------|
| `.features/<feature>/plan.md` | dream-general | `plan-template.md` |
| `.features/<feature>/contract.yaml` | dream-general | `feature-contract-template.md` |
| `.features/<feature>/spec.md` | dream-designer | `feature-spec-template.md` |
| `.features/<feature>/report.md` | dream-general | `feature-report-template.md` |
| `maestro/features/<feature>/android.yaml` | dream-tester | |
| `maestro/features/<feature>/ios.yaml` | dream-tester | |

Use templates from `.opencode/guidance/templates/` for reports. Plans must be user-approved before implementation proceeds.
