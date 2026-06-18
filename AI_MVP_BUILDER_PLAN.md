# Dream Plan And Tracker

Codename: `Dream`

## Role

This file is the operational tracker for Dream.

Use it to answer three questions quickly:

- what Dream is
- what is already done
- what still needs to improve next

Detailed architecture and generation rules live in:

- `guidance/`
- `knowledge-graph/`
- `templates/`
- `docs/cli-plan.md`
- `docs/first-try-report.md`

This file should stay compact, current, and task-oriented.

## Product

- Name: `Dream`
- Role: local AI MVP builder
- Scope: provide a CLI that can generate, verify, preview, review, and repair Kotlin Multiplatform MVPs from natural-language prompts
- Style: local-first, template-driven, agent-assisted, verification-gated

## Non-Negotiable Rules

- preserve thin app shells
- keep reusable infrastructure in shared modules
- place product behavior in feature modules
- enforce `Presentation -> Domain <- Data`
- keep domain pure Kotlin
- keep navigation centralized
- keep shared MVI primitives small
- keep shared user-visible strings in shared resources
- use desktop as the main fast preview and repair surface
- do not report success until verification is green and the generated feature matches its contract, or a blocker is clearly documented

## Current Baseline

Dream currently builds on:

- a knowledge graph under `knowledge-graph/`
- OpenCode-first guidance under `guidance/`
- a modular KMP base template at `templates/dream-kmp-base/`
- Dream-specific agents and commands under `.opencode/`

The next product step is the CLI.

Dream should become a usable local command-line tool before any backend generation work starts.

See `docs/cli-plan.md` for the current CLI plan.

## Current Workflow

1. User gives a plain product request.
2. Dream generates `.mvp/app-contract.yaml`.
3. Dream generates `.mvp/decisions.yaml`.
4. Dream instantiates the selected template.
5. Dream generates the first feature contract.
6. Dream implements the feature in modular form.
7. Dream runs verification.
8. Dream repairs failures until green or blocked.
9. Dream reviews the result against the contract and architecture rules.
10. Dream writes `.mvp/verification-report.md`.

## Required Generated Artifacts

- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- `.mvp/features/<feature>/contract.yaml`
- `.mvp/features/<feature>/test-plan.md`
- `.mvp/features/<feature>/implementation-report.md`
- `.mvp/verification-report.md`

## Required Verification

For local-only MVPs, Dream must run:

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:run
```

Green commands alone are not enough for final success.

Final success requires:

- verification commands passed or a precise environment blocker is documented
- contract behavior implemented
- required tests present for that behavior
- final review approved, or manual review recorded if delegated review tooling fails

## What We Finished

### Foundation

- created `knowledge-graph/` with schema, graph data, source chunks, product identity, and split best-practice sources
- removed the old monolithic `BEST_PRACTICES.md`
- created Dream guidance under `guidance/`
- created Dream agents and commands under `.opencode/`
- aligned Dream around OpenCode-first orchestration
- confirmed the modular base template at `templates/dream-kmp-base/`
- defined the direction to make Dream CLI-first before any backend work

### First Usable Flow

- Dream can now start from a plain request without requiring hand-written contracts
- the first `/dream-create` run succeeded end-to-end on an expense tracker prompt
- Dream generated all expected `.mvp/` artifacts
- Dream instantiated the template and replaced the sample feature
- Dream preserved centralized shell and modular feature layout
- Dream ran required verification commands successfully

## What We Learned From The First Run

The first run proved that Dream can generate a runnable project, but it also exposed quality gaps in the Dream tool itself.

### What Worked

- plain-prompt project generation worked
- contract and report files were created
- template instantiation worked
- modular feature generation worked
- Android build passed
- shared and feature tests passed
- desktop app launched

### What Failed Or Was Weak

1. Success criteria were too weak.
- Dream reported a strong result from passing verification, but the generated feature still had contract gaps.

2. Contract-to-implementation parity was not enforced tightly enough.
- Contract states existed on paper, but not all of them were modeled in code.
- Required behaviors in the contract and test plan were not fully covered by tests.

3. Package and identity normalization was incomplete.
- App-specific package naming was wrong.
- Template identity and generated app identity were not normalized strictly enough.

4. Time behavior rules were not enforced during generation.
- The generated feature hardcoded a logical month instead of using a real time abstraction.

5. Composition-root rules were too weak.
- Repositories and ViewModels were created inside a composable body, which risks unstable state and recomposition bugs.

6. UI quality was below expectation.
- The project was structurally valid, but the produced UI was not good enough.
- Dream currently optimizes more for structure than for product-level UX quality.

7. Delegated QA failed at the tool level.
- `dream-inspector` could not complete because of a task/session database failure.
- Dream attempted the QA gate, but could not rely on it.

## Dream Tooling Gaps To Fix

### High Priority

1. Tighten final success rules.
- Do not mark a project `validated` from build success alone.
- Require contract coverage, test coverage, and final review status.

2. Require explicit contract mapping during implementation.
- Each contract action, state, and required test must map to code and tests.
- Do not generate placeholder states unless they will be implemented.

3. Strengthen project instantiation rules.
- Derive canonical package root from app identity.
- Normalize package names, namespaces, application ids, and visible app identity.
- Report all renamed identifiers explicitly.

4. Enforce stable composition roots.
- Do not instantiate repositories, use cases, or ViewModels directly in recomposing composables.
- Keep assembly in a stable root using DI or stable remembered construction.

5. Enforce time abstraction rules in generated features.
- User-visible time behavior must flow through `TimeProvider`.
- Do not hardcode logical dates or months.

6. Raise UI quality expectations.
- Add minimum UX and visual quality requirements to Dream generation.
- Include empty, error, and content states that feel intentional, not placeholder.

7. Handle inspector failure safely.
- If delegated review tooling fails, Dream must record manual review fallback and avoid overstating final approval.

### Medium Priority

1. Improve generated test-plan fidelity.
- Make sure the generated `test-plan.md` matches the actual required contract behaviors.

2. Improve generated implementation reports.
- Require a section that states which contract requirements are fully implemented, partial, or deferred.

3. Add a first-run regression checklist.
- Re-run the same expense-tracker flow after each major Dream prompt or rule update.

4. Define the CLI surface clearly.
- Lock the first commands, arguments, expected outputs, and report locations before adding more generation scope.

## File Areas To Update Next

### Guidance

- `guidance/prompts/feature-implementation.md`
- `guidance/prompts/feature-contract-generator.md`
- `guidance/prompts/project-instantiation.md`
- `guidance/prompts/verification-and-repair.md`
- `guidance/prompts/app-contract-generator.md`

### OpenCode Commands And Agents

- `.opencode/commands/dream-create.md`
- `.opencode/commands/dream-review.md`
- `.opencode/commands/dream-verify.md`
- `.opencode/agents/dream-general.md`
- `.opencode/agents/dream-inspector.md`

### Knowledge Graph Rules To Reinforce In Retrieval

- time practices
- dependency injection and composition root practices
- presentation-state modeling
- UI quality and preview-state expectations
- contract and testing alignment

## Status

### Done

- Dream codename and product identity established
- knowledge graph created and split into smaller source files
- guidance layer created
- OpenCode Dream commands and agents created
- base template confirmed and used
- first plain-prompt creation flow completed successfully once

### In Progress

- converting Dream from a structurally correct generator into a reliably reviewable and higher-quality generator
- tightening prompts and success gates based on first-run findings

### Not Started

- Dream CLI shell and command surface
- formal regression checklist for repeated Dream smoke tests

Backend generation is intentionally out of scope for now.

## Next Tasks

1. Tighten Dream success criteria and verification-report rules.
2. Tighten feature implementation prompt around contract parity, time rules, and stable composition roots.
3. Tighten project instantiation prompt around app identity and package normalization.
4. Add minimum UI quality expectations to contract and implementation prompts.
5. Define fallback behavior when `dream-inspector` tooling fails.
6. Create the Dream CLI plan and first command set.
7. Re-run the expense tracker smoke test after those updates.
8. Document the second-run results in this file and in a dedicated run report.

## Working Rule For This File

Keep this file short enough to maintain by hand.

When Dream changes, update:

- `What We Finished`
- `What We Learned From The First Run` or later run notes
- `Status`
- `Next Tasks`

Do not turn this file back into a giant architecture essay.
Use `guidance/` and `knowledge-graph/` for detailed rules.
