# Dream AI MVP Builder Plan

Codename: `Dream`

## Document Role

This file is the canonical implementation plan for Dream.

It is intentionally large and explicit so that any AI agent working in this repository can use it as the main product, architecture, and execution reference without needing to infer missing rules.

This file should be read together with the `knowledge-graph/` directory, but when an AI agent needs one operational source of truth for how Dream should build projects, repair projects, and structure generated code, this file should be sufficient.

## Canonical Product Identity

- Product name: `Dream`
- Product role: local AI MVP builder
- Product scope: generate, verify, preview, and repair mobile-first Kotlin Multiplatform MVPs locally
- Product style: local-first, agent-assisted, template-driven, verification-gated

Use `Dream` as the canonical codename across planning, implementation, documentation, generated artifacts, and prompt scaffolding.

## Goal

Build Dream, a local, Replit-like AI MVP builder for mobile-first apps using Kotlin Multiplatform and Compose Multiplatform.

The tool should take a natural-language app or feature description, generate a working project, run tests, launch a desktop preview, read build and runtime failures, fix them, and report a verified result.

## Dream's Main Architectural Position

Dream is not only a code generator. It is a code generator with architectural guardrails.

The architecture must be strong enough that generated code is:

- readable
- reviewable
- testable
- repairable by another AI later
- portable across Android, iOS, and desktop
- compatible with a future migration to native UI on iOS
- compatible with a future migration to a fullstack client-server layout

That means Dream should not optimize only for the shortest possible template. It should optimize for the smallest template that still preserves strong module boundaries and clear dependency rules.

## Product Direction

V1 is local-first and mobile-first.

Targets:

- Android app using Compose Multiplatform
- iOS app starting with Compose Multiplatform UI
- Desktop app used for testing, preview, and MCP-driven Hot Reload

Future direction:

- Support native SwiftUI for iOS later
- Support native UI by separating shared logic from shared UI when needed
- Support optional Ktor backend generation for fullstack MVPs
- Support web later as a separate preset, not in V1

## Knowledge-Graph Alignment

Dream must follow the architectural defaults encoded in `knowledge-graph/`.

Those defaults matter because they are intended to act as retrieval guardrails for future AI generation and review.

The most important reusable rules from the knowledge graph are:

- package by feature, not by technical layer alone
- split each feature into `domain`, `data`, and `presentation`
- keep domain pure Kotlin
- keep UI dependent on domain, not on data internals
- centralize cross-cutting concerns in shared core modules
- use physical Gradle modules to enforce dependency direction
- keep platform shells thin
- keep navigation infrastructure outside feature UI where practical
- keep shared MVI infrastructure small and practical
- split shared core by responsibility, not by a giant catch-all module

Canonical dependency rule:

```text
Presentation -> Domain <- Data
```

Dream should generate code that respects this rule physically, not only conceptually.

## Project Template Decision

Dream should create reusable project templates instead of generating every project from nothing.

Reason:

- the AI needs stable architecture boundaries
- the AI should focus on feature code, not rebuild Gradle, DI, navigation, testing, and preview infrastructure each time
- the template gives every MVP the same verified baseline
- the template makes tests and hot reload available from the first generated feature
- the template makes future native UI migration possible without rewriting business logic
- the template gives later AI repair passes a known structural map

## JetBrains Baseline

Dream should use the current JetBrains KMP wizard as the top-level baseline:

```text
https://kmp.jetbrains.com/?android=true&ios=true&iosui=compose&desktop=true&includeTests=true
```

Important clarification:

- Dream should preserve JetBrains' new top-level app structure
- Dream should not preserve a simplistic single-module `shared` implementation if that conflicts with the knowledge-graph architecture rules

This means Dream should follow a hybrid rule:

- JetBrains structure for top-level runnable apps
- Dream modular structure inside the shared and feature layers

## Default Dream Structure

Use JetBrains' new default top-level KMP structure for runnable applications:

```text
project-root/
  androidApp/
  iosApp/
  desktopApp/
  shared/
  feature/
  build-logic/
  docs/
  .mvp/
```

Responsibilities:

- `androidApp`: Android host app only
- `iosApp`: iOS host app only
- `desktopApp`: desktop host app only, local preview surface, and MCP-driven Hot Reload surface
- `shared/`: reusable cross-feature infrastructure and shared platform-independent building blocks
- `feature/`: all product behavior split into feature modules
- `build-logic/`: Gradle convention plugins and build setup
- `docs/`: optional human-facing documentation for generated projects when needed
- `.mvp/`: Dream metadata, contracts, decisions, and verification reports

## Why Dream Should Not Use One Giant Shared Module

Dream should not use one giant `shared` Gradle module as its final architecture baseline.

Reasons:

- it weakens dependency enforcement
- it makes accidental cross-layer coupling easier
- it reduces clarity for future AI agents
- it makes feature boundaries harder to review
- it makes portability of pure business logic less explicit
- it conflicts with the knowledge graph's recommendation for physical module boundaries

Package boundaries inside one module are not enough as the main protection mechanism.

Dream should use Gradle modules to enforce architecture where it matters.

## Recommended Default Gradle Module Layout

For a local-only mobile-first MVP, Dream should generate this default structure:

```text
my-mvp/
  settings.gradle.kts
  build.gradle.kts
  gradle/
  gradlew
  gradlew.bat
  build-logic/

  androidApp/
  iosApp/
  desktopApp/

  shared/
    core/
      domain/
      data/
      presentation/
      storage/
      testing/
    designsystem/
    navigation/
    umbrella-core/
    umbrella-ui/

  feature/
    sample/
      domain/
      data/
      presentation/

  .mvp/
    app-contract.yaml
    decisions.yaml
    verification-report.md
    features/
      feature-name/
        contract.yaml
        test-plan.md
        implementation-report.md
```

## Module Responsibilities

### App Shell Modules

#### `androidApp`

Contains:

- Android manifest
- Android `Application`
- Android `Activity`
- Android-specific DI wiring if needed
- Android-only resources or integrations

Must stay thin.

It should not own business logic.

#### `iosApp`

Contains:

- Xcode project
- Swift host app
- bridge to the shared Kotlin UI or shared Kotlin exported business logic

Must stay thin.

It should not duplicate shared business rules.

#### `desktopApp`

Contains:

- desktop JVM entry point
- preview harness
- fake-data preview shell
- MCP-driven Hot Reload integration point
- desktop-specific runtime toggles useful for AI repair and visual iteration

Must stay thin.

It should consume shared UI and feature presentation modules rather than own product behavior.

### Shared Core Modules

#### `shared:core:domain`

Contains pure foundational domain utilities shared across the app.

Examples:

- `Result`
- `AppError`
- `TimeProvider`
- common value abstractions
- common validation contracts

Rules:

- no Compose
- no Android APIs
- no iOS APIs
- no database APIs
- no networking APIs

#### `shared:core:data`

Contains shared runtime helpers used by data modules.

Examples:

- dispatchers abstraction
- network result wrappers
- mapper utilities
- data error mapping helpers
- cache policy helpers

Rules:

- can depend on `shared:core:domain`
- should not depend on presentation

#### `shared:core:presentation`

Contains shared presentation infrastructure.

Examples:

- base MVI view model
- `UiState`, `UiAction`, `UiEvent` conventions
- `UiMessage`
- small state reducers or intent helpers

Rules:

- keep it small
- do not build an internal framework
- provide only reusable primitives teams actually need

#### `shared:core:storage`

Contains storage contracts and thin shared storage abstractions.

Examples:

- `StorageDriver`
- settings contracts
- key-value abstraction

Rules:

- platform-specific implementations stay outside pure domain modules

#### `shared:core:testing`

Contains shared test utilities.

Examples:

- test dispatchers
- fake time providers
- common fixtures
- fake repositories shared by tests

### Shared UI and Infrastructure Modules

#### `shared:designsystem`

Contains:

- app theme
- color tokens
- typography
- spacing tokens
- shared UI components
- shared user-visible resources

Rule:

- all user-visible strings should come from a consistent resource strategy

#### `shared:navigation`

Contains:

- root app shell
- route hierarchy
- nav graph setup
- global back behavior rules
- shell-level composition of top-level tabs and push destinations

Rules:

- keep feature presentation modules unaware of the full routing graph when possible
- pass callbacks or intent-level actions into feature screens
- keep routing infrastructure centralized

#### `shared:umbrella-core`

Contains exported business logic aggregation for platforms, especially iOS, when needed.

Use it to aggregate stable pure feature dependencies rather than exporting every feature ad hoc.

#### `shared:umbrella-ui`

Contains shared UI composition wiring where a dedicated app-wide UI aggregation layer is useful.

This module can help keep `androidApp` and `desktopApp` thin.

## Feature Module Structure

Each generated feature should be real Gradle modules under `feature/<name>/`.

Default shape:

```text
feature/<feature-name>/
  domain/
  data/
  presentation/
```

### `feature:<name>:domain`

Contains:

- domain models
- repository interfaces
- use cases
- domain-specific errors
- pure business rules

Must not contain:

- Compose
- Android APIs
- iOS APIs
- Room annotations
- Ktor clients
- DataStore
- platform SDK calls

### `feature:<name>:data`

Contains:

- repository implementations
- DTOs
- mappers at the data boundary
- local persistence access
- remote APIs
- caching logic
- exception-to-domain-error mapping

Rules:

- depends on its own `domain`
- can depend on shared data and storage infrastructure
- should not depend casually on other features' data modules
- if shared behavior appears, move it into shared modules or expose a domain contract

### `feature:<name>:presentation`

Contains:

- Compose screens
- UI state
- UI actions
- UI events
- UI models
- presentation mappers from domain to UI models
- feature view models

Rules:

- depends on its own `domain`
- depends on `shared:core:presentation`
- depends on `shared:designsystem`
- should not depend on other feature presentation modules
- should not own the global routing graph

## Canonical Dependency Rules

These rules should be treated as default generation constraints.

### Strict Direction

```text
feature:x:presentation -> feature:x:domain <- feature:x:data
```

### Shared Core Dependencies

- `feature:x:domain` may depend on pure shared core modules such as `shared:core:domain`
- `feature:x:data` may depend on `feature:x:domain`, `shared:core:data`, `shared:core:storage`, and platform/runtime libraries it actually needs
- `feature:x:presentation` may depend on `feature:x:domain`, `shared:core:presentation`, and `shared:designsystem`

### Disallowed by Default

- feature presentation depending on another feature presentation
- feature data depending on another feature data as a normal rule
- domain depending on Compose or platform SDKs
- app modules owning business logic
- navigation route types leaking into every feature by default

### Aggregator Exception Pattern

One feature may act as an aggregator, such as a dashboard or overview feature.

In that case:

- it may depend on other features' `domain` modules only
- it should not depend on other features' `presentation` modules

## Build Logic Strategy

Dream should use Gradle convention plugins through `build-logic/`.

Recommended conventions:

- `app.kmp`: base KMP setup
- `app.kmp.compose`: Compose-specific KMP setup
- `app.kmp.android-app`: Android host app setup
- `app.kmp.feature-domain`: feature domain module defaults
- `app.kmp.feature-data`: feature data module defaults
- `app.kmp.feature-presentation`: feature presentation module defaults
- `app.kmp.shared-core`: shared core module defaults

Rules:

- convention plugins should configure build behavior, not hide all dependencies
- each module build file should still declare its real direct dependencies clearly
- module build files should stay small and readable
- enable `TYPESAFE_PROJECT_ACCESSORS`

## Template Philosophy

Dream should generate a project that is immediately usable and readable by both humans and AI agents.

The template should include:

- stable module boundaries
- a minimal but consistent MVI presentation style
- centralized theming and design system setup
- centralized navigation shell
- storage hooks
- preview harness
- test utilities
- sample feature modules
- verification tasks

The template should not include:

- unnecessary framework layers
- giant all-purpose base classes
- hidden build magic that obscures module intent
- architecture shortcuts that reduce future repairability

## Reusable Starting Code

Every generated Dream project should start with reusable infrastructure already in place.

### Shared Core Starting Code

Suggested modules and starter files:

```text
shared/core/domain/src/commonMain/kotlin/
  Result.kt
  AppError.kt
  TimeProvider.kt

shared/core/data/src/commonMain/kotlin/
  DispatchersProvider.kt
  DataResult.kt

shared/core/presentation/src/commonMain/kotlin/
  MviViewModel.kt
  UiMessage.kt

shared/core/storage/src/commonMain/kotlin/
  StorageDriver.kt

shared/core/testing/src/commonMain/kotlin/
  TestDispatchersProvider.kt
  FakeTimeProvider.kt
```

### Shared UI Starting Code

```text
shared/designsystem/src/commonMain/kotlin/
  AppTheme.kt
  AppColors.kt
  AppTypography.kt
  Components.kt

shared/navigation/src/commonMain/kotlin/
  AppRoute.kt
  AppNavigation.kt
  AppShell.kt
```

### Sample Feature Starting Code

```text
feature/sample/domain/src/commonMain/kotlin/
  model/
  repository/
  usecase/
  error/

feature/sample/data/src/commonMain/kotlin/
  repository/
  mapper/
  local/
  remote/

feature/sample/presentation/src/commonMain/kotlin/
  SampleState.kt
  SampleAction.kt
  SampleEvent.kt
  SampleViewModel.kt
  SampleScreen.kt
  SampleComponents.kt
```

## App Shell Strategy

Dream should use a centralized app shell.

The app shell is responsible for:

- applying the app theme
- applying layout direction if relevant
- hosting the root navigation graph
- coordinating top-level destinations
- defining global back behavior
- keeping routing concerns out of feature modules where practical

Feature modules should expose screens and callbacks, not own the whole app graph.

## Navigation Strategy

Preferred route modeling:

- typed routes
- sealed route hierarchy
- serializable route objects when supported by the chosen navigation stack

Rules:

- top-level tabs and push destinations should be differentiated clearly
- global back behavior should be intentional and product-defined
- feature presentation modules should receive callbacks or intents instead of knowing the entire route graph

## Presentation Pattern

Dream should use a mostly MVI-oriented presentation style.

Each feature presentation module should usually have:

- `FeatureState`
- `FeatureAction`
- `FeatureEvent`
- `FeatureViewModel`
- `FeatureScreen`

Base MVI infrastructure should stay minimal.

Recommended base primitives:

- `state: StateFlow<S>`
- `eventFlow: Flow<E>`
- `reduce {}` or `updateState {}`
- `intent {}` or equivalent coroutine entry helper
- `exclusiveIntent(key)` only if truly useful

Rules:

- separate long-lived renderable state from one-off effects
- keep actions explicit
- avoid building a complex in-app framework unless necessary
- use one presentation pattern consistently by default

## Data Layer Strategy

Each feature's data module should implement its own domain contracts.

Rules:

- repository interfaces live in domain
- repository implementations live in data
- mapping from transport or persistence models into domain models happens at the data boundary
- presentation-specific mapping lives in presentation
- per-feature clients are acceptable when behavior differs meaningfully
- cache-first or local-first reads are preferred where stability matters

## Storage Strategy

The user should choose storage at project creation.

Options:

- `none`: no persistence
- `datastore`: preferences and simple key-value state
- `room`: Room KMP for local database MVPs
- `sqldelight`: SQLDelight for explicit SQL and portability

Recommended defaults:

- use `none` if the app does not need persistence
- use `datastore` for settings, flags, onboarding state, and simple preferences
- use `room` for mobile-first local database apps
- use `sqldelight` when the user prefers SQL-first modeling

Storage infrastructure should stay behind shared contracts where practical.

## Dependency Injection Strategy

Dream should allow modules to contribute registrations without allowing features to bootstrap the whole application themselves.

Rules:

- shared modules provide shared registrations
- feature modules provide their own feature registrations
- app modules supply platform-specific implementations
- app shells compose the final dependency graph

Koin remains a practical default, but Dream should avoid over-coupling the entire product architecture to one DI library.

## Desktop Preview And MCP-Driven Hot Reload

The desktop app is the local preview surface and the fastest AI repair surface.

Use it for:

- fast app launch
- MCP-driven Hot Reload
- fake data previews
- manual UI testing
- visual iteration
- reading runtime failures quickly

The desktop target is not merely a convenience. It is a core part of Dream's AI repair loop.

Dream should wire MCP-driven Hot Reload to `desktopApp`, not Android.

### Required Preview States

The template should include preview states for generated screens:

- loading
- empty
- content
- error
- long content
- dark mode
- small window
- large window

### Preview Rules

- the desktop app must be able to render fake or isolated states quickly
- runtime logs should be visible to Dream during preview
- UI reload loops must be optimized for fast human and AI iteration

### Backend-Enabled Preview

When backend is enabled, `mvp preview` should start both the Ktor server and the desktop app:

```text
1. Start Ktor server on a local port.
2. Start desktop app.
3. Inject the API base URL into the desktop app.
4. Stream server and desktop logs.
5. Feed build, server, API, and desktop runtime failures back into the AI repair loop.
```

## Testing Strategy

Testing is mandatory for generated MVPs.

Dream should generate tests at the module level.

### Minimum Generated Tests

- shared core unit tests
- feature domain model tests
- feature use case tests
- feature repository fake tests
- feature presentation state and action tests
- mapper tests
- persistence tests when storage is enabled

### Preferred Test Ownership

- `shared:core:*` modules test their own utilities
- `feature:x:domain` tests pure business logic
- `feature:x:data` tests repository behavior, mapping, and persistence rules
- `feature:x:presentation` tests state transitions and event behavior

### Additional Backend Tests When Ktor Is Enabled

- route tests
- service tests
- repository tests
- request and response serialization tests
- validation tests
- API error mapping tests

## Verification Commands

### Local-Only MVP Verification

At minimum Dream should run:

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:run
```

If the project has modular shared and feature modules, `allTests` should aggregate all shared and feature tests.

### Server-Enabled Verification

At minimum Dream should run:

```bash
./gradlew :server:test
./gradlew :server:build
./gradlew allTests
./gradlew :app:androidApp:assembleDebug
./gradlew :app:desktopApp:run
```

### iOS Verification When Available

```bash
./gradlew iosSimulatorArm64Test
```

Dream must not report success until checks pass or a clear blocker is documented.

## AI Repair Loop

The local builder should behave like this:

```text
Generate code
Run formatter, build, and tests
Read failures
Fix failures
Run again
Launch desktop preview
Launch server if backend is enabled
Read runtime failures
Fix again
Write verification report
```

The repair loop should continue until:

- the generated project is green
- a platform or environment blocker is real and documented
- or the requested product behavior is impossible under the chosen constraints

## Feature Contract

Every generated feature should start from a compact contract.

Example:

```yaml
feature: Expenses
goal: Let users track expenses and see monthly totals
entities:
  - Expense
actions:
  - create expense
  - delete expense
  - filter by category
  - view monthly total
screens:
  - ExpenseListScreen
  - AddExpenseSheet
states:
  - loading
  - empty
  - content
  - error
tests:
  - creates expense
  - validates required amount
  - calculates monthly total
  - filters expenses by category
```

Backend-enabled contract extension:

```yaml
backend:
  enabled: true
  framework: ktor
  storage: sqlite
api:
  basePath: /api/expenses
  endpoints:
    - method: GET
      path: /api/expenses
      response: List<ExpenseDto>
    - method: POST
      path: /api/expenses
      body: CreateExpenseRequest
      response: ExpenseDto
    - method: DELETE
      path: /api/expenses/{id}
```

The contract should be stored in:

```text
.mvp/features/<feature-name>/contract.yaml
```

## Dream Metadata Layout

Dream should store project-specific generation and verification metadata under `.mvp/`.

Default shape:

```text
.mvp/
  app-contract.yaml
  decisions.yaml
  verification-report.md
  features/
    feature-name/
      contract.yaml
      test-plan.md
      implementation-report.md
```

Purpose:

- `app-contract.yaml`: top-level product definition
- `decisions.yaml`: explicit generation choices and architecture selections
- `verification-report.md`: result of the current repair and validation cycle
- `features/*/contract.yaml`: per-feature contract
- `features/*/test-plan.md`: intended checks and behaviors
- `features/*/implementation-report.md`: AI implementation notes and repair results

## CLI And OpenCode

The CLI should build on OpenCode for V1, but the MVP engine should not be tightly coupled to OpenCode internals.

Recommended architecture:

```text
MVP CLI
  -> MVP Engine
    -> Preset system
    -> Template manager
    -> Contract generator
    -> Verification runner
    -> OpenCode adapter
```

### OpenCode Responsibilities

- read project context
- generate code
- apply edits
- fix compile and test errors
- review generated code

### MVP Engine Responsibilities

- own project templates
- own feature contract format
- own commands
- own verification gates
- own preview lifecycle
- store reports in `.mvp/`

This keeps V1 fast while allowing another AI backend later.

## CLI Commands

Suggested V1 commands:

```bash
mvp create "Build a simple expense tracker"
mvp create "Build a habit tracker" --storage room
mvp create "Build a team task tracker" --backend ktor --server-db sqlite --client-cache room
mvp feature "Add weekly streak tracking"
mvp test
mvp preview
mvp fix
mvp report
```

## Creation Flow

```text
1. User describes the MVP.
2. CLI asks for storage choice if needed.
3. CLI asks whether a backend is needed if the request implies accounts, sharing, collaboration, sync, admin dashboards, or remote APIs.
4. CLI creates the project from the modular Dream KMP template or the server-enabled modular Dream template.
5. AI creates `.mvp/app-contract.yaml`.
6. AI generates initial shared and feature modules needed for the product.
7. CLI runs verification.
8. CLI launches desktop preview and server if enabled.
9. AI fixes failures until green or reports a real blocker.
```

## Backend Choices

Backend generation should be optional.

Options:

- `none`: no backend, local-only MVP
- `ktor`: Kotlin Ktor backend
- `mock`: generated fake backend for prototype-only flows

Recommended default:

- use `none` unless the app needs accounts, sharing, collaboration, remote sync, admin tools, or multi-device data
- use `ktor` for real fullstack MVPs
- use `mock` when the user wants to prototype API-backed UI without building the real server yet

### Server Database Choices

- `none`: stateless API or mock data only
- `memory`: in-memory repository for demos and tests
- `sqlite`: local server database for MVPs
- `postgres`: production-style database for later fullstack projects

Recommended server defaults:

- use `memory` for prototypes and tests
- use `sqlite` for local fullstack MVPs
- use `postgres` only when the project is explicitly moving toward production or deployment

### Recommended Ktor Stack

- Ktor server
- kotlinx.serialization
- Koin for DI or another minimal shared DI setup
- Exposed or SQLDelight for server persistence
- SQLite as the default local server database
- Ktor server test host for route tests

For V1.5, prefer Ktor + SQLite + Exposed for fastest CRUD generation. Add SQLDelight as an option when shared SQL modeling is useful.

## Server-Enabled Structure

When backend generation is enabled, Dream should switch to this default structure:

```text
my-mvp/
  settings.gradle.kts
  build.gradle.kts
  gradle/
  gradlew
  gradlew.bat
  build-logic/

  core/
    api/
    model/
    validation/

  server/
    src/
    feature/
    shared/

  app/
    androidApp/
    iosApp/
    desktopApp/

    shared/
      core/
        domain/
        data/
        presentation/
        storage/
        testing/
      designsystem/
      navigation/
      umbrella-core/
      umbrella-ui/

    feature/
      sample/
        domain/
        data/
        presentation/

  .mvp/
    app-contract.yaml
    decisions.yaml
    verification-report.md
    features/
```

Responsibilities:

- `core`: code shared between server and client, such as DTOs, validation, and API contracts
- `server`: Ktor backend code
- `app/*`: client-side modular KMP app

## Future Native UI Path

When a generated MVP needs to become a real production app with native UI, Dream should migrate from shared Compose UI to split logic and UI more explicitly.

Recommended path:

```text
shared/
  core/
  designsystem/
  navigation/
  umbrella-core/
  umbrella-ui/
```

Toward:

```text
sharedLogic/
sharedUI/
androidApp/
iosApp/
desktopApp/
```

Move into `sharedLogic`:

- domain models
- repository interfaces
- use cases
- data implementations that remain shared
- persistence abstractions
- validation
- stable presentation contracts if desired

Keep in `sharedUI`:

- Compose screens
- Compose components
- navigation helpers
- shared shell logic for Compose-based platforms

Then `iosApp` can replace Compose screens with SwiftUI while still reusing shared Kotlin logic.

## Future Fullstack Path

When a local-only MVP needs to become fullstack, migrate from the client-only modular structure to the server-enabled structure.

Move into `core`:

- DTOs
- shared validation rules
- API request and response models
- shared constants for API paths if useful

Add into `server`:

- Ktor routes
- services
- repositories
- database schema
- server tests

Update client-side `app/feature/*/data` modules:

- add remote data sources
- add client repositories backed by Ktor APIs
- keep local cache if selected
- add sync behavior only when explicitly needed

## V1 Scope

Include in V1:

- local CLI
- OpenCode-backed generation and repair loop
- JetBrains KMP wizard-based top-level template
- modular shared and feature Gradle structure aligned with the knowledge graph
- Android app
- iOS app with Compose UI
- desktop app for preview and MCP-driven hot reload
- user-selectable storage
- tests included by default
- feature contracts
- verification reports

Include in V1.5:

- optional Ktor backend template
- root `core` module for client and server DTOs and validation
- server database choice
- client API data source generation
- server tests
- `mvp preview` starts server and desktop together

Do not include in V1:

- web target
- cloud deployment
- native SwiftUI generation
- full visual editor
- App Store or TestFlight automation

Do not include before V2:

- cloud backend deployment
- Docker production deployment
- advanced auth
- multi-tenant backend generation
- admin dashboards unless explicitly requested

## First Milestone

The first milestone should prove this complete local-only flow:

```text
Input: "Build a simple expense tracker where users can add expenses, categorize them, and see a monthly total."

Output:
- New modular KMP project
- Android app builds
- iOS Compose app exists
- Desktop app runs
- Shared and feature tests pass
- Expense feature works with selected persistence
- Desktop preview supports MCP-driven hot reload
- Verification report is generated
```

The first backend milestone should prove this fullstack flow:

```text
Input: "Build a team task tracker where users can create tasks, assign them to teammates, and sync changes through a local backend."

Output:
- New server-enabled modular KMP project
- `core` contains shared DTOs and validation
- Ktor server runs locally
- Desktop app talks to the local server
- Android app builds
- iOS Compose app exists
- Server tests pass
- Client shared and feature tests pass
- API happy path passes
- Verification report is generated
```

## Recommended Implementation Order

1. Finalize the canonical modular Dream architecture in this document.
2. Create the modular KMP base template using the JetBrains top-level structure.
3. Add `build-logic/` and convention plugins.
4. Add `desktopApp` preview and MCP-driven hot reload support.
5. Add shared core modules.
6. Add shared design system and navigation modules.
7. Add sample feature modules.
8. Add storage option hooks.
9. Add `.mvp/` metadata format.
10. Build the CLI shell.
11. Add the OpenCode adapter.
12. Generate the first feature from a description.
13. Add verification and repair loop.
14. Validate with the expense tracker milestone.
15. Add optional Ktor server template.
16. Validate with the team task tracker backend milestone.

## Final Default Rule For Dream Agents

When generating or reviewing Dream projects, agents should default to these rules unless a section in this document explicitly overrides them:

- preserve thin app shells
- keep reusable infrastructure in shared modules
- place product behavior in feature modules
- enforce `Presentation -> Domain <- Data`
- keep domain pure Kotlin
- keep navigation centralized
- keep shared MVI primitives small
- use physical Gradle modules where architecture boundaries matter
- use desktop as the main fast preview and MCP-driven hot reload surface
- do not report success until verification is green or a blocker is clearly documented
