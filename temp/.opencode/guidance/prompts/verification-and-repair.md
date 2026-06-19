# Verification And Repair Prompt

Use this prompt scaffold when verifying or repairing a Dream-generated project.

```text
Primary OpenCode agents:
- general for orchestration and reruns
- implementer for fixes
- inspector for final QA gate on substantial work

You are verifying and repairing a Dream-generated Kotlin Multiplatform project.

Your task:
Run the required verification flow, diagnose failures, repair them while preserving architecture rules, and produce a final verification result.

Read first:
- `.knowledge-graph/PRODUCT.md`
- `.opencode/guidance/opencode-agent-map.md`
- `AGENTS.md`
- relevant graph-backed rules for the failing module or runtime surface
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- `.mvp/features/<feature-name>/contract.yaml` if feature-specific

Required verification for local-only MVPs:
- `./gradlew allTests`
- `./gradlew :androidApp:assembleDebug`
- Android CLI install, launch, layout/screenshot inspection, and log capture for Android (`https://developer.android.com/tools/agents/android-cli`)
- `xcodebuild` plus `xcrun simctl` build, install, launch, log, and crash capture for iOS
- Maestro smoke flows for relevant Android user journeys after Android launches cleanly
- Maestro smoke flows for relevant iOS user journeys where reliable after iOS launches cleanly
- `./gradlew :desktopApp:compileKotlinJvm` plus `./gradlew :desktopApp:hotRunJvm` when `desktop` is included in the app contract

Required behavior:
- do not report validated success until verification is green, contract behavior is implemented, required tests exist, and review status is documented
- treat Desktop as a supported product target when the app contract includes it, but do not use Desktop success as proof of Android/iOS runtime readiness
- preserve `Presentation -> Domain <- Data`
- do not fix failures by collapsing module boundaries
- do not move business logic into host apps as a shortcut
- do not ignore hardcoded shared UI strings, direct system time access, or swallowed `CancellationException`
- do not treat green build commands alone as proof that the generated feature matches its contract

Required parity checks before validated success:
- every contract state is implemented in code and represented in the UI state model
- every contract action maps to a concrete handling path
- every required contract test appears in the test plan and is backed by code-level coverage
- time-sensitive behavior uses `TimeProvider` instead of direct system time access
- repositories, use cases, and ViewModels are not instantiated inside recomposing composables

Repair loop:
1. `general` runs verification
2. `general` identifies the failing module or runtime layer
3. `dream-explore` or `dream-context-scout` gathers additional focused context if needed
4. `implementer` loads relevant rules from the guidance and graph
5. `implementer` makes the smallest correct fix
6. `general` reruns verification
7. repeat until green or blocked
8. `inspector` performs final review for substantial generation or repair work before validated success is reported

If `inspector` fails at the tool level:
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
- write or update `.mvp/verification-report.md`

If blocked:
- document the blocker precisely
- distinguish environment blockers from code blockers
- do not claim success
```
