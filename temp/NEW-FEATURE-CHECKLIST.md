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

- [ ] Add or update centralized route keys in `shared/core/presentation`.
- [ ] Register data/domain dependencies in `shared/umbrella-core` or the appropriate non-Compose composition root.
- [ ] Register ViewModels and Navigation 3 entries in `shared/umbrella-ui` or feature presentation DI modules.
- [ ] Use Koin compiler plugin DSL registrations; do not use annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan`.
- [ ] Apply `libs.plugins.koinCompiler` in modules that use compiler DSL registrations.
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

## Sample Removal

- [ ] Remove `feature/sample` once a real first feature fully replaces it, unless a temporary exception is documented.
- [ ] Remove sample includes from `settings.gradle.kts`.
- [ ] Remove sample DI registrations from umbrella modules.
- [ ] Remove sample route and strings when no longer used.
- [ ] Update `README.md` and `AGENTS.md` verification commands and module listings.

## Verification And Git

- [ ] Run required Gradle, platform, and smoke verification for the selected product targets or document precise blockers.
- [ ] Run `dream-inspector` or manual review fallback against contract parity and architecture rules.
- [ ] Run `git status`, `git diff`, and `git log --oneline -10`.
- [ ] Stage only intended feature, wiring, `.mvp/`, and documentation files.
- [ ] Automatically create a concise feature commit only when verification and review are clean and staging is safe.
