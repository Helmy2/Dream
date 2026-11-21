# Plan: Feature-less starter with archetypes

Status: draft, awaiting approval. No code has been changed.

## Goal

Make `temp/` a feature-less shell (infrastructure only) that is easy to work on, and keep the architectural knowledge as reusable **archetypes**: real, working reference features that an agent adapts when building a new feature.

## Decisions

- **Placeholder home screen: yes.** The shell keeps a tiny placeholder (about 10 lines) so `Navigator` has a start destination and the shell always builds. The first real feature replaces it.
- **No scaffold script, no `__Feature__` tokens.** Archetypes are real compiling-style source. The agent reads and adapts them.
- **Checklist must be command-backed.** Every item has a command that passes or fails. A read-only `verify-feature.sh <name>` is the final gate.
- **Archetype location (proposed): `temp/.opencode/archetypes/`.** It travels with generated projects. Confirm or change.
- **First archetype: `local-crud` only.** Add others after the pipeline is proven.

## Known problems this plan relies on fixing first

- The working tree deletes `app.xcscheme` and removes `appTests` from `iosApp.xcscheme`, but `AGENTS.md` and `ONBOARDING.md` still tell agents to run `xcodebuild test -scheme app`.
- `temp/.gitignore` ignores `.opencode/` and `AGENTS.md`, yet 107 `.opencode` files are tracked.
- Sample code is baked into shared modules: `core-data` (Room DB, DAO, entity, factories), `umbrella-core` (`CoreModule`, extensions, iOS factory), `umbrella-ui` (`UiModule`, `module.yaml`), `androidApp` (`MainActivity`), and iOS (`ContentView`, `SharedCoreBridge`).

## Phases

### Phase 0: Baseline
- Decide the Xcode scheme state (commit or revert) and make the docs match.
- Fix the `.gitignore` conflict. Move the ignore of `.opencode/` and `AGENTS.md` into `dream-init` if it is meant for generated apps.
- Run `./kotlin build`, `./kotlin test --platform=android`, `./kotlin test --platform=iosSimulatorArm64`, and the Android and iOS app builds. Record results.
- Exit: a known-good, recorded baseline.

### Phase 1: Decouple sample from core (separate commit)
- Move the Room DB, DAO, entity, and factories from `core-data` into `sample-data`.
- Split `CoreModule` into a generic core module (time, DataStore, HTTP, session) and a sample module.
- Make the iOS bridge per-feature instead of sample-specific.
- Exit: same behavior as the baseline, all tests and builds green, app launches on both platforms.

### Phase 2: Create the `local-crud` archetype
- Move the sample (Kotlin modules, SwiftUI, bridge, Swift tests, Maestro flows) to `.opencode/archetypes/local-crud/`.
- Add `archetype.yaml`: when to use it, tradeoffs, modules created, dependencies, strings, and every wiring point it touches.
- Add `archetypes/README.md` explaining how to choose an archetype.

### Phase 3: Checklist and verification
- Write the "add a feature" guide and extend `new-feature-checklist.md` with a command per item.
- Write `verify-feature.sh <name>`. Checks include: modules listed in `project.yaml` exist with a `module.yaml`; `-impl` is wired into `umbrella-ui`; a route key exists in `-api`; strings are in `core-strings`; tests exist per layer; Maestro flows exist per platform; build and tests pass.

### Phase 4: Strip `temp` to the shell
- Remove `feature/sample-*`, clean `project.yaml`, remove the sample from iOS, and delete `remove-sample.sh`.
- Add the placeholder home (Android and iOS).
- Exit: the empty shell builds, tests run (modules without tests must not break `./kotlin test`), and the app launches.

### Phase 5: Knowledge and agents
- Reword the 14 best-practice files that mention the sample so they point at archetypes.
- Find out how `graph.json` and `source-chunks.json` are generated, then regenerate them.
- Update `dream-general` so its planning phase selects an archetype. Update `dream-implementer` and `dream-implementer-ui` to start from it.
- Update `dream-init`, `ONBOARDING.md`, `AGENTS.md`, and `specs/verification-report.md`.

### Phase 6: Archetype check, then more archetypes
- Document an "archetype check": copy `temp` to a scratch directory, have an agent apply the archetype, run `verify-feature.sh` and the build. Run it before releases. Add CI later.
- Add `remote-list` (Ktor, DTO, mapper, cache), then `settings` (DataStore), one at a time, each passing the check.

## Risks

- The Kotlin Toolchain is Alpha. A shell with no feature modules may not be accepted (for example an `umbrella-core` with no feature dependencies). Test early in Phase 4.
- The Toolchain regenerates the Xcode project, so removing iOS sample files needs a careful pass with `fix-ios-project.sh`.
- Koin compiler plugin `moduleId` options are per module. Adapted features must set them correctly. `verify-feature.sh` should check this.
- Archetype code is outside the build and can rot. Mitigated by the archetype check in Phase 6.

## Open items

- Confirm the archetype location.
- Confirm Phase 1 as its own commit (recommended).
