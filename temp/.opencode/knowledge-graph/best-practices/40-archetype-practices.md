# 40. Archetype Practices

An archetype is a complete, working reference feature for a common architecture, kept under `.opencode/archetypes/<name>/`. The starter itself has no feature modules; archetypes carry the feature-level knowledge (module shape, wiring, tests, strings, platform UI) as code that was built and verified.

### 40.1 Choose an archetype before planning a feature

- Read `.opencode/archetypes/README.md` and each candidate's `archetype.yaml`: `when_to_use`, `when_not_to_use`, `tradeoffs`.
- Pick the closest fit and record it in the plan's **Archetype** section: the name, why it fits, what will deviate, and which demo content will be dropped.
- If no archetype fits, record `none` with the reason and build from the architecture rules. A new archetype can be proposed after the feature is verified.

### 40.2 Adapt the archetype, never copy it unchanged

- Read `archetype.yaml`, `wiring.md`, and the reference code for the layers being built, then write the feature for the new domain.
- Rename consistently using the archetype's `naming` map across Kotlin, Swift, YAML, strings, and Maestro flows.
- Drop demo content before review: seed data, demo enums, demo strings, and demo copy in flows.
- Archetypes are reference code, not templates. They have no placeholder tokens and there is no scaffold script.
- `apply-verbatim.sh` applies an archetype unchanged to a scratch copy for the archetype check only. It refuses directories inside a git repository.

### 40.3 The placeholder home and the first feature

- The starter ships a placeholder home screen: `HomeRoute` and `placeholderHomeModule` in `shared/umbrella-ui` on Android, and an empty state in `ContentView.swift` on iOS.
- The first real feature replaces the placeholder: point `navigatorModule` at the feature's route, add the feature's Koin modules to `initKoin`, delete `PlaceholderHome.kt`, and remove the `placeholder_*` strings on both platforms.
- The "First Feature" section of `.opencode/guidance/new-feature-checklist.md` lists every step.

### 40.4 An archetype is valid only if it was validated

- `archetype.yaml` records `validated_with`: Toolchain, Kotlin, Compose, and Room versions, the commit, and the date.
- Archetype code lives outside the build and can rot silently. Run the archetype check before a release and whenever `libs.versions.toml` or the Toolchain version changes.
- The check copies the starter to a scratch directory, applies the archetype with `apply-verbatim.sh`, and runs `verify-feature.sh sample` in full.
- The scratch directory must be outside any git repository and must not be under `/tmp`, `/var`, or `/etc`: Xcode reports the symlinked path and the Toolchain's Xcode integration cannot resolve it, so Swift tests cannot run there.

### 40.5 Finish with the verification gate

- Run `.opencode/scripts/verify-feature.sh <feature-name>` before reporting a feature complete. It checks module structure, layer rules, Koin `moduleId` uniqueness, wiring, tests per layer, strings, iOS files, and Maestro flows, then runs the build and test ladder.
- A `FAILED` line blocks success. A full run with checks left `not run` is `INCOMPLETE` and exits 1; carry those lines into the verification report as `not run`, never as passed.
- `--quick` runs the structural checks only and is not a verification result.
- `.opencode/scripts/test-verify-feature.sh` breaks a scratch copy in many ways and checks the gate catches each one; run it after editing the gate.

### 40.6 Anti-pattern: shipping archetype content

- Copying an archetype unchanged, leaving the `sample` naming, seed rows, demo categories, or demo strings in a product feature, and keeping `HomeRoute` or `placeholder_*` after the first real feature all count as defects.
- Replacement: adapt the archetype per 40.2 and remove demo content and placeholders per 40.3 before review.
