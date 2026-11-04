# New Feature Checklist

Use this checklist when adding or replacing a Dream feature module.

Sections are tagged by condition:
- **Always** — apply to every feature
- **iOS** — apply when the feature has an iOS (SwiftUI) screen
- **Forms** — apply only when feature has editable text inputs
- **Time** — apply only when feature uses dates, timestamps, or time-sensitive logic
- **First Feature** — apply only when removing the sample feature
- **UI Fix** — apply when using `/dream-ui-fix`
- **Bug Fix** — apply when using `/dream-bug-fix`

For detailed rules, see `.opencode/knowledge-graph/rules-condensed.md`.

---

## Phase 0: Plan & User Review (Always)

- [ ] Create `specs/<feature-name>/plan.md` using the plan template before any implementation.
- [ ] Plan must define: feature goal, priority, modules to create/modify, screens with states, architecture decisions, dependencies, and phases.
- [ ] Present the plan to the user and ask: **questions? edits? confirm? deny?**
- [ ] On **Confirm**: create feature branch (`dream/feature/<name>`) and proceed.
- [ ] On **Edit**: update plan and re-present; loop until confirmed or denied.
- [ ] On **Deny**: stop and document in `specs/<feature>/report.md`.

## Contract First (Always)

- [ ] Create or update `specs/<feature-name>/contract.yaml` before implementation starts.
- [ ] Define entity fields, action inputs, screen states, design expectations, and tests by layer.
- [ ] Use meaningful timestamp field names with units (e.g., `createdAtMillis`).
- [ ] Mark `timeSensitiveBehavior.usesTimeProvider: true` if feature uses time-sensitive logic (see Time section).

## Feature Modules (Always)

- [ ] Add `feature/<feature-name>-domain/module.yaml` (`kmp/lib`, pure Kotlin).
- [ ] Add `feature/<feature-name>-data/module.yaml` when persistence or network is needed.
- [ ] Add `feature/<feature-name>-api/module.yaml` with `NavKey` route types only; no Koin definitions.
- [ ] Add `feature/<feature-name>-impl/module.yaml` (`platforms: [android]`, Compose) with screens, ViewModels, UI state, and Koin module.
- [ ] Module names equal directory names and must be unique across the project (e.g. `<feature-name>-domain`, never a bare `domain`).
- [ ] Set a unique Koin compiler plugin `moduleId` (the module name) in each module that applies the Koin plugin.
- [ ] For multi-word features, keep folder names, module names, and packages aligned.
- [ ] Add each new module to `project.yaml` (create dir + `module.yaml` + `project.yaml` entry), and add `//feature/<feature-name>-...` dependencies in the consuming `module.yaml` files.
- [ ] Keep repository interfaces in domain, implementations in data.
- [ ] Inject PresentationMapper into ViewModels via Koin `factory` — not in composables.
- [ ] Expose UI models (not domain types) in ViewModel state.
- [ ] Add unit tests for PresentationMapper.
- [ ] No module outside this feature should depend on `feature/<name>-impl`; callers use `api` only.

## Wiring (Always)

- [ ] Add `NavKey` route types in `feature/<feature-name>-api`.
- [ ] Install Navigation 3 entry providers in `feature/<feature-name>-impl` Koin module.
- [ ] Keep shared navigation centralized in `shared/navigation`.
- [ ] Import feature `impl` Koin modules in `shared/umbrella-ui`.
- [ ] Register data/domain dependencies in `shared/umbrella-core`.
- [ ] Use Koin compiler plugin DSL; no `@KoinViewModel`, `@Module`, or `@ComponentScan`.
- [ ] Configure the Koin compiler plugin (`settings.kotlin.compilerPlugins` with unique `moduleId`) in `module.yaml` of modules using compiler DSL.
- [ ] Add all user-visible strings to `shared/core-strings` before writing UI text.
- [ ] Cross-feature navigation goes through target feature's `api` only.

## Design System (Always)

- [ ] Use `shared.designsystem` components before raw Material3.
- [ ] Use `DreamTheme` tokens for spacing, shape, size, and color.
- [ ] Document any raw Material3 exception with the reason.

## Tests (Always)

- [ ] Add domain tests for model construction, value rules, and repository contracts.
- [ ] Add data tests for entity/domain mapping and repository behavior.
- [ ] Add presentation tests for loading, empty, content, error, action handling, and event emission.
- [ ] Ensure required `test-dependencies` are declared in each module's `module.yaml` before writing tests.

## Git Branching (Always)

- [ ] Create feature branch after Phase 0 confirmation: `dream/feature/<name>`.
- [ ] All commits go to the feature branch — never directly to main.
- [ ] After verification + review are green, present merge summary to user.
- [ ] User explicitly confirms: **merge? edit message? deny?**
- [ ] On confirm: squash merge to main, delete feature branch.
- [ ] On deny: keep branch, document reason in report.

## Phase Checkpoints (Always)

- [ ] After each phase completes, append to `specs/<feature>/checkpoints.md`.
- [ ] Checkpoint includes: phase name, git commit hash, status, timestamp.
- [ ] If a later phase fails, offer to roll back to last good checkpoint.

## Verification And Commit (Always)

- [ ] Run required `./kotlin test`/`./kotlin build` commands, platform builds, and UI inspection or document precise blockers.
- [ ] Record verification in explicit buckets: passed, blocked by environment, not run, not applicable.
- [ ] Run `dream-inspector` or manual review fallback against contract parity and architecture rules.
- [ ] Run `git status`, `git diff`, and `git log --oneline -10`.
- [ ] Stage only intended files.
- [ ] Present commit summary to user and get explicit approval before committing.
- [ ] Write `specs/<feature-name>/retrospective.md`.

## Preflight (Always)

- [ ] Run `./scripts/dream-preflight.sh` and record any blockers.
- [ ] Verify git and Android SDK readiness before verification.


---

## iOS SwiftUI Host (iOS)

Apply when the feature has an iOS screen:

- [ ] Create SwiftUI views in `iosApp/src/<FeatureName>/Views/`.
- [ ] Create `@Observable` ViewModels in `iosApp/src/<FeatureName>/ViewModels/`.
- [ ] Create Swift models in `iosApp/src/<FeatureName>/Models/` when needed.
- [ ] Extend bridge layer (`Core/SharedCoreBridge`) with new capabilities.
- [ ] Add string keys to `iosApp/src/Localizable.xcstrings` matching `strings.xml`.
- [ ] New `.swift` files in `iosApp/src` are picked up automatically (no project sync script).
- [ ] Run `./kotlin build -m iosApp --platform=iosSimulatorArm64` and install/launch with `xcrun simctl`. 
- [ ] Run Swift unit tests: `xcodebuild test -project iosApp/module.xcodeproj -scheme app -destination 'platform=iOS Simulator,name=iPhone 17 Pro' CODE_SIGNING_ALLOWED=NO` (tests in `iosApp/test`, target `appTests`).

## Responsive Forms And IME (Forms)

Apply only when feature has editable text inputs:

- [ ] Keep primary action reachable while keyboard is open.
- [ ] Use compact single-column form layout on narrow screens.
- [ ] Use adaptive wrapping for chips, tags, and filters.
- [ ] Verify success by outcome state, not keyboard-hidden assertions.

## Storage And Time (Time)

Apply only when feature uses dates, timestamps, or time-sensitive logic:

- [ ] Keep shared Room builder policy in shared core modules.
- [ ] Add new entities and DAOs to shared database unless intentionally isolated.
- [ ] Inject `TimeProvider` — use `nowMillis()`, `today()`, `currentYearMonth()`, `formatDate(...)`, `formatDateTime(...)`.
- [ ] Use `FakeTimeProvider` in time-sensitive tests.
- [ ] Do not add feature-local date formatting helpers in common code.

## Sample Removal (First Feature)

Apply only when removing the sample feature for the first time:

- [ ] Run `./scripts/remove-sample.sh --dry-run` first.
- [ ] Run `./scripts/remove-sample.sh` to delete sample files and strip deps.
- [ ] Complete manual edits from script output.
- [ ] Update `AGENTS.md`, `README.md`, and `maestro/README.md`.
- [ ] Run `./scripts/remove-sample.sh --dry-run` again to confirm no leftovers.

---

## UI Fix Workflow

Apply when using `/dream-ui-fix`:

- [ ] Phase 0: Create fix plan describing visual issue, platforms, and screens.
- [ ] Phase 1: Capture screenshot + element tree before changes.
- [ ] Phase 2: Identify affected UI source files and compare against UI spec.
- [ ] Phase 3: Apply fix only to presentation layer — never domain, data, or api.
- [ ] Phase 4: Re-capture screenshot + element tree for before/after comparison.
- [ ] Phase 4: Present before/after evidence to user and get signoff.
- [ ] Write `specs/ui-fix/report.md`.
- [ ] Commit only when user confirms fix is correct.

## Bug Fix Workflow

Apply when using `/dream-bug-fix`:

- [ ] Phase 0: Create fix plan with suspected root-cause layer.
- [ ] Phase 1: Gather diagnostics — crash logs, logcat, stack traces, test output.
- [ ] Phase 2: Map error output to layer boundaries.
- [ ] Phase 3: Apply minimal fix — fix the bug, do not refactor.
- [ ] Phase 4: Run targeted re-verification per fixed layer.
- [ ] Phase 4: Check skip conditions after each round.
- [ ] Write `specs/bug-fix/report.md`.
- [ ] Commit only when verification is green and user confirms.
