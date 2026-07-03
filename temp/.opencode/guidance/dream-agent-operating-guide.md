# Dream OpenCode Operating Guide

## Purpose

Defines default behavior for OpenCode agents generating, reviewing, verifying, or repairing Dream projects.

**Use with**: `opencode-agent-map.md`, `PRODUCT.md`, `README.md`, `best-practices/`, `AGENTS.md`, `.features/` metadata.

## Required Read Order

1. `.opencode/knowledge-graph/PRODUCT.md`
2. `.opencode/guidance/opencode-agent-map.md`
3. `AGENTS.md`
4. `.opencode/knowledge-graph/README.md`
5. Relevant `best-practices/*.md` for current concern
6. Selected template README
7. Target project `.features/` metadata

## Default Mission

Produce KMP MVPs that are: runnable, verifiable, readable, repairable, aligned with Dream architecture.

Optimize for correct generation + future repairability, not just speed.

## Agent Role Model

| Role | Agent | Owns |
|------|-------|------|
| Orchestrator | `dream-general` | Workflow, contracts, delegation, verification coordination |
| Research | `dream-research` | Fast codebase search + external research + focused context packs |
| Design | `dream-designer` | UI/UX specs (no code) |
| KMP Business | `dream-implementer` | Domain, data, api, wiring |
| UI Implementation | `dream-implementer-ui` | Cross-platform UI — Compose (Android) + SwiftUI (iOS) |
| Verification | `dream-tester` | Tests, builds, device, Maestro |
| Review | `dream-inspector` | Final QA gate |

**Rule**: Use smallest capable agent per subtask. `dream-general` must not edit implementation files directly.

## Substantial Work Threshold

`dream-inspector` review required for:
- New feature generation
- Repair changing >2 files
- Changes to shared modules (`shared/core/*`, `shared/designsystem`, `shared/navigation`, `shared/umbrella-*`)
- Changes to contracts, specs, or architecture rules

Optional for: single-line corrections, documentation updates, typo fixes.

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

## Generation Workflow

1. `dream-general` reads product request → produces `.features/<feature>/plan.md` → presents plan to user → gets explicit confirmation
2. `dream-research` gathers context, external research (if needed)
3. `dream-designer` produces UI spec
4. `dream-implementer` implements KMP business layer + tests + wiring
5. `dream-implementer-ui` implements UI layer (platform: both)
6. `dream-tester` runs verification; implementers repair until green/blocked/skip
7. `dream-inspector` reviews substantial work
8. `dream-general` writes report and retrospective

## UI Fix Workflow

1. `dream-general` interprets the UI issue → produces fix plan → user confirms.
2. `dream-tester` captures screenshot + element tree via mobile-mcp (Phase 1: Capture).
3. `dream-research` identifies affected UI source files (Phase 2: Diagnose).
4. `dream-implementer-ui` applies fix (Phase 3: Fix).
5. `dream-tester` re-captures screenshot + element tree for before/after comparison (Phase 4: Verify).
6. `dream-general` presents before/after evidence to user; repair loop until fixed or skip condition triggers.

**Scope**: UI layer only — Compose screens, SwiftUI views, ViewModels, UI state models. Never domain, data, or api.

## Bug Fix Workflow

1. `dream-general` interprets the bug or crash log → produces fix plan with suspected root-cause layer → user confirms.
2. `dream-tester` gathers diagnostics (crash logs, logcat, build output) + `dream-research` for error research (Phase 1: Diagnostics).
3. `dream-research` narrows to affected files; `dream-general` identifies failing layer (Phase 2: Root Cause).
4. Layer-appropriate implementer applies fix (Phase 3: Fix).
5. `dream-tester` runs targeted re-verification per fixed layer (Phase 4: Verify).
6. Repair loop until green, blocked, or skip condition triggers (max 5 iterations).

**Scope**: Any layer — domain, data, api, presentation, bridge, or wiring. Fix is minimal: fix the bug, don't refactor.

## Plan Rules

Plans must exist and be confirmed by the user before any implementation phase begins. Every plan defines: feature summary, modules to create/modify, screens with states, architecture decisions, dependencies, and phased implementation order.

The plan file `.features/<feature>/plan.md` is the user-facing contract for "what Dream will do." It must be written in Phase 0 and approved before Phase 1.

## Contract Rules

Feature contracts must exist before implementation. See `.opencode/knowledge-graph/rules-condensed.md` for contract structure rules ([feat-1] through [feat-7]) and architecture rules ([arch-1] through [arch-5]).

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
2. `dream-research` gathers context if needed
3. Relevant implementer loads graph rules, applies minimal fix
4. `dream-tester` reruns verification

Repair until: green, real blocker identified, behavior impossible, or skip condition triggers.

## Git Branching Strategy

See `.opencode/knowledge-graph/rules-condensed.md` for full rules ([git-1] through [git-7]).

### Branch Naming

| Workflow | Branch Pattern | Example |
|----------|---------------|---------|
| Feature | `dream/feature/<name>` | `dream/feature/expense-tracker` |
| UI Fix | `dream/ui-fix/<desc>` | `dream/ui-fix/card-spacing` |
| Bug Fix | `dream/bug-fix/<desc>` | `dream/bug-fix/crash-on-empty` |
| Repair | `dream/repair/<desc>` | `dream/repair/di-resolution` |

### Workflow

1. After Phase 0 (plan confirmed), create feature branch from main
2. All commits go to the feature branch — never directly to main
3. After verification + review are green, present merge summary to user
4. User explicitly confirms: **merge? edit message? deny?**
5. On confirm: squash merge to main, delete feature branch
6. On deny: keep branch, document reason in report

## Phase Checkpoints

See `.opencode/knowledge-graph/rules-condensed.md` for full rules ([ckpt-1] through [ckpt-4]).

After each phase completes successfully, append to `.features/<feature>/checkpoints.md`:

```markdown
## Phase <N>: <name>
- Status: completed
- Git hash: <hash>
- Timestamp: <ISO-8601>
- Context usage: ~<N>% (estimated)
- Notes: <what was accomplished>
```

If a later phase fails, offer to roll back to the last good checkpoint via `git checkout <hash>`.

## Inspector Repair Loop

See `.opencode/knowledge-graph/rules-condensed.md` for full rules ([insp-1] through [insp-5]).

When `dream-inspector` returns an itemized defect list:

1. Present defects to user with severity: **critical** / **warning** / **info**
2. Ask: **repair? accept with defects? deny?**
3. On **repair**: delegate to appropriate implementer, re-run inspector
4. On **accept**: document defects in report, proceed to merge
5. On **deny**: stop workflow, document reason
6. Max 2 inspector repair rounds — if still failing after 2 rounds, escalate to user

## Skip Conditions & Iteration Tracking

See `.opencode/guidance/reference/skip-conditions.md`

## Context Management

**Fresh subagent per round**: Each repair round uses fresh implementer invocation. Subagent reads state from `.features/`, not conversation history.

**State persistence**: Before delegating, write to `.features/<feature>/report.md`: iteration number, failures, past fixes, expected action, relevant rules.

**Context budget**: If conversation exceeds ~3 repair rounds, write state to `.features/`, start fresh conversation with compact handoff (iteration, what was tried, what fails, next module).

### Context Compaction Workflow

When context window approaches limit or after 3 repair rounds:

1. **Write state to file** `.features/<feature>/report.md` — include iteration number, failures, past fixes, next action.
2. **Create compact handoff prompt** with: iteration, what was tried, what fails, next module, relevant rules, pointer to full state file.
3. **Start fresh conversation** with handoff prompt only — do not include full tool-call history.

### Fresh Subagent Invocation

Each repair round must use a fresh subagent invocation. Subagent reads current state from `.features/` files, not conversation history. Pass iteration number and pointer to state file. Include relevant rules in task prompt, not entire knowledge graph.

## Context Caching (Token Optimization)

**Tiered loading**: Agents use 3-tier context loading to minimize tokens:
- **Tier 1** (always loaded): Agent definition + condensed rules (`rules-condensed.md`)
- **Tier 2** (load before work): Layer-specific best-practice files
- **Tier 3** (load only if needed): Detailed files per `rules-index.md`

**Orchestrator caching**: `dream-general` should read frequently-needed files once and pass content to subagents in task prompts:
- `AGENTS.md`
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/knowledge-graph/rules-condensed.md`
- Current feature contract
- Current verification report

**Subagent handoff**: When launching subagents, include relevant file content in the task prompt instead of pointing to files. This avoids redundant reads.

**Condensed rules**: Use `.opencode/knowledge-graph/rules-condensed.md` as the default rules reference. Load specific best-practice files only when the task requires detailed guidance.

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
