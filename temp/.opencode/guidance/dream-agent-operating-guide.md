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
| Discovery | `dream-discovery` | Fast codebase search + focused context packs |
| Research | `dream-search` | External docs, troubleshooting |
| Design | `dream-designer` | UI/UX specs (no code) |
| KMP Business | `dream-implementer` | Domain, data, api, wiring |
| Android UI | `dream-implementer-android` | Compose screens, ViewModels, Koin nav |
| iOS UI | `dream-implementer-ios` | SwiftUI views, ViewModels, bridge, XCTest |
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

1. `dream-general` reads product request → infers backend/storage → produces contracts
2. `dream-discovery` gathers context, `dream-search` for external research (if needed)
3. `dream-designer` produces UI spec
4. `dream-implementer` implements KMP business layer + tests + wiring
5. `dream-implementer-android` + `dream-implementer-ios` implement platform UI
6. `dream-tester` runs verification; implementers repair until green/blocked/skip
7. `dream-inspector` reviews substantial work
8. `dream-general` writes report and retrospective

## Contract Rules

Feature contracts must exist before implementation. Every contract defines: feature name, goal, entities/fields (with timestamp names), actions/inputs, screens, loading/empty/content/error states, time-sensitive behavior (using `TimeProvider`), design expectations, required tests by layer.

Implementation must map each contract item to code and tests explicitly. Green commands alone are not sufficient stop conditions.

**Key rules**:
- Koin compiler plugin DSL for DI (no `@KoinViewModel`, `@Module`, `@ComponentScan`)
- `viewModel<T>()` syntax (not `viewModel { }` lambda)
- Navigation: `NavKey` routes in `feature/<name>/api`, screens in `feature/<name>/impl`
- `TimeProvider` for all time behavior (no direct `Clock.System.now()`)
- Design-system components over raw Material3

## Git Rules

Before flow depends on git, verify repo is initialized. Required flow: `git status`, `git diff`, `git log --oneline -10`, stage intended files, propose commit message.

Do not commit when: verification blocked, review not approved, reports missing, user hasn't approved, unrelated changes present.

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
| `.features/<feature>/contract.yaml` | dream-general | `feature-contract-template.md` |
| `.features/<feature>/spec.md` | dream-designer | `feature-spec-template.md` |
| `.features/<feature>/report.md` | dream-general | `feature-report-template.md` |
| `maestro/features/<feature>/android.yaml` | dream-tester | |
| `maestro/features/<feature>/ios.yaml` | dream-tester | |

Use templates from `.opencode/guidance/templates/` for reports.
