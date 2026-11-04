# Dream Onboarding

Dream is a local-first AI production-app builder for Kotlin Multiplatform apps. It packages a reusable starter project, OpenCode agents, architecture guidance, verification rules, and smoke-test scaffolding so a natural-language product request can become a modular Android and iOS app.

Use this document when you are new to the repository and need to understand how Dream is built, how the workflow runs, and why the template is shaped this way.

## Mental Model

Dream is not a single generated app. Dream is the tool and starter baseline that creates generated apps.

The repository centers on `temp/`, which is the canonical starter project copied by the initializer. Generated app work should happen in `temp/` or in a copy created from `temp/`, not in unrelated root-level surfaces.

**Build system: Kotlin Toolchain (Alpha).** The starter builds with the JetBrains Kotlin Toolchain (`./kotlin` CLI, YAML `project.yaml` / `module.yaml`, v0.13) instead of Gradle. iOS is native SwiftUI only (`iosApp/`); Android UI is Jetpack Compose in Android-only modules. Not supported by Toolchain 0.13: SKIE, Swift export, and the Xcode 27 JSON project format. The Xcode project has an `app` target plus a hosted `appTests` unit-test target (Swift tests in `iosApp/test`, run via `xcodebuild test`; `./kotlin test` runs Kotlin tests only). See `temp/.opencode/guidance/reference/kotlin-toolchain.md` for commands, layout, module names, and iOS/Android rules.

```mermaid
flowchart TD
    User[User product request] --> Dream[Dream workflow]
    Dream --> Contract[.features contracts and decisions]
    Dream --> Template[temp starter copy]
    Template --> Generated[Generated KMP app]
    Contract --> Generated
    Generated --> Verify[Tests, builds, platform launch, smoke flows]
    Verify --> Report[specs/verification-report.md]
```

## Repository Map

- `dream-init`: initializer entry point that copies and renames `temp/`.
- `AGENTS.md`: repository-level working rules for agents and contributors.
- `ONBOARDING.md`: this entry-level guide.
- `temp/`: canonical Kotlin Multiplatform starter project.
- `temp/.opencode/`: packaged OpenCode config, agents (7 agents: dream-general, dream-research, dream-designer, dream-implementer, dream-implementer-ui, dream-tester, dream-inspector), commands, and prompt guidance.
- `temp/.opencode/knowledge-graph/`: reusable architecture rules and retrieval sources.
- `temp/specs/`: generated app contracts, feature specs, decisions, reports, and retrospectives.
- `temp/maestro/`: Android and iOS smoke-flow scaffolding.
- `temp/.opencode/scripts/dream-preflight.sh`: local readiness check for git, Android SDK, and tooling.
- `temp/.opencode/scripts/sync-pbxproj.rb`: obsolete under the Kotlin Toolchain (the Xcode project uses a file-system-synchronized folder, so new Swift files are picked up automatically).
- `temp/.opencode/scripts/remove-sample.sh`: removes sample feature files, strips sample deps, and checks for leftovers.
- `temp/.opencode/scripts/start-codebase-memory-mcp.sh`: launches codebase-memory-mcp binary for code intelligence via persistent knowledge graph.
- `temp/.cbmignore`: excludes files from codebase-memory-mcp indexing (build artifacts, generated files, Dream metadata).

```mermaid
flowchart LR
    Root[Dream repo root] --> Init[dream-init]
    Root --> Temp[temp/ canonical starter]
    Temp --> Apps[androidApp / iosApp]
    Temp --> Shared[shared/*]
    Temp --> Features[feature/*]
    Temp --> Agents[.opencode agents and commands]
    Temp --> Graph[.opencode/knowledge-graph rules]
    Temp --> MVP[.features generated metadata]
    Temp --> Smoke[maestro smoke flows]
```

## Why `temp/` Exists

`temp/` is a complete starter app plus the agent layer needed to evolve it. The initializer should only copy `temp/`, rename it, apply deterministic identity rewrites, and hand the result to OpenCode. Feature generation, repair, verification, and review happen after the copy exists.

This keeps Dream local-first and reproducible:

- starter structure is versioned in one place
- generated apps inherit the same architecture rules
- agents and commands travel with the generated project
- verification expectations are available inside the generated project

## Generation Workflow

Dream follows a contract-first flow. Code should not be generated before the relevant contract exists.

```mermaid
sequenceDiagram
    participant User
    participant General as dream-general
    participant Research as dream-research
    participant Designer as dream-designer
    participant Impl as dream-implementer
    participant ImplUI as dream-implementer-ui
    participant Tester as dream-tester
    participant Inspector as dream-inspector
    participant Project as Generated Project

    User->>General: Plain product request
    General->>Research: Gather context (on-demand)
    General->>Project: Phase 0: Write specs/<feature>/plan.md
    General->>User: Present plan for review
    User->>General: Confirm, edit, or deny
    General->>Project: Write specs/app-contract.yaml
    General->>Project: Write specs/decisions.yaml
    General->>Project: Write feature contract and test plan
    General->>Designer: Phase 0.5: produce ui-spec.md
    Designer->>Project: Write specs/<feature>/ui-spec.md
    General->>Impl: Phase 1: KMP foundation (domain, data, api)
    Impl->>Project: Add domain entities, repos, data layer, route types, tests
    par Phase 2: UI in parallel
        General->>ImplUI: Android Compose + iOS SwiftUI (platform: both)
    end
    ImplUI->>Project: Compose screens, SwiftUI views, ViewModels, bridge, tests
    General->>Project: Phase 3: wiring (umbrella, DI, nav)
    General->>Tester: Phase 4: full verification ladder
    Tester->>Project: Kotlin Toolchain tests, platform builds, device launch, logs
    Tester->>General: Report defects
    General->>Impl: Repair failures when needed
    Impl->>Project: Fix issues
    General->>Inspector: Request final QA review
    Inspector->>General: APPROVED or defects
    General->>Project: Write verification report and request commit approval if safe
```

Required generated artifacts:

- `specs/app-contract.yaml`
- `specs/decisions.yaml`
- `specs/<feature>/plan.md`
- `specs/<feature>/contract.yaml`
- `specs/<feature>/ui-spec.md`
- `specs/<feature>/test-plan.md`
- `specs/<feature>/implementation-report.md`
- `specs/<feature>/retrospective.md`
- `specs/verification-report.md`

## Architecture Rules

Dream keeps generated apps modular so they can grow without collapsing into host-app code.

Non-negotiable rules:

- keep host apps thin
- place product behavior in feature modules
- preserve `Presentation -> Domain <- Data`
- keep domain pure Kotlin
- keep navigation centralized
- keep user-visible strings in shared resources
- use Dream design-system components and `DreamTheme` tokens before raw Material3 or ad hoc feature-local UI primitives
- validate Android and iOS as real product targets

```mermaid
flowchart TD
    Presentation[feature/name-presentation] --> Domain[feature/name-domain]
    Data[feature/name-data] --> Domain
    Presentation --> SharedPresentation[shared/core-presentation]
    Presentation --> Strings[shared/core-strings]
    Presentation --> Design[shared/designsystem]
    Data --> SharedData[shared/core-data]
    Data --> SharedDomain[shared/core-domain]
    subgraph Android Compose Flow
        HostC[androidApp] --> UmbrellaUI[shared/umbrella-ui]
        UmbrellaUI --> Navigation[shared/navigation]
        UmbrellaUI --> FeatureUI[feature impl modules]
    end
    subgraph iOS Native Flow
        HostS[iosApp SwiftUI] --> UmbrellaCore[shared/umbrella-core]
    end
    UmbrellaUI --> UmbrellaCore
    UmbrellaCore --> FeatureData[feature data modules]
```

## Feature Modules

Generated features use this shape:

- `feature/<name>-domain`: domain models, repository interfaces, use cases, pure business contracts
- `feature/<name>-data`: Room/network/local implementations, entities, mapping, repository implementations
- `feature/<name>-api`: public navigation contract — `NavKey` route types and any minimal public types callers need to navigate to this feature
- `feature/<name>-impl`: full feature implementation — screens, ViewModels, UI state/actions/events, presentation mappers, and the Koin module that installs Navigation 3 entry providers

For multi-word feature concepts, keep module names (equal to directory names, which must be unique under the Toolchain) and Kotlin packages aligned. Module directories are flat, such as `feature/expense-details-domain` or `feature/sample-impl`; Kotlin packages stay dotted (`feature.expense.details`).

```mermaid
flowchart TD
    Domain[feature/name-domain]
    Data[feature/name-data] --> Domain
    Api[feature/name-api]
    Impl[feature/name-impl] --> Api
    Impl --> Domain
    Impl --> Shared[shared/core-* + shared/designsystem]
    OtherFeature[other feature or app shell] --> Api
    UmbrellaUI[shared/umbrella-ui] --> Impl
```

Why this shape:

- domain can be tested without platform dependencies
- data depends on domain contracts rather than leaking persistence upward
- `api` keeps the public navigation contract small and stable; callers never need to know about implementation details
- `impl` can be refactored or replaced without changing any caller
- cross-feature navigation is enforced at the module boundary — depending on `impl` is a build error for other features

Feature reuse rules:

- other features and the app shell depend on `feature/<name>-api` only, never on `feature/<name>-impl`
- domain-only reuse can be valid for aggregators or clearly dependent workflows
- cross-feature data implementation reuse is an explicit MVP exception, not the default
- extract to `shared/*` when multiple features equally own the same persisted concept or reusable behavior

Design-system rule:

- feature UI should use `shared/designsystem` components for common buttons, text fields, cards, dialogs, sheets, lists, navigation chrome, progress, feedback, and selection controls
- raw Material3 components are allowed only when no design-system wrapper exists or a wrapper lacks required behavior
- document raw Material3 exceptions and consider adding a shared wrapper when the need is reusable

## Navigation

Dream uses Navigation 3 with centralized navigation display and feature-owned typed route keys.

Rules:

- route types live in `feature/<name>-api` and implement `androidx.navigation3.runtime.NavKey`
- `feature/<name>-impl` installs Navigation 3 entry providers using `navigation<Route> { FeatureScreen() }` in its Koin module
- `shared/navigation` owns the `NavKey` back stack and renders it with `NavDisplay`; it has no knowledge of feature-specific types
- `shared/umbrella-ui` imports feature `impl` Koin modules so all navigation installers are registered before `NavDisplay` renders
- other features or the app shell navigate by depending on `feature/<name>-api` for the route type only

```mermaid
flowchart LR
    Api[feature/name-api\nSampleRoute : NavKey]
    Impl[feature/name-impl\nKoin: navigation&lt;SampleRoute&gt;]
    Impl --> Api
    UmbrellaUI[shared/umbrella-ui] --> Impl
    Navigator[shared/navigation Navigator] --> BackStack[SnapshotStateList&lt;NavKey&gt;]
    AppNavigation[shared/navigation AppNavigation] --> NavDisplay[Navigation 3 NavDisplay]
    BackStack --> NavDisplay
    UmbrellaUI --> AppNavigation
```

This replaced the older shared `AppRoute` pattern and a single `presentation` module holding both routes and screens, because the public navigation contract and implementation details should live in separate modules.

## Dependency Injection

Dream uses Koin with a composition-root mindset.

Rules:

- host apps call the shared guarded startup helper
- features contribute modules, but do not start Koin themselves
- generated feature code uses Koin compiler-plugin DSL registrations
- avoid annotation-based DI such as `@KoinViewModel`, `@Module`, and `@ComponentScan` unless explicitly requested
- retrieve ViewModels at stable navigation/composition boundaries

```mermaid
flowchart TD
    subgraph Android Compose Flow
        Android[androidApp MainActivity] --> InitUI[shared/umbrella-ui initKoin]
    end
    subgraph iOS Native Flow
        IOSS[iosApp SwiftUI] --> InitNative[Native Swift initKoin]
    end
    InitUI --> Guard[one-time startup guard]
    InitNative --> Guard
    Guard --> Modules[Koin modules]
    Modules --> Core[shared/umbrella-core data/domain]
    Modules --> UI[feature impl Koin modules]
    Modules --> Navigator[shared Navigator]
```

## Storage

The starter demonstrates local-first persistence with KMP Room.

Important defaults:

- Room builder policy is centralized in `shared/core-data`.
- The bundled SQLite driver is configured once.
- Room query execution uses `Dispatchers.Default` for KMP-safe behavior.
- First-pass MVP iteration uses destructive migration fallback to recover from stale local test schemas.
- Real apps that need to preserve user data must replace destructive fallback with migrations.

## Testing

Generated features should include tests by layer.

- domain tests for models, value rules, and repository contracts
- data tests for entity mapping and repository behavior using fake DAO/database boundaries where possible
- presentation tests for loading, empty, content, error, actions, and events
- ViewModel tests use reusable dispatcher setup with `@BeforeTest` and `@AfterTest`
- Turbine is available for flow and state transition assertions
- time-sensitive tests should use `FakeTimeProvider` and readable `kotlinx-datetime` construction

## Verification

Dream treats verification as a ladder, not a single build command.

```mermaid
flowchart TD
    Preflight[./.opencode/scripts/dream-preflight.sh] --> Tests[./kotlin test per platform]
    Tests --> AndroidBuild[./kotlin build -m androidApp --platform=android]
    AndroidBuild --> AndroidRuntime[Android CLI install/launch/log inspection]
    AndroidRuntime --> IOSBuild[./kotlin build -m iosApp --platform=iosSimulatorArm64]
    IOSBuild --> IOSRuntime[xcrun simctl install/launch/log inspection]
    IOSRuntime --> MCP[mobile-mcp element tree + screenshot on failure]
    MCP --> Report[verification-report.md]
```

Run from `temp/` or from a generated copy:

```bash
./.opencode/scripts/dream-preflight.sh
./kotlin build
./kotlin test --platform=android
./kotlin test --platform=iosSimulatorArm64
./kotlin build -m androidApp --platform=android
./kotlin build -m iosApp --platform=iosSimulatorArm64
```

Focused iteration example: `./kotlin test -m sample-impl --platform=android`. Module names equal directory names (`core-domain`, `sample-impl`, ...).

Feature-owned smoke flows live under `maestro/features/<feature-name>/`. Run them via `/dream-e2e`:

```bash
/dream-e2e <feature-name>
```

Every user-visible generated feature should have at least one Android smoke flow and one iOS smoke flow when the feature is supported on that platform. App-level startup smoke checks may exist, but they do not replace feature-level coverage.

Verification reports must distinguish:

- `passed`
- `blocked by environment`
- `not run`
- `not applicable`

Use `temp/.opencode/guidance/templates/verification-report-template.md` for the report shape.

## Working With The Starter

Dream orchestrates feature work in phases through `dream-general`. When adding or replacing a feature:

1. **Phase 0 — Plan & User Review:** `dream-general` produces `specs/<feature>/plan.md`, presents it to the user, and gets explicit confirmation before proceeding.
2. **Phase 1 — Research (on-demand):** Use `dream-research` for codebase discovery, external library research, API docs, or troubleshooting.
3. **Phase 2 — Contract:** Create or update the feature contract. Write `specs/<feature>/contract.yaml` and `specs/<feature>/test-plan.md`.
4. **Phase 3 — Design:** Use `dream-designer` to produce `specs/<feature>/ui-spec.md` — view states, design-system components, layout, interactions.
5. **Phase 4 — KMP foundation:** Use `dream-implementer` for `feature/<name>-domain` (entities, repository interfaces, use cases), `feature/<name>-data` (repo implementations, DTOs, mappers), and `feature/<name>-api` (NavKey route types only). No UI.
6. **Phase 5 — UI:** Deploy `dream-implementer-ui` for cross-platform UI — Compose screens, ViewModels, Koin navigation installers, presentation tests, SwiftUI views, bridge code, and Swift unit tests in `iosApp/test`.
7. **Phase 6 — Wiring:** Consolidate umbrella module imports, DI registration, and navigation entries.
8. **Phase 7 — Verification:** Use `dream-tester` for the full verification ladder — Kotlin Toolchain unit tests, platform builds, device install/launch, log inspection. Run Maestro end-to-end flows separately via `/dream-e2e`.
9. Write reports using the templates under `temp/.opencode/guidance/templates/`.
10. Request `dream-inspector` QA gate for substantial work — architecture violations, contract parity, test coverage, anti-patterns.
11. Only commit when verification and review are clean and staging is safe.

Use `temp/.opencode/guidance/new-feature-checklist.md` as the operational checklist.

## MCP Tools

Dream agents have access to two MCP servers for enhanced capabilities:

**mobile-mcp** provides device interaction for verification and UI inspection. Used by `dream-tester` and `dream-inspector` for screenshots, element trees, and crash logs.

**codebase-memory-mcp** provides code intelligence via a persistent knowledge graph. Used by `dream-discovery`, `dream-research`, `dream-implementer`, `dream-implementer-ui`, `dream-inspector`, and `dream-tester` for symbol search, call tracing, impact analysis, and architecture overview.

Agents should prefer MCP queries over grep/read for codebase exploration. See `temp/.opencode/guidance/reference/codebase-memory-mcp-tools.md` for the full tool reference.

## Sample Feature Policy

`feature/sample-*` exists as starter scaffolding. It is useful for demonstrating module shape, Room, strings, ViewModel tests, navigation, and DI.

Once a real first feature replaces it in a generated app, remove sample leftovers:

- `feature/sample-*` modules
- `project.yaml` module entries
- sample DI registrations
- sample route keys and navigation entries
- sample strings and docs text
- sample database names and Room schema exports

The current template guidance explicitly calls out these cleanup checks because leftover sample artifacts were a source of review drift in the first generated-app run.

## Updating Dream Itself

When changing Dream rather than a generated app:

- update `temp/.opencode/guidance/dream-agent-operating-guide.md` if workflow, priorities, or success criteria change
- update `temp/.opencode/guidance/prompts/` when generation or verification behavior changes
- update `temp/.opencode/knowledge-graph/best-practices/` when architecture rules change
- update `temp/AGENTS.md` when starter structure or validation commands change
- keep root docs compact and link to detailed guidance rather than duplicating every rule

## Current Follow-Ups

- Run a fresh generated-project flow to verify the hardening changes eliminate first-run drift in practice.
- Keep sample schema exports only as starter scaffolding; remove them when a generated app replaces `feature/sample-*`.
- Continue tightening identity normalization during project instantiation.
