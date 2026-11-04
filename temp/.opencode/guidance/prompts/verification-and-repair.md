# Verification And Repair Prompt

Use this prompt scaffold when verifying or repairing a Dream-generated project.

```text
Primary OpenCode agents:
- dream-general for orchestration and reruns
- dream-tester for verification execution
- dream-implementer, dream-implementer-android, or dream-implementer-ios for fixes (per failing layer)
- dream-inspector for final QA gate on substantial work

You are verifying and repairing a Dream-generated Kotlin Multiplatform project.

Your task:
Run the required verification flow, diagnose failures, repair them while preserving architecture rules, and produce a final verification result.

Read first:
- `.opencode/knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `AGENTS.md`
- relevant graph-backed rules for the failing module or runtime surface
- `specs/app-contract.yaml`
- `specs/decisions.yaml`
- `specs/<feature-name>/contract.yaml` if feature-specific
- `.opencode/guidance/templates/verification-report-template.md`

Required verification for local-only MVPs:
- `./scripts/dream-preflight.sh` when present
- early git readiness: project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work
- early Android SDK readiness: `ANDROID_HOME` or the configured SDK path is available before Android validation is treated as code-ready
- `./kotlin test --platform=android` + `./kotlin test --platform=iosSimulatorArm64`
- `./kotlin build -m androidApp --platform=android`
- Android CLI install, launch, layout/screenshot inspection, and log capture for Android (`https://developer.android.com/tools/agents/android-cli`)
- `./kotlin build -m iosApp --platform=iosSimulatorArm64` plus `xcrun simctl` install, launch, log, and crash capture for iOS (bundle id `com.dream.template.ios`)

Required behavior:
 - do not report validated success until verification is green, contract behavior is implemented, required tests exist, and review status is documented
- preserve `Presentation -> Domain <- Data`
- do not fix failures by collapsing module boundaries
- do not move business logic into host apps as a shortcut
- do not ignore hardcoded shared UI strings, direct system time access, or swallowed `CancellationException`
- do not treat green build commands alone as proof that the generated feature matches its contract

Required parity checks before validated success:
- every contract state is implemented in code and represented in the UI state model
- every contract entity field maps to domain models and data mappings without silent renames
- every contract action maps to a concrete handling path
- every required contract test appears in the test plan and is backed by code-level coverage
- multi-word feature folders and packages use one consistent naming convention
- `feature/<name>-api` contains only `NavKey` route types and minimal public contracts; no Koin definitions
- `feature/<name>-impl` is not imported by any other feature or by shared navigation; cross-feature navigation goes through `feature/<name>-api`
- cross-feature data implementation reuse is documented as an MVP exception or extracted to a shared module
- editable-form primary actions remain reachable while the software keyboard may be open
- responsive forms use compact single-column, wider grouped layout, and adaptive wrapping where applicable
- feature UI uses `shared/designsystem` components and `DreamTheme` tokens by default, with raw Material3 or ad hoc primitives documented as exceptions
- time-sensitive behavior uses the appropriate `TimeProvider` API instead of direct system time access or feature-local formatting
- meaningful date/month/time tests use readable `kotlinx-datetime` construction rather than unexplained raw epoch values
- repositories, use cases, and ViewModels are not instantiated inside recomposing composables
- generated feature DI uses Koin compiler plugin DSL registration, not annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan`
- feature-specific Navigation 3 route types are owned by `feature/<name>-api` and implement `NavKey`; Navigation 3 entry providers are installed in the Koin module inside `feature/<name>-impl`; shared navigation should not regain a shared `AppRoute` hierarchy without a concrete reason
- the native host keeps bridge code in `Core` and native state holders in `ViewModels`
- shared types needed by Swift are reachable from `iosApp`'s dependencies (via `//shared/umbrella-core`) so they appear in `KotlinModules`

Automated defect scans (run these BEFORE signing off):
1. **Grep for `Clock.System.now()`** in feature source files. The ONLY allowed occurrence is in `TimeProvider` implementations (e.g., `shared/core-domain/TimeProvider.kt` or an `expect/actual` platform time provider). Any other usage is a defect — flag it.
2. **Grep for hardcoded user-visible strings** in Compose UI files. Look for string literals passed to `Text()`, `TextField()`, `Button()`, `contentDescription`, or similar UI parameters that are not wrapped in `stringResource()`. Flag any hardcoded visible text.
3. **Grep for `String.format`** or `java.text.*` or `java.util.*` in the common `src` source set (not `src@android`). These are JVM-only APIs that break iOS. Flag them.
4. **Grep for `menuAnchor()`** — this Material3 API is deprecated. Flag for migration to `MenuAnchorType`.
5. **Verify test coverage by layer**: Check that domain, data, and presentation modules each have at least one test file under `test/`. A feature with only presentation tests but no domain or data tests is incomplete — flag it.
6. **Verify shared strings XML exists**: Check that `shared/core-strings/composeResources/values/strings.xml` contains string resources for the feature. Cross-reference with the feature's UI to ensure every user-visible string has a resource entry.
7. **Verify DI setup**: Check that the feature's DI module registers repositories, ViewModels, and navigation entries through Koin DSL (not manual construction). Verify that repositories and ViewModels are not instantiated inside `@Composable` functions.
8. **Verify date formatting**: Check user-visible date/date-time text in common UI code uses `TimeProvider.formatDate(...)` or `TimeProvider.formatDateTime(...)`. Flag feature-local date formatting helpers.
9. **Verify Koin compiler DSL setup**: Grep generated feature modules for `@KoinViewModel`, `@Module`, `@ComponentScan`, and `koin.annotations`. Flag any usage unless the user explicitly requested annotation-based DI. Verify modules using compiler DSL registrations apply the Koin compiler plugin (`compilerPlugins` in `module.yaml`, with a unique `moduleId`).
10. **Verify git completion for feature flows**: After validated feature work, run `git status`, `git diff`, and `git log --oneline -10`; stage only intended files, prepare a concise feature commit, and ask the user for explicit approval before committing. Do not commit blocked, partial, unreviewed, or unsafe worktree states.
11. **Verify design-system usage**: Scan feature Compose code for raw Material3 imports such as `Button`, `OutlinedTextField`, `Card`, `AlertDialog`, `ModalBottomSheet`, `Snackbar`, `TopAppBar`, and progress indicators. If a Dream design-system wrapper exists, using the raw primitive is a defect unless a local exception is documented.
12. **Verify SwiftUI host wiring**: Verify `iosApp/module.xcodeproj` still has `KOTLIN_CLI_WRAPPER_PATH = ../kotlin`, the expected bundle id, and `IPHONEOS_DEPLOYMENT_TARGET = 17.0`, and that `./kotlin build -m iosApp --platform=iosSimulatorArm64` succeeds.
13. **Verify Swift bridge API correctness**: When Swift uses exported Kotlin types, check for guessed ObjC-shaped names in Swift code such as underscore-suffixed enum cases, missing async wrapper usage, or missing types in `KotlinModules` that indicate a missing module dependency in `umbrella-core`/`iosApp`.

Repair loop:
1. `dream-general` delegates verification to `dream-tester`
2. `dream-general` identifies the failing module or runtime layer from tester output
3. `dream-discovery` gathers additional focused context if needed (uses codebase-memory-mcp for efficient code intelligence queries)
4. `dream-general` persists iteration state to `specs/verification-report.md` (current iteration, failures, past attempts, next module to fix)
5. `dream-general` launches a fresh implementer subagent invocation based on the failing layer:
   - `dream-implementer` for domain, data, or api failures
   - `dream-implementer-android` for Compose UI or `impl` failures
   - `dream-implementer-ios` for SwiftUI or bridge failures
6. The implementer loads relevant rules from the guidance and graph
7. The implementer makes the smallest correct fix
8. `dream-general` delegates reverification to `dream-tester`
9. `dream-general` checks skip conditions: iteration ≤ 5? failure count decreased? no regressions? environment blocker repeated?
10. if a skip condition triggers, stop and report `blocked` with the skip reason (documented in `specs/verification-report.md`); otherwise repeat from step 1
11. `dream-inspector` performs final review for substantial generation or repair work before validated success is reported

If `dream-inspector` fails at the tool level:
1. record the delegated review failure and the error message
2. run a manual review fallback against the contract, tests, and architecture rules
3. report status as `partial` or `blocked` if review defects remain
4. do not claim `validated` from verification results alone

Output requirements:
- produce a concise final verification summary
- state whether status is `validated`, `partial`, or `blocked`
- list commands run
- state review mode as `delegated` or `manual`
- list failing areas if blocked
- write or update `specs/verification-report.md`
- in `specs/verification-report.md`, list every check under one of these statuses: `passed`, `blocked by environment`, `not run`, or `not applicable`
- follow `.opencode/guidance/templates/verification-report-template.md` unless the orchestrator requests a stricter variant
- for completed feature flows, state the git commit hash or the precise reason no commit was created

If blocked:
- document the blocker precisely
- distinguish environment blockers from code blockers
- do not claim success
```
