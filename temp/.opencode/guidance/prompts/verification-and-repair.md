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
- `.features/app-contract.yaml`
- `.features/decisions.yaml`
- `.features/<feature-name>/contract.yaml` if feature-specific
- `.opencode/guidance/templates/verification-report-template.md`

Required verification for local-only MVPs:
- `./.opencode/scripts/dream-preflight.sh` when present
- early git readiness: project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work
- early Android SDK readiness: `ANDROID_HOME` or the configured SDK path is available before Android validation is treated as code-ready
- early Maestro readiness: Maestro is installed or the missing tool is recorded as an environment blocker
- `./gradlew allTests`
- `./gradlew :androidApp:assembleDebug`
- Android CLI install, launch, layout/screenshot inspection, and log capture for Android (`https://developer.android.com/tools/agents/android-cli`)
- `xcodebuild` plus `xcrun simctl` build, install, launch, log, and crash capture for iOS
- Feature-owned Maestro smoke flows for every user-visible generated or touched Android feature after an Android emulator/device is open, the APK is installed, and Android launches cleanly
 - Feature-owned Maestro smoke flows for every user-visible generated or touched iOS feature where reliable after the target simulator is booted, the app is installed, and iOS launches cleanly

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
- `feature/<name>/api` contains only `NavKey` route types and minimal public contracts; no Koin definitions
- `feature/<name>/impl` is not imported by any other feature or by shared navigation; cross-feature navigation goes through `feature/<name>/api`
- cross-feature data implementation reuse is documented as an MVP exception or extracted to a shared module
- editable-form primary actions remain reachable while the software keyboard may be open
- responsive forms use compact single-column, wider grouped layout, and adaptive wrapping where applicable
- feature UI uses `shared/designsystem` components and `DreamTheme` tokens by default, with raw Material3 or ad hoc primitives documented as exceptions
- time-sensitive behavior uses the appropriate `TimeProvider` API instead of direct system time access or feature-local formatting
- meaningful date/month/time tests use readable `kotlinx-datetime` construction rather than unexplained raw epoch values
- repositories, use cases, and ViewModels are not instantiated inside recomposing composables
- generated feature DI uses Koin compiler plugin DSL registration, not annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan`
- feature-specific Navigation 3 route types are owned by `feature/<name>/api` and implement `NavKey`; Navigation 3 entry providers are installed in the Koin module inside `feature/<name>/impl`; shared navigation should not regain a shared `AppRoute` hierarchy without a concrete reason
- when `iosUiStrategy` is `swiftui`, the native host keeps bridge code in `Core`, native state holders in `ViewModels`, and XCTest coverage in `iosAppTests`
- when `iosUiStrategy` is `swiftui`, exported shared types needed by Swift are visible through framework `export(...)` plus transitive `api(...)` chains instead of hidden behind `implementation(...)`

Automated defect scans (run these BEFORE signing off):
1. **Grep for `Clock.System.now()`** in feature source files. The ONLY allowed occurrence is in `TimeProvider` implementations (e.g., `shared/core/domain/TimeProvider.kt` or an `expect/actual` platform time provider). Any other usage is a defect — flag it.
2. **Grep for hardcoded user-visible strings** in Compose UI files. Look for string literals passed to `Text()`, `TextField()`, `Button()`, `contentDescription`, or similar UI parameters that are not wrapped in `stringResource()`. Flag any hardcoded visible text.
3. **Grep for `String.format`** or `java.text.*` or `java.util.*` in `commonMain` source sets (not `androidMain`). These are JVM-only APIs that break iOS. Flag them.
4. **Grep for `menuAnchor()`** — this Material3 API is deprecated. Flag for migration to `MenuAnchorType`.
5. **Verify test coverage by layer**: Check that domain, data, and presentation modules each have at least one test file under `src/commonTest/`. A feature with only presentation tests but no domain or data tests is incomplete — flag it.
6. **Verify shared strings XML exists**: Check that `shared/core/strings/src/commonMain/composeResources/values/strings.xml` contains string resources for the feature. Cross-reference with the feature's UI to ensure every user-visible string has a resource entry.
7. **Verify DI setup**: Check that the feature's DI module registers repositories, ViewModels, and navigation entries through Koin DSL (not manual construction). Verify that repositories and ViewModels are not instantiated inside `@Composable` functions.
8. **Verify date formatting**: Check user-visible date/date-time text in common UI code uses `TimeProvider.formatDate(...)` or `TimeProvider.formatDateTime(...)`. Flag feature-local date formatting helpers.
9. **Verify Koin compiler DSL setup**: Grep generated feature modules for `@KoinViewModel`, `@Module`, `@ComponentScan`, and `koin.annotations`. Flag any usage unless the user explicitly requested annotation-based DI. Verify modules using compiler DSL registrations apply `libs.plugins.koinCompiler`.
10. **Verify Maestro flows and validate UI via mobile-mcp**: Run feature-owned Maestro smoke flows under `maestro/features/<feature-name>/android.yaml` and `maestro/features/<feature-name>/ios.yaml` for every user-visible generated or touched feature supported on those platforms. Do not accept one global smoke file as coverage for multiple features. Run Maestro only after the target emulator/simulator is open, the app is installed, and platform launch has been verified. Start smoke flows with `clearState` before `launchApp` unless the contract explicitly tests persisted state across launches. Prefer `eraseText`, `pressKey: Enter` on iOS single-line fields, full rendered-row regex selectors, and user-outcome assertions over keyboard-state assertions. Flag Android smoke flows that require app/window title text to be visible as the only launch proof. After Maestro flows complete, use mobile-mcp tools (`mobile_take_screenshot`, `mobile_list_elements_on_screen`) to capture the UI state and inspect the element tree. If mobile-mcp is not available, fall back to `mobilecli screenshot --device <id> -o <file>` and `mobilecli dump ui --device <id>`. If mobilecli is also unavailable, fall back to Android CLI (`adb exec-out screencap -p`, `adb shell uiautomator dump`). Verify contract-mandated UI elements, accessibility labels, and content descriptions are present. Cross-reference visible text with `shared/core/strings` resources. If none of MCP, mobilecli, or device CLI are available, skip UI inspection and record it as `not run` in the verification report.
11. **Verify git completion for feature flows**: After validated feature work, run `git status`, `git diff`, and `git log --oneline -10`; stage only intended files, prepare a concise feature commit, and ask the user for explicit approval before committing. Do not commit blocked, partial, unreviewed, or unsafe worktree states.
12. **Verify design-system usage**: Scan feature Compose code for raw Material3 imports such as `Button`, `OutlinedTextField`, `Card`, `AlertDialog`, `ModalBottomSheet`, `Snackbar`, `TopAppBar`, and progress indicators. If a Dream design-system wrapper exists, using the raw primitive is a defect unless a local exception is documented.
13. **Verify SwiftUI host wiring**: When the native iOS strategy is SwiftUI, verify the shared Xcode scheme exists, `xcodebuild build` and `xcodebuild test` run against it, the Xcode shell build phase invokes the correct `embedAndSignAppleFrameworkForXcode` task, and `FRAMEWORK_SEARCH_PATHS` points at `shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)` without duplicate entries.
14. **Verify Swift bridge API correctness**: When Swift uses exported Kotlin types, check for guessed ObjC-shaped names in Swift code such as underscore-suffixed enum cases, missing async wrapper usage, or missing exported framework types that indicate `export(...)` or `api(...)` gaps.

Repair loop:
1. `dream-general` delegates verification to `dream-tester`
2. `dream-general` identifies the failing module or runtime layer from tester output
3. `dream-explore` or `dream-context-scout` gathers additional focused context if needed
4. `dream-general` persists iteration state to `.features/verification-report.md` (current iteration, failures, past attempts, next module to fix)
5. `dream-general` launches a fresh implementer subagent invocation based on the failing layer:
   - `dream-implementer` for domain, data, or api failures
   - `dream-implementer-android` for Compose UI or `impl` failures
   - `dream-implementer-ios` for SwiftUI or bridge failures
6. The implementer loads relevant rules from the guidance and graph
7. The implementer makes the smallest correct fix
8. `dream-general` delegates reverification to `dream-tester`
9. `dream-general` checks skip conditions: iteration ≤ 5? failure count decreased? no regressions? environment blocker repeated?
10. if a skip condition triggers, stop and report `blocked` with the skip reason (documented in `.features/verification-report.md`); otherwise repeat from step 1
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
- write or update `.features/verification-report.md`
- in `.features/verification-report.md`, list every check under one of these statuses: `passed`, `blocked by environment`, `not run`, or `not applicable`
- follow `.opencode/guidance/templates/verification-report-template.md` unless the orchestrator requests a stricter variant
- for completed feature flows, state the git commit hash or the precise reason no commit was created

If blocked:
- document the blocker precisely
- distinguish environment blockers from code blockers
- do not claim success
```
