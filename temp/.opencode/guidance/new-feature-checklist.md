# New Feature Checklist

Use this checklist when adding or replacing a Dream feature module.

## Contract First

- [ ] Create or update `.features/<feature-name>/contract.yaml` before implementation starts.
- [ ] Define entity fields, action inputs, screen states, time-sensitive behavior, design expectations, and tests by layer.
- [ ] Use meaningful timestamp field names with units, such as `createdAtMillis`, `occurredAtMillis`, or `dueAtMillis`.
- [ ] Mark `timeSensitiveBehavior.usesTimeProvider: true` for current date, logical period, timestamp, or date-filter behavior.

## Feature Modules

- [ ] Add `feature/<feature-name>/domain/build.gradle.kts`.
- [ ] Add `feature/<feature-name>/data/build.gradle.kts` when persistence, network, or platform data is needed.
- [ ] Add `feature/<feature-name>/api/build.gradle.kts` with `NavKey` route types and any minimal public contracts; no Koin definitions in `api`.
- [ ] Add `feature/<feature-name>/impl/build.gradle.kts` with screens, ViewModels, UI state/actions/events, and the Koin module that installs Navigation 3 entry providers.
- [ ] For multi-word features, keep folder paths, Gradle module names, and Kotlin packages aligned; prefer package-mirroring directories such as `feature/expense/details` with `feature.expense.details`.
- [ ] Add feature includes to `settings.gradle.kts`.
- [ ] Keep repository interfaces in domain and implementations in data.
- [ ] Keep domain-to-UI mapping in `impl`.
- [ ] If reusing another feature's domain or data layer, document the reason in `.features/decisions.yaml`; extract to shared modules when ownership is equal or repeated.
- [ ] No module outside this feature should depend on `feature/<feature-name>/impl`; callers use `api` only.

## Wiring

- [ ] Add `NavKey` route types in `feature/<feature-name>/api`.
- [ ] Install Navigation 3 entry providers in the Koin module inside `feature/<feature-name>/impl` using `navigation<Route> { FeatureScreen() }`.
- [ ] Keep shared navigation centralized in `shared/navigation`; it must not depend on feature `api` or `impl` modules.
- [ ] Import feature `impl` Koin modules in `shared/umbrella-ui` so that navigation entry providers are registered before `NavDisplay` renders.
- [ ] Register data/domain dependencies in `shared/umbrella-core` or the appropriate non-Compose composition root.
- [ ] Use Koin compiler plugin DSL registrations; do not use annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan`.
- [ ] Apply `libs.plugins.koinCompiler` in modules that use compiler DSL registrations.
- [ ] Call shared guarded Koin startup from host apps; do not call `startKoin` directly from multiple platform entry points.
- [ ] Keep repositories, use cases, and ViewModels out of recomposing composable bodies.
- [ ] Add all user-visible strings to `shared/core/strings` before writing UI text.
- [ ] Do not let `feature/<name>/impl` depend directly on another feature's `impl` module; cross-feature navigation goes through the target feature's `api` only.

## Design System

- [ ] Use `shared/designsystem` components before raw Material3 or ad hoc Compose UI primitives.
- [ ] Use `DreamTheme` tokens for spacing, shape, size, and color decisions.
- [ ] Prefer design-system wrappers for buttons, text fields, cards, dialogs, sheets, lists, navigation chrome, progress, snackbar/banner feedback, and common selection controls.
- [ ] Document any raw Material3 exception with the missing wrapper behavior or feature-specific reason.
- [ ] Do not create one-off feature-local visual primitives when a shared design-system component covers the need.

## Responsive Forms And IME

- [ ] For editable forms, keep the primary action reachable while the software keyboard may be open.
- [ ] Use compact single-column form layout on narrow screens.
- [ ] Use wider grouped layout only when it keeps labels, validation, and primary actions understandable.
- [ ] Use adaptive wrapping for chips, categories, tags, and filters instead of rigid fixed rows.
- [ ] Verify create/update success by outcome state, updated summary, or list/detail content rather than keyboard-hidden assertions alone.

## Storage And Time

- [ ] Keep shared Room builder policy in shared core modules.
- [ ] Put feature-specific entities, DAOs, and mappings in the feature data layer unless the schema is intentionally shared.
- [ ] Inject `TimeProvider` and call the appropriate API: `nowMillis()` for timestamps, `today()` for current date, `currentYearMonth()` for period filters, and `formatDate(...)` or `formatDateTime(...)` for shared UI display.
- [ ] Use `FakeTimeProvider` or an equivalent fixed-time fake in time-sensitive tests.
- [ ] Do not add feature-local date/date-time formatting helpers in common code.

## Tests

- [ ] Add domain tests for model construction, value rules, and repository contracts.
- [ ] Add data tests for entity/domain mapping and repository behavior using fake DAO/database boundaries unless real KMP Room tests are already proven.
- [ ] Add presentation tests for loading, empty, content, error, action handling, and event emission for the `impl` ViewModel.
- [ ] Ensure required `commonTest` dependencies are present before writing tests.
- [ ] Prefer reusable coroutine test setup with `@BeforeTest` main dispatcher setup and `@AfterTest` reset when a ViewModel test needs `Dispatchers.Main`.
- [ ] Use Turbine or another explicit flow-testing pattern for state transition assertions when it improves readability over direct `state.value` checks.
- [ ] Use `kotlinx-datetime` helpers for meaningful month, date, or time cases instead of unexplained raw epoch values.

## iOS SwiftUI Host

Apply these items only when `iosUiStrategy` is `swiftui`:

- [ ] Create or update SwiftUI views for each feature screen in `iosApp/<FeatureName>/Views/`, matching the feature contract screens.
- [ ] Create or update `@Observable` ViewModels in `iosApp/<FeatureName>/ViewModels/` that wrap bridge calls and expose native state.
- [ ] Create or update Swift models in `iosApp/<FeatureName>/Models/` when the feature needs Swift-side value types or form state.
- [ ] Extend the bridge layer (`Core/SharedCoreBridge` or equivalent protocol) with methods that expose new shared capabilities to Swift.
- [ ] Add or update XCTest coverage in `iosAppTests/<FeatureName>/` for native ViewModel state transitions and bridge wiring.
- [ ] Verify the shared Xcode scheme includes all new files and the test target discovers them.
- [ ] Run `xcodebuild build` and `xcodebuild test` as part of structural validation for the iOS host.

## Sample Removal

- [ ] Remove `feature/sample` once a real first feature fully replaces it, unless a temporary exception is documented.
- [ ] Remove sample includes from `settings.gradle.kts`.
- [ ] Remove sample DI registrations from umbrella modules.
- [ ] Remove sample route keys, navigation entries, and strings when no longer used.
- [ ] Remove sample Room schema exports from `shared/core/data/schemas` unless intentionally retained for a migration test.
- [ ] Update `README.md` and `AGENTS.md` verification commands and module listings.
- [ ] Grep for leftover sample names, route names, module names, string keys, database names, schema paths, and docs claims before review.

## Preflight

- [ ] Verify the generated project is a git repository before feature work depends on git commands.
- [ ] Run `./scripts/dream-preflight.sh` from the project root and record any blockers.
- [ ] Verify `git status`, `git diff`, and `git log --oneline -10` work before the commit stage.
- [ ] Verify Android SDK readiness early with `ANDROID_HOME` or the configured SDK path before Android verification is treated as code-ready.
- [ ] Verify Maestro is installed or document it as an environment blocker before smoke validation.
- [ ] Create or update feature-owned Maestro smoke files under `maestro/features/<feature-name>/android.yaml` and `maestro/features/<feature-name>/ios.yaml` for every user-visible feature supported on those platforms.
- [ ] Do not rely on one app-level/global smoke file to cover this feature.
- [ ] Use `.opencode/guidance/templates/maestro-smoke-checklist-template.md` when replacing starter smoke-flow placeholders.
- [ ] Before running Maestro, boot/open the target emulator or simulator, install the app, and confirm platform launch is healthy.
- [ ] Start each Maestro flow with `clearState` before `launchApp` unless the contract explicitly tests persisted state across launches.
- [ ] For iOS single-line text replacement in Maestro, prefer `tapOn`, `eraseText`, `inputText`, `pressKey: Enter`, then the save action.
- [ ] For detail smoke assertions, prefer full rendered-row regex selectors such as `Occurred: .*` and `Created: .*` instead of standalone labels.
- [ ] If Android physical-device Maestro fails at `launchApp` while `adb install` and `adb shell am start` work, record a Maestro/device-driver blocker and try emulator-backed Maestro when feasible.
- [ ] For first-pass local Room schema changes, either provide a real migration or document/apply destructive recovery for MVP iteration on Android, iOS, and Desktop.

## Verification And Git

- [ ] Run required Gradle, platform, and smoke verification for the selected product targets or document precise blockers.
- [ ] Record verification in explicit buckets: passed, blocked by environment, not run, and not applicable.
- [ ] Run `dream-inspector` or manual review fallback against contract parity and architecture rules.
- [ ] Run `git status`, `git diff`, and `git log --oneline -10`.
- [ ] Stage only intended feature, wiring, `.features/`, and documentation files.
- [ ] Prepare a concise feature commit only when verification and review are clean and staging is safe, then ask the user for explicit approval before committing.
- [ ] Write `.features/<feature-name>/retrospective.md` using `template-improvement-retrospective-template.md`.
