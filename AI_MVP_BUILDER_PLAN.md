# Dream AI MVP Builder Plan

Codename: `Dream`

## Goal

Build Dream, a local, Replit-like AI MVP builder for mobile-first apps using Kotlin Multiplatform and Compose Multiplatform.

The tool should take a natural-language app or feature description, generate a working project, run tests, launch a desktop preview, read build/runtime failures, fix them, and report the verified result.

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

## Project Template Decision

We should create a reusable project template instead of generating every project from nothing.

Reason:

- The AI needs stable architecture boundaries.
- The AI should focus on feature code, not rebuilding Gradle, DI, navigation, testing, and preview infrastructure each time.
- The template gives every MVP the same verified baseline.
- The template makes tests and hot reload available from the first generated feature.
- The template makes future native UI migration possible without rewriting business logic.

The template should be generated from or aligned with the JetBrains KMP wizard:

```text
https://kmp.jetbrains.com/?android=true&ios=true&iosui=compose&includeTests=true
```

Then our builder should add the local desktop target and MVP-specific infrastructure.

If the user chooses a backend, the template should switch to the JetBrains server-capable structure with `core/`, `server/`, and nested client app modules.

## KMP Structure

Use JetBrains' new default KMP structure:

```text
project-root/
  shared/
  androidApp/
  iosApp/
  desktopApp/
```

Responsibilities:

- `shared`: Kotlin Multiplatform library containing shared app logic and shared Compose UI for V1.
- `androidApp`: Android runnable app.
- `iosApp`: iOS runnable app using Compose UI in V1.
- `desktopApp`: Desktop runnable app for preview, testing, and MCP-driven Hot Reload.

Future native UI structure:

```text
project-root/
  sharedLogic/
  sharedUI/
  androidApp/
  iosApp/
  desktopApp/
```

In that future structure:

- `sharedLogic` contains domain, data, use cases, repositories, and platform-independent presentation contracts.
- `sharedUI` contains Compose UI consumed by Android and desktop.
- `iosApp` can use SwiftUI while reusing `sharedLogic`.

Server-enabled structure:

```text
project-root/
  core/
  server/

  app/
    shared/
    androidApp/
    iosApp/
    desktopApp/
```

In the server-enabled structure:

- `core` contains models, validation, DTOs, and API contracts shared by the client and server.
- `server` contains the Ktor backend.
- `app/shared` contains KMP client logic and Compose UI.
- `app/androidApp` contains the Android runnable app.
- `app/iosApp` contains the iOS Compose runnable app.
- `app/desktopApp` contains the desktop preview and MCP-driven hot reload app.

## V1 Generated Project Layout

```text
my-mvp/
  settings.gradle.kts
  build.gradle.kts
  gradle/
  gradlew
  gradlew.bat

  shared/
    src/
      commonMain/
      commonTest/
      androidMain/
      iosMain/

  androidApp/
    src/main/

  iosApp/
    iosApp.xcodeproj
    iosApp/

  desktopApp/
    src/jvmMain/
    src/jvmTest/

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

## Server-Enabled Project Layout

When the user chooses a backend, generate this layout instead:

```text
my-mvp/
  settings.gradle.kts
  build.gradle.kts
  gradle/
  gradlew
  gradlew.bat

  core/
    src/
      commonMain/
      commonTest/

  server/
    src/
      main/
      test/

  app/
    shared/
      src/
        commonMain/
        commonTest/
        androidMain/
        iosMain/

    androidApp/
      src/main/

    iosApp/
      iosApp.xcodeproj
      iosApp/

    desktopApp/
      src/jvmMain/
      src/jvmTest/

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

Use this layout only when backend generation is enabled. Keep the simpler `shared/`, `androidApp/`, `iosApp/`, `desktopApp/` structure for local-only MVPs.

## Core Template Code

The template should include the reusable code that every MVP needs.

Core areas:

- App shell
- Navigation shell
- Design system
- Theme support
- DI setup
- Result/error model
- MVI/ViewModel base classes
- Test fakes
- Storage abstraction
- Desktop preview harness
- Sample feature
- Verification scripts or Gradle tasks

Additional server-template areas when backend is enabled:

- Ktor application entry point
- Routing setup
- Serialization setup
- Server DI setup
- API error response model
- Health route
- Feature route template
- Server service template
- Server repository template
- Server test utilities
- Local server runtime configuration

Suggested shared package layout:

```text
shared/src/commonMain/kotlin/
  core/
    domain/
      Result.kt
      AppError.kt
    data/
      DispatchersProvider.kt
    presentation/
      MviViewModel.kt
      UiMessage.kt
    storage/
      StorageDriver.kt

  design/
    AppTheme.kt
    AppColors.kt
    AppTypography.kt
    Components.kt

  navigation/
    AppRoute.kt
    AppNavigation.kt

  features/
    sample/
      domain/
      data/
      presentation/
      ui/
```

For MVP speed, V1 can keep features inside `shared/src/commonMain/kotlin/features/` instead of creating a separate Gradle module per feature.

Later, a production mode can modularize features into separate Gradle modules.

Suggested server package layout:

```text
server/src/main/kotlin/
  Application.kt
  Routing.kt
  config/
    ServerConfig.kt
  core/
    ApiErrorResponse.kt
    HealthRoutes.kt
  di/
    ServerModule.kt
  features/
    sample/
      SampleRoutes.kt
      SampleService.kt
      SampleRepository.kt
```

Suggested shared client/server core layout:

```text
core/src/commonMain/kotlin/
  api/
    ApiResult.kt
  model/
  validation/
```

## Feature Generation Structure

Each generated feature should follow this shape:

```text
features/<feature-name>/
  domain/
    model/
    repository/
    usecase/
    error/

  data/
    repository/
    mapper/
    local/
    remote/

  presentation/
    FeatureState.kt
    FeatureAction.kt
    FeatureEvent.kt
    FeatureViewModel.kt

  ui/
    FeatureScreen.kt
    FeatureComponents.kt
```

This keeps the feature ready for future native UI because business logic is not coupled to Compose UI.

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

The contract is stored in:

```text
.mvp/features/<feature-name>/contract.yaml
```

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

OpenCode responsibilities:

- Read project context
- Generate code
- Apply edits
- Fix compile/test errors
- Review generated code

MVP Engine responsibilities:

- Own project templates
- Own feature contract format
- Own commands
- Own verification gates
- Own preview lifecycle
- Store reports in `.mvp/`

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

Creation flow:

```text
1. User describes the MVP.
2. CLI asks for storage choice if needed.
3. CLI asks whether a backend is needed if the request implies accounts, sharing, collaboration, sync, admin dashboards, or remote APIs.
4. CLI creates the project from the KMP template or the server-enabled KMP template.
5. AI creates `.mvp/app-contract.yaml`.
6. AI generates the first feature.
7. CLI runs verification.
8. CLI launches desktop preview and server if enabled.
9. AI fixes failures until green or reports a real blocker.
```

## Storage Choices

The user should choose storage at project creation.

Options:

- `none`: no persistence
- `datastore`: preferences and simple key-value state
- `room`: Room KMP for local database MVPs
- `sqldelight`: SQLDelight for explicit SQL and portability

Recommended defaults:

- Use `none` if the app does not need persistence.
- Use `datastore` for settings, flags, onboarding state, and simple preferences.
- Use `room` for mobile-first local database apps.
- Use `sqldelight` when the user prefers SQL-first modeling.

## Backend Choices

Backend generation should be optional.

Options:

- `none`: no backend, local-only MVP
- `ktor`: Kotlin Ktor backend
- `mock`: generated fake backend for prototype-only flows

Recommended default:

- Use `none` unless the app needs accounts, sharing, collaboration, remote sync, admin tools, or multi-device data.
- Use `ktor` for real fullstack MVPs.
- Use `mock` when the user wants to prototype API-backed UI without building the real server yet.

Server database choices:

- `none`: stateless API or mock data only
- `memory`: in-memory repository for demos/tests
- `sqlite`: local server database for MVPs
- `postgres`: production-style database for later fullstack projects

Recommended server defaults:

- Use `memory` for prototypes and tests.
- Use `sqlite` for local fullstack MVPs.
- Use `postgres` only when the project is explicitly moving toward production/deployment.

Recommended Ktor stack:

- Ktor server
- kotlinx.serialization
- Koin for DI
- Exposed or SQLDelight for server persistence
- SQLite as the default local server database
- Ktor server test host for route tests

For V1.5, prefer Ktor + SQLite + Exposed for fastest CRUD generation. Add SQLDelight as an option when shared SQL modeling is useful.

## Desktop Preview And MCP-Driven Hot Reload

The desktop app is the local preview surface.

Use it for:

- Fast app launch
- MCP-driven Hot Reload
- Fake data previews
- Manual UI testing
- Visual iteration

The template should include preview states for generated screens:

- Loading
- Empty
- Content
- Error
- Long content
- Dark mode
- Small window
- Large window

MCP-driven Hot Reload (using Model Context Protocol) should be wired to `desktopApp`, not Android.

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

Minimum generated tests:

- Domain model tests
- Use case tests
- Repository fake tests
- ViewModel state/action tests
- Mapper tests
- Persistence tests when storage is enabled

Additional backend tests when Ktor is enabled:

- Route tests
- Service tests
- Repository tests
- Request/response serialization tests
- Validation tests
- API error mapping tests

Minimum verification commands:

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:run
```

Server-enabled verification commands:

```bash
./gradlew :server:test
./gradlew :server:build
./gradlew :app:shared:allTests
./gradlew :app:androidApp:assembleDebug
./gradlew :app:desktopApp:run
```

iOS verification when available:

```bash
./gradlew iosSimulatorArm64Test
```

The AI should not report success until checks pass or a clear blocker is documented.

## AI Repair Loop

The local builder should behave like this:

```text
Generate code
Run formatter/build/tests
Read errors
Fix errors
Run again
Launch desktop preview
Launch server if backend is enabled
Write verification report
```

The verification report should be stored in:

```text
.mvp/verification-report.md
```

## V1 Scope

Include in V1:

- Local CLI
- OpenCode-backed generation and repair loop
- JetBrains KMP wizard-based template
- Android app
- iOS app with Compose UI
- Desktop app for preview and MCP-driven hot reload
- User-selectable storage
- Tests included by default
- Feature contracts
- Verification reports

Include in V1.5:

- Optional Ktor backend template
- Shared `core` module for client/server DTOs and validation
- Server database choice
- Client API data source generation
- Server tests
- `mvp preview` starts server and desktop together

Do not include in V1:

- Web target
- Cloud deployment
- Native SwiftUI generation
- Full visual editor
- App Store or TestFlight automation

Do not include before V2:

- Cloud backend deployment
- Docker production deployment
- Advanced auth
- Multi-tenant backend generation
- Admin dashboards unless explicitly requested

## Future Native UI Path

When a generated MVP needs to become a real production app with native UI, migrate from:

```text
shared/
```

To:

```text
sharedLogic/
sharedUI/
```

Move into `sharedLogic`:

- Domain models
- Repository interfaces
- Use cases
- Data implementations
- Persistence
- Validation
- Stable presentation contracts if desired

Keep in `sharedUI`:

- Compose screens
- Compose components
- Compose navigation helpers

Then `iosApp` can replace Compose screens with SwiftUI while still reusing shared Kotlin logic.

## Future Fullstack Path

When a local-only MVP needs to become fullstack, migrate from:

```text
shared/
androidApp/
iosApp/
desktopApp/
```

To:

```text
core/
server/
app/shared/
app/androidApp/
app/iosApp/
app/desktopApp/
```

Move into `core`:

- DTOs
- Shared validation rules
- API request/response models
- Shared constants for API paths if useful

Add into `server`:

- Ktor routes
- Services
- Repositories
- Database schema
- Server tests

Update `app/shared`:

- Add remote data sources
- Add client repositories backed by Ktor APIs
- Keep local cache if selected
- Add sync behavior only when explicitly needed

## First Milestone

The first milestone should prove this complete flow:

```text
Input: "Build a simple expense tracker where users can add expenses, categorize them, and see a monthly total."

Output:
- New KMP project
- Android app builds
- iOS Compose app exists
- Desktop app runs
- Tests pass
- Expense feature works with selected persistence
- Desktop preview supports MCP-driven hot reload
- Verification report is generated
```

The first backend milestone should prove this fullstack flow:

```text
Input: "Build a team task tracker where users can create tasks, assign them to teammates, and sync changes through a local backend."

Output:
- New server-enabled KMP project
- `core` contains shared DTOs and validation
- Ktor server runs locally
- Desktop app talks to the local server
- Android app builds
- iOS Compose app exists
- Server tests pass
- Client tests pass
- API happy path passes
- Verification report is generated
```

## Recommended Implementation Order

1. Create the KMP project template.
2. Add `desktopApp` and MCP-driven hot reload support.
3. Add core architecture code.
4. Add storage option hooks.
5. Add `.mvp/` metadata format.
6. Build the CLI shell.
7. Add OpenCode adapter.
8. Generate the first feature from a description.
9. Add verification and repair loop.
10. Validate with the expense tracker milestone.
11. Add optional Ktor server template.
12. Validate with the team task tracker backend milestone.
