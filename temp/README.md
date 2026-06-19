# Dream KMP Base Template

This template follows JetBrains' new top-level Kotlin Multiplatform app structure and Dream's modular internal architecture.

This `temp` folder is the canonical starter project that Dream's initializer copies and renames. It is self-contained for OpenCode work: Dream agents, commands, guidance, and graph rules are packaged inside hidden project folders.

Packaged Dream context:

- `.opencode/opencode.json`: OpenCode instructions and MCP hot reload config
- `.opencode/agents/`: Dream agent definitions
- `.opencode/commands/`: Dream slash-command workflows
- `.opencode/guidance/`: operating guidance and prompt scaffolds
- `.knowledge-graph/`: architecture graph and best-practice rules
- `.mvp/`: generated app contracts, decisions, feature contracts, and verification reports

Modules:

- `androidApp`: Android host app
- `iosApp`: iOS host app that consumes the exported `Shared` framework
- `desktopApp`: Desktop host app and MCP hot reload integration point; use `:desktopApp:hotRunJvm` when running it interactively
- `shared/core/*`: split shared infrastructure modules
- `shared/core/database`: shared Room builder defaults for bundled SQLite and KMP-safe query dispatch
- `shared/core/strings`: Compose Resources string catalog exported for feature UI
- `shared/designsystem`: theme, tokens, and reusable components
- `shared/navigation`: centralized shell, Navigator, and Navigation 3 display infrastructure
- `shared/umbrella-core`: non-Compose shared business logic aggregation and data/domain dependency wiring for SwiftUI or other native UI consumers
- `shared/umbrella-ui`: exported Compose UI composition root, Koin startup, Navigator wiring, and Koin Navigation 3 entry aggregation
- `feature/sample/*`: sample feature split into `domain`, `data`, and `presentation`
- `build-logic`: Dream convention plugins used by the modular template

Key architecture rules:

- thin app shells
- `Presentation -> Domain <- Data`
- domain stays pure Kotlin
- shared core is split by responsibility
- navigation stays centralized

Template baseline:

- Koin compiler plugin is the default DI path for new modules
- shared screen state uses lifecycle `ViewModel` plus Koin ViewModel wiring at stable navigation/composition roots
- app navigation uses Navigation 3 typed routes, a Koin-provided `Navigator`, `NavDisplay`, and Koin `navigation<Route>` entry providers
- canonical top-level route keys live in `shared/core/presentation` so feature presentation and shared navigation use one route type
- the shared MVI base processes actions sequentially instead of launching one coroutine per action
- the sample feature uses Room for local persisted data on Android, iOS, and Desktop
- common Room builder setup lives in `shared/core/database` and uses a bundled SQLite driver plus `Dispatchers.Default` for KMP-safe query execution
- user-visible sample copy comes from `shared/core/strings` Compose Resources instead of hardcoded composable text
- shared result helpers preserve `CancellationException` instead of swallowing structured concurrency
- `shared/core/testing` is the reusable home for coroutine-aware test helpers
- app shells start Koin once and pass only platform factories into shared code
- `shared/umbrella-core` stays free of Compose and Navigation 3 UI dependencies; `shared/umbrella-ui` owns Compose startup and navigation display wiring
- the sample UI demonstrates shared loading, empty, content, and error-state components
- the shared shell applies safe-area padding and token-backed theme styling
- Android, iOS, and contract-selected Desktop are product runtime targets; Desktop validation does not replace Android/iOS readiness checks

Verification commands:

```bash
./gradlew :feature:sample:presentation:allTests :feature:sample:data:compileKotlinDesktop :shared:navigation:allTests :shared:umbrella-core:allTests :shared:core:domain:allTests :shared:core:data:allTests :shared:core:presentation:allTests :shared:core:storage:allTests :shared:core:testing:allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:compileKotlinJvm
# Interactive Desktop validation when Desktop is selected:
./gradlew :desktopApp:hotRunJvm
```
