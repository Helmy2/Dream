# Feature Implementation Prompt

Use this prompt scaffold when implementing a feature inside a Dream-generated project.

```text
Intended OpenCode agent: implementer

You are implementing a feature inside a Dream Kotlin Multiplatform project.

Your task:
Implement the requested feature using the existing Dream modular template and the feature contract.

Read first:
- `.knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/templates/implementation-report-template.md`
- `AGENTS.md`
- relevant files in `.knowledge-graph/best-practices/`
- template README and relevant modules
- `.mvp/app-contract.yaml`
- `.mvp/features/<feature-name>/contract.yaml`

Inputs from the orchestrator:
- approved feature objective
- relevant context pack if one was produced
- any known constraints or explicit user preferences

Architecture rules you must enforce:
- `Presentation -> Domain <- Data`
- domain stays pure Kotlin
- repository interfaces live in domain
- repository implementations and transport or persistence mapping live in data
- domain-to-UI mapping lives in presentation
- navigation stays centralized
- concrete Navigation 3 route keys live in the owning feature presentation module as `NavKey` types unless a route is genuinely shared
- feature UI should prefer callbacks over owning the full route graph
- multi-word feature folders, Gradle modules, and Kotlin packages must use one consistent convention; prefer package-mirroring directories such as `feature/expense/details` with `feature.expense.details`
- feature presentation modules must not depend directly on another feature presentation module; use callbacks or composition-root navigation wiring
- cross-feature data implementation reuse must be an explicit MVP exception recorded in `.mvp/decisions.yaml`, and shared ownership should be extracted to shared modules
- feature UI must use `shared/designsystem` components and `DreamTheme` tokens by default; raw Material3 or ad hoc Compose primitives require a documented gap or feature-specific reason
- shared user-visible strings should not be hardcoded
- time-sensitive behavior must flow through `TimeProvider`
- composition roots must stay stable across recomposition
- generated feature DI must use Koin compiler plugin DSL registrations, not annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan`

Implementation rules:
- reuse the template structure instead of bypassing it
- create or update `feature/<name>/domain`
- create or update `feature/<name>/data`
- create or update `feature/<name>/presentation`
- add or update tests for the feature
- prefer minimal correct changes
- keep shared infrastructure in shared modules
- map every contract action, state, and required test to code and test coverage explicitly
- map every contract entity field to domain models, persistence models, mapping code, and tests where applicable
- do not generate placeholder states unless they are implemented and testable
- do not instantiate repositories, use cases, or ViewModels directly inside recomposing composables
- do not let feature UI own routing infrastructure when callbacks or events are sufficient
- register feature-owned `NavKey` routes through Koin Navigation 3 entries; do not add feature-specific route keys to shared core by default
- for editable forms, make the primary action reachable while the software keyboard may be open; do not depend on `imePadding()` alone
- for responsive forms, use compact single-column layout on narrow screens, grouped fields on wider screens, and adaptive chip wrapping for categories/tags/filters
- prefer existing design-system components for buttons, text fields, cards, dialogs, sheets, lists, navigation chrome, progress, snackbar/banner feedback, and common selection controls before importing raw Material3 equivalents
- when the contract marks time-sensitive behavior, inject `TimeProvider`; use `nowMillis()` for persisted timestamps, `today()` for current-date behavior, `currentYearMonth()` for period filters, and `formatDate(...)` or `formatDateTime(...)` for shared UI display
- include loading, empty, content, and error state handling when the contract requires them
- do not rename contract fields during implementation; if a field name is wrong, update the contract first and then implement from the updated contract

Mandatory checks before writing any Compose code:
1. **Shared strings**: Create or update `shared/core/strings/src/commonMain/composeResources/values/strings.xml` with ALL user-visible strings for the feature BEFORE writing Compose UI. Every label, title, button, and message must use `stringResource(R.string.key)` in Compose code. No hardcoded text literals.
2. **TimeProvider injection**: Identify all time-sensitive behavior in the contract. Inject `TimeProvider` (from `shared/core/domain`) via constructor in repositories and ViewModels. Use `nowMillis()`, `today()`, `currentYearMonth()`, `formatDate(...)`, or `formatDateTime(...)` based on the behavior. After implementation, grep the feature for `Clock.System.now()` — the only allowed occurrence is inside `SystemTimeProvider`. Any other `Clock.System.now()` call is a defect.
3. **No JVM-only APIs in common code**: grep for `String.format`, `java.*`, `SimpleDateFormat` in common source sets. Replace with KMP-safe alternatives like custom `formatCurrency()` or `kotlin.uuid.Uuid.random()`.
4. **No deprecated APIs**: Verify `ExposedDropdownMenuBox.menuAnchor()` is not used. Use `MenuAnchorType` from Material3 instead.
5. **Stable composition roots**: Verify that repositories, use cases, ViewModels are assembled through DI (Koin modules) or stable `remember` blocks, never inside a `@Composable` function body.
6. **Koin compiler DSL only**: Verify generated feature modules that use compiler DSL registrations apply `libs.plugins.koinCompiler`, and do not use `@KoinViewModel`, `@Module`, `@ComponentScan`, or `libs.koin.annotations` unless the user explicitly requested annotation-based DI.
7. **Feature naming and boundaries**: Verify multi-word feature paths and packages use one convention, and verify the feature does not import another feature's presentation module directly.
8. **Editable form reachability**: For create/update forms, plan for keyboard-open action reachability and responsive compact/wide layout behavior before writing the composables.
9. **Design-system components first**: Before importing raw Material3 UI primitives, check `shared/designsystem/components`. Use the design-system wrapper when it exists; document any raw Material3 exception.

Test requirements:
- **Domain tests**: Create `feature/<name>/domain/src/commonTest/.../ModelTest.kt` — verify entity construction, enum values, default factory methods. Create `RepositoryContractTest.kt` — verify the repository interface contract is defined and documented.
- **Data tests**: When Room is the storage layer, create `feature/<name>/data/src/commonTest/.../EntityMappingTest.kt` — verify entity-to-domain and domain-to-entity mapping. Do not require Android-only in-memory Room from `commonTest`; use fake DAO/database interfaces for repository behavior unless a real KMP Room test setup is already proven in the template.
- **Repository tests**: Create `feature/<name>/data/src/commonTest/.../RepositoryImplTest.kt` — verify the repository implementation(s) using fake DAO/database boundaries or reliable KMP in-memory storage.
- **Room migrations**: For first-pass local MVP iteration, the shared Room builder may use destructive migration fallback to recover from stale local test schemas. If the contract requires preserving existing local data, add real migrations and document why destructive recovery is not acceptable.
- **Presentation tests**: Create `feature/<name>/presentation/src/commonTest/.../ViewModelTest.kt` — verify loading → content, loading → empty, loading → error transitions, action handling, and event emission.
- **Presentation tests**: Use reusable main dispatcher setup with `@BeforeTest` and `@AfterTest` when `Dispatchers.Main` is needed. Prefer Turbine or an explicit flow-testing pattern for ordered state transition assertions.
- **Time-sensitive tests**: Use `FakeTimeProvider` from `shared/core/testing` or an equivalent fixed-time fake for behavior involving current time, date filters, timestamps, formatting, or logical periods. Use `kotlinx-datetime` helpers for readable date and month construction instead of unexplained raw epoch values.
- **Test dependencies**: Ensure every module's `build.gradle.kts` declares the dependencies required by its tests, including `kotlin("test")` and `shared/core/testing` where reusable fakes are needed. Add these BEFORE writing tests.

Output requirements:
- implement the code directly
- keep code readable and modular
- if a tradeoff is needed, prefer repairability and architecture clarity over short-term convenience

Before finishing:
- summarize which modules changed
- provide a contract mapping table with one row per action, state, and required test
- include entity-field mapping rows for domain and data fields when the feature has persisted or user-entered entities
- mark each mapped item as `implemented`, `partial`, or `blocked`
- identify the code location and test coverage for each mapped item
- note any partial or blocked behavior explicitly
- write `.mvp/features/<feature-name>/implementation-report.md` using `.opencode/guidance/templates/implementation-report-template.md`

Required final report shape:
- follow `.opencode/guidance/templates/implementation-report-template.md` exactly unless the orchestrator requests a stricter variant
```
