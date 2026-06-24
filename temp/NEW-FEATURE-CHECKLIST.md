# New Feature Checklist

Use this checklist when adding or replacing a Dream feature module.

## Contract First

- [ ] Create or update `.mvp/features/<feature-name>/contract.yaml` before implementation starts.
- [ ] Define entity fields, action inputs, screen states, time-sensitive behavior, design expectations, and tests by layer.
- [ ] Use meaningful timestamp field names with units, such as `createdAtMillis`, `occurredAtMillis`, or `dueAtMillis`.
- [ ] Mark `timeSensitiveBehavior.usesTimeProvider: true` for current date, logical period, timestamp, or date-filter behavior.

## Feature Modules

- [ ] Add `feature/<feature-name>/domain/build.gradle.kts`.
- [ ] Add `feature/<feature-name>/data/build.gradle.kts` when persistence, network, or platform data is needed.
- [ ] Add `feature/<feature-name>/presentation/build.gradle.kts`.
- [ ] Add feature includes to `settings.gradle.kts`.
- [ ] Keep repository interfaces in domain and implementations in data.
- [ ] Keep domain-to-UI mapping in presentation.

## Wiring

- [ ] Add concrete Navigation 3 route keys in the owning feature presentation module using `androidx.navigation3.runtime.NavKey`.
- [ ] Keep shared navigation centralized in `shared/navigation`, but do not put feature-specific route definitions in shared core unless there is a concrete cross-feature reason.
- [ ] Register data/domain dependencies in `shared/umbrella-core` or the appropriate non-Compose composition root.
- [ ] Register ViewModels and Navigation 3 entries in `shared/umbrella-ui` or feature presentation DI modules.
- [ ] Use Koin compiler plugin DSL registrations; do not use annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan`.
- [ ] Apply `libs.plugins.koinCompiler` in modules that use compiler DSL registrations.
- [ ] Call shared guarded Koin startup from host apps; do not call `startKoin` directly from multiple platform entry points.
- [ ] Keep repositories, use cases, and ViewModels out of recomposing composable bodies.
- [ ] Add all user-visible strings to `shared/core/strings` before writing UI text.

## Storage And Time

- [ ] Keep shared Room builder policy in shared core modules.
- [ ] Put feature-specific entities, DAOs, and mappings in the feature data layer unless the schema is intentionally shared.
- [ ] Inject `TimeProvider` and call the appropriate API: `nowMillis()` for timestamps, `today()` for current date, `currentYearMonth()` for period filters, and `formatDate(...)` or `formatDateTime(...)` for shared UI display.
- [ ] Use `FakeTimeProvider` or an equivalent fixed-time fake in time-sensitive tests.
- [ ] Do not add feature-local date/date-time formatting helpers in common code.

## Tests

- [ ] Add domain tests for model construction, value rules, and repository contracts.
- [ ] Add data tests for entity/domain mapping and repository behavior using fake DAO/database boundaries unless real KMP Room tests are already proven.
- [ ] Add presentation tests for loading, empty, content, error, action handling, and event emission.
- [ ] Ensure required `commonTest` dependencies are present before writing tests.
- [ ] Prefer reusable coroutine test setup with `@BeforeTest` main dispatcher setup and `@AfterTest` reset when a ViewModel test needs `Dispatchers.Main`.
- [ ] Use Turbine or another explicit flow-testing pattern for state transition assertions when it improves readability over direct `state.value` checks.
- [ ] Use `kotlinx-datetime` helpers for meaningful month, date, or time cases instead of unexplained raw epoch values.

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
- [ ] Create or update per-feature Maestro smoke-flow scaffolding for Android and iOS user-visible journeys.
- [ ] Use `.opencode/guidance/templates/maestro-smoke-checklist-template.md` when replacing starter smoke-flow placeholders.
- [ ] For first-pass local Room schema changes, either provide a real migration or document/apply destructive recovery for MVP iteration on Android, iOS, and Desktop.

## Verification And Git

- [ ] Run required Gradle, platform, and smoke verification for the selected product targets or document precise blockers.
- [ ] Record verification in explicit buckets: passed, blocked by environment, not run, and not applicable.
- [ ] Run `dream-inspector` or manual review fallback against contract parity and architecture rules.
- [ ] Run `git status`, `git diff`, and `git log --oneline -10`.
- [ ] Stage only intended feature, wiring, `.mvp/`, and documentation files.
- [ ] Automatically create a concise feature commit only when verification and review are clean and staging is safe.
