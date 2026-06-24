# Dream Onboarding

Dream is a local-first AI MVP builder for Kotlin Multiplatform apps. It packages a reusable starter project, OpenCode agents, architecture guidance, verification rules, and smoke-test scaffolding so a natural-language product request can become a modular Android, iOS, and optional Desktop MVP.

Use this document when you are new to the repository and need to understand how Dream is built, how the workflow runs, and why the template is shaped this way.

## Mental Model

Dream is not a single generated app. Dream is the tool and starter baseline that creates generated apps.

The repository centers on `temp/`, which is the canonical starter project copied by the initializer. Generated app work should happen in `temp/` or in a copy created from `temp/`, not in unrelated root-level Gradle surfaces.

```mermaid
flowchart TD
    User[User product request] --> Dream[Dream workflow]
    Dream --> Contract[.mvp contracts and decisions]
    Dream --> Template[temp starter copy]
    Template --> Generated[Generated KMP app]
    Contract --> Generated
    Generated --> Verify[Tests, builds, platform launch, smoke flows]
    Verify --> Report[.mvp/verification-report.md]
```

## Repository Map

- `dream-init`: initializer entry point that copies and renames `temp/`.
- `AI_MVP_BUILDER_PLAN.md`: compact product tracker and current priorities.
- `AGENTS.md`: repository-level working rules for agents and contributors.
- `ONBOARDING.md`: this entry-level guide.
- `temp/`: canonical Kotlin Multiplatform starter project.
- `temp/.opencode/`: packaged OpenCode config, agents, commands, and prompt guidance.
- `temp/.knowledge-graph/`: reusable architecture rules and retrieval sources.
- `temp/.mvp/`: generated contracts, decisions, feature reports, and verification reports.
- `temp/maestro/`: Android and iOS smoke-flow scaffolding.
- `temp/scripts/dream-preflight.sh`: local readiness check for git, Android SDK, and Maestro.

```mermaid
flowchart LR
    Root[Dream repo root] --> Init[dream-init]
    Root --> Plan[AI_MVP_BUILDER_PLAN.md]
    Root --> Temp[temp/ canonical starter]
    Temp --> Apps[androidApp / iosApp / desktopApp]
    Temp --> Shared[shared/*]
    Temp --> Features[feature/*]
    Temp --> Agents[.opencode agents and commands]
    Temp --> Graph[.knowledge-graph rules]
    Temp --> MVP[.mvp generated metadata]
    Temp --> Maestro[maestro smoke flows]
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
    participant Implementer as dream-implementer
    participant Inspector as dream-inspector
    participant Project as Generated Project

    User->>General: Plain product request
    General->>Project: Write .mvp/app-contract.yaml
    General->>Project: Write .mvp/decisions.yaml
    General->>Project: Write feature contract and test plan
    General->>Implementer: Implement from contract
    Implementer->>Project: Add feature modules, tests, wiring
    General->>Project: Run preflight, tests, builds, platform checks
    Implementer->>Project: Repair failures when needed
    General->>Inspector: Request final QA review
    Inspector->>General: APPROVED or defects
    General->>Project: Write verification report and commit if safe
```

Required generated artifacts:

- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- `.mvp/features/<feature>/contract.yaml`
- `.mvp/features/<feature>/test-plan.md`
- `.mvp/features/<feature>/implementation-report.md`
- `.mvp/verification-report.md`

## Architecture Rules

Dream keeps generated apps modular so they can grow without collapsing into host-app code.

Non-negotiable rules:

- keep host apps thin
- place product behavior in feature modules
- preserve `Presentation -> Domain <- Data`
- keep domain pure Kotlin
- keep navigation centralized
- keep user-visible strings in shared resources
- validate Android, iOS, and contract-selected Desktop as real product targets

```mermaid
flowchart TD
    Presentation[feature/name/presentation] --> Domain[feature/name/domain]
    Data[feature/name/data] --> Domain
    Presentation --> SharedPresentation[shared/core/presentation]
    Presentation --> Strings[shared/core/strings]
    Presentation --> Design[shared/designsystem]
    Data --> SharedData[shared/core/data]
    Data --> SharedDomain[shared/core/domain]
    Host[androidApp / iosApp / desktopApp] --> UmbrellaUI[shared/umbrella-ui]
    UmbrellaUI --> Navigation[shared/navigation]
    UmbrellaUI --> FeatureUI[feature presentation modules]
    UmbrellaUI --> UmbrellaCore[shared/umbrella-core]
    UmbrellaCore --> FeatureData[feature data modules]
```

## Feature Modules

Generated features should use this shape:

- `feature/<name>/domain`: domain models, repository interfaces, pure business contracts
- `feature/<name>/data`: Room/network/local implementations, entities, mapping, repository implementations
- `feature/<name>/presentation`: state, actions, events, ViewModels, UI, feature-owned route keys

Why this shape:

- domain can be tested without platform dependencies
- data depends on domain contracts rather than leaking persistence upward
- presentation maps domain state into UI state and stays testable
- host apps stay thin and do not accumulate product behavior

## Navigation

Dream uses Navigation 3 with centralized navigation display and feature-owned typed route keys.

Current rule:

- concrete route keys live in the feature presentation module that owns the destination
- route keys implement `androidx.navigation3.runtime.NavKey`
- shared navigation owns the `NavKey` back stack and renders it with `NavDisplay`
- Koin Navigation 3 entries wire route keys to screens

```mermaid
flowchart LR
    Route[Feature-owned Route : NavKey] --> KoinEntry[Koin navigation<Route> entry]
    Navigator[shared/navigation Navigator] --> BackStack[SnapshotStateList<NavKey>]
    AppNavigation[shared/navigation AppNavigation] --> NavDisplay[Navigation 3 NavDisplay]
    BackStack --> NavDisplay
    KoinEntry --> NavDisplay
```

This replaced the older shared `AppRoute` pattern because feature-specific route definitions should not live in shared core unless they are genuinely shared.

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
    Android[androidApp MainActivity] --> Init[shared/umbrella-ui initKoin]
    IOS[iOS MainViewController] --> Init
    Desktop[desktopApp main] --> Init
    Init --> Guard[one-time startup guard]
    Guard --> Modules[Koin modules]
    Modules --> Core[shared/umbrella-core data/domain]
    Modules --> UI[feature UI modules]
    Modules --> Navigator[shared Navigator]
```

## Storage

The starter demonstrates local-first persistence with KMP Room.

Important defaults:

- Room builder policy is centralized in `shared/core/data`.
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
    Preflight[./scripts/dream-preflight.sh] --> Tests[./gradlew allTests]
    Tests --> AndroidBuild[./gradlew :androidApp:assembleDebug]
    AndroidBuild --> AndroidRuntime[Android CLI install/launch/log inspection]
    AndroidRuntime --> IOSBuild[xcodebuild]
    IOSBuild --> IOSRuntime[xcrun simctl install/launch/log inspection]
    IOSRuntime --> Maestro[Maestro Android/iOS smoke flows]
    Maestro --> Desktop{Desktop selected?}
    Desktop -->|yes| DesktopBuild[compileKotlinJvm and hotRunJvm]
    Desktop -->|no| Report[verification-report.md]
    DesktopBuild --> Report
```

Run from `temp/` or from a generated copy:

```bash
./scripts/dream-preflight.sh
./gradlew allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:compileKotlinJvm
```

When Desktop is in the app contract and interactive validation is required:

```bash
./gradlew :desktopApp:hotRunJvm
```

Smoke flows live under `maestro/`:

```bash
maestro test maestro/android/smoke.yaml
maestro test maestro/ios/smoke.yaml
```

Verification reports must distinguish:

- `passed`
- `blocked by environment`
- `not run`
- `not applicable`

Use `temp/.opencode/guidance/templates/verification-report-template.md` for the report shape.

## Working With The Starter

When adding or replacing a feature:

1. Create or update the feature contract first.
2. Add `feature/<name>/domain`, `feature/<name>/data`, and `feature/<name>/presentation` as needed.
3. Add feature-owned `NavKey` routes in presentation.
4. Register dependencies through Koin modules.
5. Add shared string resources before writing UI copy.
6. Add tests by layer.
7. Update Maestro smoke scaffolding for the user-visible journey.
8. Run preflight and verification.
9. Write reports using the templates under `temp/.opencode/guidance/templates/`.
10. Only commit when verification and review are clean and staging is safe.

Use `temp/NEW-FEATURE-CHECKLIST.md` as the operational checklist.

## Sample Feature Policy

`feature/sample` exists as starter scaffolding. It is useful for demonstrating module shape, Room, strings, ViewModel tests, navigation, and DI.

Once a real first feature replaces it in a generated app, remove sample leftovers:

- `feature/sample` modules
- settings includes
- sample DI registrations
- sample route keys and navigation entries
- sample strings and docs text
- sample database names and Room schema exports

The current template guidance explicitly calls out these cleanup checks because leftover sample artifacts were a source of review drift in the first generated-app run.

## Updating Dream Itself

When changing Dream rather than a generated app:

- update `AI_MVP_BUILDER_PLAN.md` if workflow, priorities, or success criteria change
- update `temp/.opencode/guidance/prompts/` when generation or verification behavior changes
- update `temp/.knowledge-graph/best-practices/` when architecture rules change
- update `temp/README.md` and `temp/AGENTS.md` when starter structure or validation commands change
- keep root docs compact and link to detailed guidance rather than duplicating every rule

## Current Follow-Ups

- Run a fresh generated-project flow to verify the hardening changes eliminate first-run drift in practice.
- Keep sample schema exports only as starter scaffolding; remove them when a generated app replaces `feature/sample`.
- Continue tightening identity normalization during project instantiation.
