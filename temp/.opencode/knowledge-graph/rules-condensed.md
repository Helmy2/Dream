# Dream Rules (Condensed)

Quick-reference rules for Dream agents. Load specific files for details.

## Architecture [file:03]

- [arch-1] Package by feature, not by technical layer alone
- [arch-2] Split features into `domain`, `data`, `api`, `impl`
- [arch-3] Enforce `Presentation -> Domain <- Data`
- [arch-4] Keep domain pure Kotlin — no platform dependencies
- [arch-5] Use physical Gradle modules to enforce dependency direction

## Feature Module [file:05]

- [feat-1] `domain`: entities, repository interfaces, use cases, domain errors
- [feat-2] `data`: repository implementations, DAOs, DTOs, mappers, remote/local sources
- [feat-3] `api`: NavKey route types only — no screens, ViewModels, Koin definitions
- [feat-4] `impl`: screens, ViewModels, UI models, mappers, Koin module with navigation entries
- [feat-5] Multi-word features: consistent naming across folders, Gradle modules, packages
- [feat-6] PresentationMapper: inject via Koin `factory` into ViewModel, not in composables
- [feat-7] UI state must hold UI models, never domain types

## Dependency Rules [file:06]

- [dep-1] `feature:x:domain` depends only on `shared:core:domain`
- [dep-2] `feature:x:data` depends on its own `domain` + shared runtime
- [dep-3] `feature:x:api` depends only on shared navigation (NavKey)
- [dep-4] `feature:x:impl` depends on its own `api`, `domain`, shared presentation, design system
- [dep-5] No module outside a feature depends on `feature:x:impl`
- [dep-6] Other features navigate via `feature:x:api` only
- [dep-7] Aggregator exception: dashboard may depend on other features' `domain` only, never `impl`
- [dep-8] Cross-feature data coupling is an exception, not standard

## Domain [file:09]

- [dom-1] Domain models describe business meaning, not storage shape
- [dom-2] Use typed result wrappers: `sealed interface Result<out D, out E : DomainError>`
- [dom-3] Always rethrow `CancellationException` in suspend helpers
- [dom-4] Name use cases after business intent: `ObserveX`, `SetX`, `ToggleX`, `MarkX`
- [dom-5] Time access through `TimeProvider` only — no direct `Clock.System.now()`

## Data [file:10]

- [dat-1] Repositories: map DTOs→domain, convert failures→domain errors
- [dat-2] Always rethrow `CancellationException` — never swallow coroutine cancellation
- [dat-3] Prefer cache-first reads for offline-first/low-churn features
- [dat-4] Per-feature `HttpClient` instances when service behavior differs
- [dat-5] Mappers at boundaries: DTO↔entity↔domain in data, domain→UI in presentation
- [dat-6] Feature mappers belong in `feature/<name>/data/mapper/`, not `shared/core/data/mapper/`

## Presentation [file:14]

- [pres-1] Separate: `UiState` (long-lived), `UiAction` (user inputs), `UiEvent` (one-off effects)
- [pres-2] Keep MVI base small — provide only primitives teams actually use
- [pres-3] Prefer lifecycle-backed `ViewModel` over plain classes
- [pres-4] Queue actions sequentially through one channel — avoid one coroutine per action
- [pres-5] Split screens: `XxxScreen` (smart container) + `XxxContent` (pure presentation)
- [pres-6] `XxxContent` accepts only state, event callback, modifier — no ViewModel dependency
- [pres-7] UiState must hold UI models, not domain types

## Navigation [file:15]

- [nav-1] Central app shell owns navigation, theme, safe-area handling
- [nav-2] Routes as typed `NavKey` values in `feature/<name>/api`
- [nav-3] `Navigator` owns mutable back stack with `goTo()` and `goBack()`
- [nav-4] Koin navigation entries in `feature/<name>/impl`, not shared core or app shell
- [nav-5] Other modules → `feature/<name>/api` (routes) → `feature/<name>/impl` (entries) → `shared/umbrella-ui` (aggregates)

## Resources [file:17]

- [res-1] All shared user-visible strings from `shared/core/strings` Compose Resources
- [res-2] No hardcoded strings in shared UI composables
- [res-3] Strings-first workflow: define ALL strings in `strings.xml` before writing UI
- [res-4] Naming: `<feature>_<purpose>` (e.g., `expense_title`, `expense_add_button`)

## Dependency Injection [file:18]

- [di-1] `startKoin` only from top-level app init (Android: `Application.onCreate()`)
- [di-2] Koin compiler plugin DSL — no `@KoinViewModel`, `@Module`, `@ComponentScan`
- [di-3] `viewModel<T>()` syntax — not `viewModel { }` lambda (unsupported by compiler plugin)
- [di-4] `factory { }` for stateless helpers (mappers), `single { }` for repositories
- [di-5] Feature `impl` Koin modules install Navigation 3 entry providers
- [di-6] `shared/umbrella-ui` aggregates all feature Koin modules before `NavDisplay` renders
- [di-7] Verify DI graph: `./gradlew :shared:umbrella-ui:allTests`

## Composition Root [file:30]

- [comp-1] Do not instantiate repositories/ViewModels inside recomposing composables
- [comp-2] Root composables retrieve DI-managed ViewModels, don't build them
- [comp-3] Trigger initial loading from stable lifecycle-aware entry point

## Testing [file:23]

- [test-1] Inject `TimeProvider` — use `FakeTimeProvider` in tests
- [test-2] Keep domain pure for easy unit testing
- [test-3] Typed results and repository interfaces reduce mocking complexity
- [test-4] Keep shared test utilities in `shared/core/testing`
- [test-5] Use Mokkery for KMP-native mocking (auto-applied via convention plugin)
- [test-6] Domain tests + data tests required for every feature (not just presentation)

## iOS Project Structure [file:35]

- [ios-1] Native SwiftUI: group by feature bucket (`Sample/Views/`, `Sample/ViewModels/`)
- [ios-2] `Core/` for bridge code and app-level dependency assembly only
- [ios-3] Compose iOS host: keep intentionally thin (`iOSApp.swift`, `ContentView.swift`)

## iOS KMP Bridge [file:36]

- [ios-bridge-1] One dedicated bridge per native host surface
- [ios-bridge-2] Wrap Kotlin APIs with `async throws` for Swift consumption
- [ios-bridge-3] Trust SKIE's Swift surface — verify generated names, don't guess ObjC names
- [ios-bridge-4] Export transitive shared types with `export(...)` and `api(...)`
- [ios-bridge-5] Cast `KotlinDouble` explicitly in Swift: `Double(truncating: value as NSNumber)`
- [ios-bridge-6] Run `ruby scripts/sync-pbxproj.rb` after adding/removing `.swift` files

## iOS String Localization [file:39]

- [ios-str-1] xcstrings keys must match `strings.xml` names; English values must match
- [ios-str-2] Format specifiers: `%1$s` (XML) → `%@` (xcstrings)
- [ios-str-3] iOS-only strings use `ios_` infix in key
- [ios-str-4] No hardcoded user-visible strings in SwiftUI views

## Anti-Patterns [file:26]

- [anti-1] No cross-feature data-module coupling (move to shared core)
- [anti-2] No raw preference parsing in UI/domain (map at repository boundary)
- [anti-3] No direct `Clock.System.now()` (use `TimeProvider`)
- [anti-4] No hardcoded shared UI strings (use `shared/core/strings`)
- [anti-5] No route leakage into features (keep in `api` only)
- [anti-6] No dependency assembly in recomposing composables
- [anti-7] No giant convention plugins (keep narrow)
- [anti-8] No overbuilt MVI framework (keep base small)
- [anti-9] No `String.format`/`java.text.*`/`java.util.*` in `commonMain`
- [anti-10] No `viewModel { }` lambda in Koin compiler plugin DSL
- [anti-11] No hardcoded `SystemTimeProvider()` in composables — inject via Koin
