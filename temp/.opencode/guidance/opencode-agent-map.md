# Dream OpenCode Agent Map

## Purpose

This document defines which OpenCode agent should own each major Dream workflow stage.

Dream should be OpenCode-agent-first.
That means workflow guidance should be written around real OpenCode agent responsibilities, not abstract agent roles.

## Available OpenCode Agents

### Orchestrator

- `general` / `dream-general`: Primary orchestrator — interprets requests, breaks work into phases, delegates to subagents, manages context, and reports final status.

### Read-Only Subagents

- `dream-explore`: Fast codebase discovery and targeted searching — locates modules, files, symbols, and rule paths.
- `dream-context-scout`: Focused read-only context pack creation — gathers the smallest high-signal set of files and rules before a task.
- `dream-search`: External technical research — library docs, API references, dependency compatibility, error troubleshooting.

### Spec-Generation Subagent

- `dream-designer`: Translates feature contracts into cross-platform UI/UX specs (`.features/<feature>/ui-spec.md`). Writes spec files, not code.

### Code-Writing Subagents

- `dream-implementer`: KMP business foundation — domain entities, repository interfaces, data mappings, API route types. No UI.
- `dream-implementer-android`: Jetpack Compose presentation layer — screens, ViewModels, UI state, Koin navigation providers.
- `dream-implementer-ios`: Native SwiftUI layer — views, ViewModels, models, Swift-to-Kotlin bridge, XCTest coverage.

### Verification Subagent

- `dream-tester`: Full verification ladder — Gradle tests, platform builds, device install/launch, log inspection, Maestro smoke flows.

### Review Subagent

- `dream-inspector`: Final QA and architecture review gate — contract parity, anti-patterns, test coverage, leftover sample artifacts.

## Default Ownership

### `general` / `dream-general`

Owns:

- overall Dream workflow orchestration
- deciding which subagent to call next
- contract-generation coordination
- verification-loop coordination
- final status reporting

Should do:

- interpret the user request
- decide whether backend is needed
- decide whether more context gathering is required
- launch the appropriate subagent for each phase: `dream-context-scout`, `dream-explore`, `dream-search`, `dream-designer`, `dream-implementer`, `dream-implementer-android`, `dream-implementer-ios`, `dream-tester`, or `dream-inspector`
- ensure that Dream does not report validated success before verification is green, contract parity is checked, required tests are present, and review status is documented

Should not do by default:

- large direct code edits when a specialized implementer should own them
- final QA signoff without `dream-inspector` for substantial generation or repair work
- run verification directly — delegate to `dream-tester`

### `dream-explore`

Owns:

- fast project and template discovery
- locating relevant modules, files, and symbols
- locating architecture rules by topic

Use when:

- the orchestrator needs to know where something lives
- the repair loop needs to find all affected files quickly
- a prompt needs targeted codebase discovery without deep planning

Expected output:

- concise path list
- exact matches
- minimal explanation of what was found

### `dream-context-scout`

Owns:

- focused read-only context packs before implementation or repair

Use when:

- a feature implementation needs a precise set of relevant files
- a repair needs exact surrounding context
- the template or target project is large enough that a focused read pack is better than ad hoc searching

Expected output:

- relevant paths
- small structural summary
- key snippets or file roles

### `dream-search`

Owns:

- external technical research — library documentation, API references, dependency compatibility, error troubleshooting

Use when:

- the project needs to integrate an unfamiliar library
- a cryptic build or runtime error needs diagnosis
- framework or API upgrade research is required
- the team needs authoritative patterns from official documentation

Expected output:

- concise summary of findings
- API patterns or code snippets with source references
- platform-specific caveats

Input scope:

- external URLs, dependency names, error messages
- never receives the full project codebase — only relevant context from the orchestrator

### `dream-designer`

Owns:

- UI/UX specification generation from feature contracts
- `.features/<feature>/ui-spec.md` — view states, design-system components, layout structure, interactions

Use when:

- a feature contract exists and platform UI implementation is about to start
- the orchestrator needs a shared visual spec for both Android and iOS implementers

Expected output:

- `.features/<feature>/ui-spec.md` with Loading, Empty, Content, Error state definitions
- component references using shared Dream design-system names and tokens
- layout structure and interaction definitions

Constraints:

- does not write any code
- does not reference platform-specific APIs
- enforces design-system component reuse over raw primitives

### `dream-implementer`

Owns:

- KMP business foundation: `feature/<name>/domain`, `feature/<name>/data`, `feature/<name>/api`
- domain entities, repository interfaces, data source implementations, DTOs and mappers
- NavKey route types in api
- domain, data, and api test coverage

Use when:

- the KMP business layer for a feature needs to be created or repaired
- entity definitions, repository contracts, or data mappings need changes

Must preserve:

- `Presentation -> Domain <- Data`
- thin app shells
- centralized navigation
- shared infrastructure boundaries
- pure Kotlin domain — no platform dependencies

Must not:

- write any UI code — delegate Compose work to `dream-implementer-android` and SwiftUI work to `dream-implementer-ios`
- modify `feature/<name>/impl` or `iosApp-swiftui/`

### `dream-implementer-android`

Owns:

- Jetpack Compose presentation layer: `feature/<name>/impl`
- screens, ViewModels, UI state/actions/events
- Koin compiler plugin DSL navigation entry providers
- Compose previews and presentation-layer tests

Use when:

- Compose screens, ViewModels, or UI state mappings need to be created or updated

Must:

- read the UI spec from `.features/<feature>/ui-spec.md` before implementing
- use Dream design-system components and tokens over raw Material3 primitives
- implement every view state: Loading, Empty, Content, Error

Must not:

- modify domain, data, or api modules
- modify iOS files

### `dream-implementer-ios`

Owns:

- native iOS SwiftUI presentation layer: `iosApp-swiftui/`
- SwiftUI views, `@Observable` ViewModels, Swift models
- Swift-to-Kotlin bridge protocol in `Core/`
- XCTest coverage in `iosAppTests/`

Use when:

- SwiftUI views, ViewModels, or bridge code need to be created or updated

Must:

- read the UI spec from `.features/<feature>/ui-spec.md` before implementing
- keep the Swift-to-Kotlin bridge narrow, protocol-based, and in `Core/`
- implement every view state: Loading, Empty, Content, Error

Must not:

- modify Kotlin source files
- modify shared domain or data logic

### `dream-tester`

Owns:

- the full verification ladder: Gradle unit tests, platform builds, device install/launch, log inspection, Maestro smoke flows
- Maestro flow definitions under `maestro/features/`

Use when:

- verification is needed after implementation or repair
- the orchestrator needs green/blocked status before reporting success

Expected output:

- results with explicit status buckets: `passed`, `blocked by environment`, `not run`, `not applicable`
- clear failure logs and error messages
- generated Maestro flows for new features

Must:

- run environment preflights (Android SDK, git, Maestro) before the verification loop
- install and launch APK on running emulator after Android build
- install and launch on iOS simulator after xcodebuild
- inspect platform logs for crashes

Must not:

- modify feature implementation code
- claim green status when any check is `blocked by environment` or `not run`

### `dream-inspector`

Owns:

- architecture and behavior review
- final QA gate before Dream reports validated success

Use when:

- a substantial feature has been generated
- a repair loop claims green status
- the orchestrator needs an explicit pass or defect list

Expected output:

- exactly `APPROVED` when clean
- otherwise an itemized defect list

## Default Workflow Map

### Project Creation

1. `general` interprets the request.
2. `dream-explore` or `dream-context-scout` gathers relevant template and graph context if needed.
3. `general` generates or coordinates `.features/app-contract.yaml` and decisions.
4. `dream-implementer` instantiates the selected Dream template.
5. `dream-implementer` replaces or prepares sample feature scaffolding.
6. when the first real feature is present, `dream-implementer` removes `feature/sample` from the generated app unless a documented temporary exception remains.

### Feature Generation

1. `general` selects the feature to generate.
2. `dream-context-scout` gathers the exact template and feature context.
3. `general` produces or refines the feature contract.
4. `dream-search` may be called if external research is needed for dependencies or APIs.
5. `dream-designer` produces `.features/<feature>/ui-spec.md` from the contract.
6. `dream-implementer` generates the KMP business layer: domain, data, api, and their tests.
7. `dream-implementer-android` generates the Jetpack Compose UI layer: screens, ViewModels, Koin nav, tests.
8. `dream-implementer-ios` generates the native SwiftUI layer: views, ViewModels, bridge, XCTest.
9. `dream-tester` runs the verification ladder: unit tests, platform builds, device install/launch, log inspection, Maestro flows.
10. `dream-inspector` reviews if the change is substantial.

### Verification And Repair

1. `general` selects the failing surface and sets iteration context.
2. `dream-explore` or `dream-context-scout` narrows the failure surface when needed.
3. `dream-implementer`, `dream-implementer-android`, or `dream-implementer-ios` applies the smallest correct fix depending on the failure layer.
4. `dream-tester` reruns the verification ladder.
5. `dream-inspector` acts as the final review gate before validated status is reported.

If `dream-inspector` fails at the tool level, `general` must run a manual review fallback and avoid reporting validated status from verification results alone.

## Required Inputs By Agent

### `general` / `dream-general`

- user request
- `AGENTS.md`
- `.opencode/knowledge-graph/PRODUCT.md`
- `dream-agent-operating-guide.md`
- relevant `.features/` metadata

### `dream-explore`

- targeted search question
- path scope if known
- optional module or layer constraint

### `dream-context-scout`

- exact implementation or repair objective
- target module or feature if known
- any already-identified files

### `dream-search`

- research question or problem statement
- library/dependency names or error messages
- relevant context from the project (not full codebase)

### `dream-designer`

- feature contract from `.features/<feature>/contract.yaml`
- design system component catalog reference
- target platforms (Android, iOS)

### `dream-implementer`

- approved objective
- feature contract
- architecture constraints
- verification expectations

### `dream-implementer-android`

- approved objective
- UI spec from `.features/<feature>/ui-spec.md`
- feature contract (domain entities, actions, states)
- architecture constraints (Compose patterns, design-system components)
- verification expectations

### `dream-implementer-ios`

- approved objective
- UI spec from `.features/<feature>/ui-spec.md`
- feature contract (domain entities, actions, states)
- iOS best-practice files (`35` through `38`)
- verification expectations

### `dream-tester`

- verification objective (new feature, repair round)
- target platforms (Android, iOS, or both)
- feature names for Maestro flow generation
- iteration number and past failure context from `.features/verification-report.md`

### `dream-inspector`

- stated objective
- changed files or affected modules
- architecture rules and contract expectations
- verification status

## Handoff Rules

- `general` should hand off discovery to `dream-explore` or `dream-context-scout` instead of doing broad discovery itself
- `general` should hand off external research to `dream-search` instead of using ad hoc web searches
- `general` should hand off UI specification to `dream-designer` before platform implementation begins
- `general` should hand off KMP business layer code to `dream-implementer`
- `general` should hand off Compose UI code to `dream-implementer-android`
- `general` should hand off SwiftUI code to `dream-implementer-ios`
- `general` should hand off verification to `dream-tester` instead of running it directly
- `general` should use `dream-inspector` before reporting validated success for substantial work
- `dream-implementer` should not redefine architecture rules; it should apply the rules already established by Dream guidance and the graph
- `dream-implementer-android` and `dream-implementer-ios` should use the UI spec from `dream-designer` as their single source of truth for visual behavior
- `dream-inspector` should judge against the contract, architecture rules, ui-spec, and verification status, not personal style preferences
