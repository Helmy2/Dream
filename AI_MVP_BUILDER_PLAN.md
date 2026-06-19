# Dream Plan And Tracker

Codename: `Dream`

## Role

This file is the operational tracker for Dream.

Use it to answer three questions quickly:

- what Dream is
- what is already done
- what still needs to improve next

Detailed architecture and generation rules live in:

- `temp/.opencode/guidance/`
- `temp/.knowledge-graph/`
- `temp/`
- `docs/first-try-report.md`

This file should stay compact, current, and task-oriented.

## Product

- Name: `Dream`
- Role: local AI MVP builder
- Scope: provide an OpenCode-first workflow that can generate, verify, preview, review, and repair Kotlin Multiplatform MVPs from natural-language prompts
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
- treat Android, iOS, and contract-selected Desktop as product runtime targets
- use Android CLI and iOS command-line tooling for platform install, launch, logs, and repair loops
- use Maestro for black-box smoke and end-to-end user-flow checks after platform launch is healthy
- support Desktop when the generated app contract includes it; when Desktop is run interactively, use `:desktopApp:hotRunJvm`
- do not report success until verification is green and the generated feature matches its contract, or a blocker is clearly documented

## Current Baseline

Dream currently builds on:

- a canonical starter project under `temp/`
- a packaged knowledge graph under `temp/.knowledge-graph/`
- packaged OpenCode guidance under `temp/.opencode/guidance/`
- packaged Dream agents and commands under `temp/.opencode/`

The near-term product step is finishing the initializer packaging workflow and regression loop.

Dream should remain OpenCode-first. The initializer should only copy `temp/`, rename it, apply deterministic identity rewrites, and tell the user to open the generated project in OpenCode.

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

For local-only MVPs, Dream must validate the real product targets before treating a generated app as successful.

Required baseline:

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
```

Required platform and smoke ladder:

1. Run shared, feature, Koin graph, and platform unit tests.
2. Build Android with Gradle.
3. Use Android CLI (`https://developer.android.com/tools/agents/android-cli`) for Android environment checks, emulator/device management, APK install, app launch, layout/screenshot inspection, and log capture.
4. Build iOS with `xcodebuild`.
5. Use `xcrun simctl` to boot a simulator, install the app, launch it, and capture logs or crash output.
6. Run Maestro Android smoke flows for user-visible behavior after Android launches cleanly.
7. Run Maestro iOS smoke flows where reliable after iOS launches cleanly.
8. When `desktop` is included in the generated app contract, build Desktop with `./gradlew :desktopApp:compileKotlinJvm` and run it interactively with `./gradlew :desktopApp:hotRunJvm`.

Green commands alone are not enough for final success.

Final success requires:

- verification commands passed or a precise environment blocker is documented
- Android install, launch, and log inspection completed through Android CLI or a documented blocker exists
- iOS build, simulator install, launch, and log inspection completed through `xcodebuild`/`xcrun simctl` or a documented blocker exists
- Maestro smoke coverage completed for the relevant platform flows, or a precise tooling/platform blocker is documented
- Desktop compile and `hotRunJvm` validation completed when `desktop` is in the app contract, or a precise blocker is documented
- contract behavior implemented
- required tests present for that behavior
- final review approved, or manual review recorded if delegated review tooling fails

## What We Finished

### Foundation

- created `.knowledge-graph/` with schema, graph data, source chunks, product identity, and split best-practice sources
- removed the old monolithic `BEST_PRACTICES.md`
- created Dream guidance under `.opencode/guidance/`
- created Dream agents and commands under `.opencode/`
- aligned Dream around OpenCode-first orchestration
- consolidated the modular KMP starter under root `temp/` as the initializer copy source
- defined the direction to make Dream OpenCode-first before any backend work

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
- desktop app launched during the first run, and Desktop should use `:desktopApp:hotRunJvm` for interactive validation when it is a generated app target
- sample scaffolding must not remain as a live feature module once the first real feature is generated in the target app

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
- Treat identity normalization as required completion criteria for project instantiation, not a later cleanup pass.
- Do not allow generated projects to keep template identity in package-defining or user-facing app surfaces unless a blocker is documented.

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

4. Define the initializer rewrite surface clearly.
- Lock the identity rewrite files, expected `.mvp/` outputs, OpenCode handoff behavior, verification behavior, and report locations before adding more generation scope.

## File Areas To Update Next

### Guidance

- `temp/.opencode/guidance/prompts/feature-implementation.md`
- `temp/.opencode/guidance/prompts/feature-contract-generator.md`
- `temp/.opencode/guidance/prompts/project-instantiation.md`
- `temp/.opencode/guidance/prompts/verification-and-repair.md`
- `temp/.opencode/guidance/prompts/app-contract-generator.md`

### OpenCode Commands And Agents

- `temp/.opencode/commands/dream-create.md`
- `temp/.opencode/commands/dream-review.md`
- `temp/.opencode/commands/dream-verify.md`
- `temp/.opencode/agents/dream-general.md`
- `temp/.opencode/agents/dream-inspector.md`

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

- initializer CLI copy-and-rename tool
- formal regression checklist for repeated Dream smoke tests

Backend generation is intentionally out of scope for now.

## Next Tasks

1. Tighten Dream success criteria and verification-report rules.
2. Tighten feature implementation prompt around contract parity, time rules, and stable composition roots.
3. Tighten project instantiation prompt around app identity and package normalization.
4. Add minimum UI quality expectations to contract and implementation prompts.
5. Define fallback behavior when `dream-inspector` tooling fails.
6. Implement the initializer rewrite surface for copying `temp/` into a new app.
7. Re-run the expense tracker smoke test through OpenCode after those updates.
8. Document the second-run results in this file and in a dedicated run report.

## Working Rule For This File

Keep this file short enough to maintain by hand.

When Dream changes, update:

- `What We Finished`
- `What We Learned From The First Run` or later run notes
- `Status`
- `Next Tasks`

Do not turn this file back into a giant architecture essay.
Use `temp/.opencode/guidance/` and `temp/.knowledge-graph/` for detailed rules.
