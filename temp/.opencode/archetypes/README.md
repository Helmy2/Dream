# Archetypes

An archetype is a complete, working reference feature for a common architecture. Agents read it to see how Dream expects a feature of that kind to be built, then adapt it to the new feature. It is reference code, not a template: there are no placeholder tokens and no scaffold script.

## Available archetypes

| Archetype | Use when | Platforms |
| --- | --- | --- |
| [`local-crud`](local-crud/archetype.yaml) | A user-owned collection stored on the device with list, detail and create/edit screens | Android (Compose) and iOS (SwiftUI) |

Planned, not yet written: `remote-list` (Ktor, DTO, mapper, cache) and `settings` (DataStore).

## Choosing one

1. Read each candidate's `archetype.yaml`: `when_to_use`, `when_not_to_use`, `tradeoffs`.
2. Pick the closest fit and say why in `specs/<feature>/plan.md`. If nothing fits, build from the architecture rules in `.opencode/knowledge-graph/` and consider proposing a new archetype afterwards.
3. Record the choice in the feature contract so reviewers can compare the result against it.

## Applying one

1. Read the whole archetype folder: `archetype.yaml`, `wiring.md`, then the code under `kotlin/`, `ios/`, `strings/` and `maestro/`.
2. Rename consistently using the `naming` map in `archetype.yaml`.
3. Create the modules, adapt domain, data and presentation to the new feature, and write the contract and tests first or alongside.
4. Make every wiring change listed in `wiring.md`.
5. Remove demo-only content: seed data, the demo category enum, demo strings.
6. Run the commands under `verification` in `archetype.yaml`, then the checklist in `../guidance/new-feature-checklist.md`.

## What makes a valid archetype

- It compiled and its tests passed at the commit and versions recorded in `validated_with`.
- It follows the architecture rules: `Presentation -> Domain <- Data`, `-api` holds route keys only, strings in shared resources, design-system components first.
- `archetype.yaml` lists the modules, the wiring points outside them, the included tests, the pitfalls, and when not to use it.
- Anything a newcomer would get wrong (unique Koin `moduleId`, module naming, Swift naming quirks) is under `pitfalls`.

## Archetype check

Archetype code lives outside the build and can rot as versions change. Run this before a release and whenever `libs.versions.toml` or the Toolchain version changes:

```bash
# from the starter root (temp/)
SCRATCH="$HOME/.dream-scratch/archetype-check"   # outside any git repo, and NOT under /tmp, /var or /etc
rm -rf "$SCRATCH" && mkdir -p "$SCRATCH"
rsync -a --exclude=build/ --exclude=.kotlin/ --exclude=.git/ ./ "$SCRATCH/"
"$SCRATCH/.opencode/archetypes/<name>/apply-verbatim.sh" "$SCRATCH"
cd "$SCRATCH" && .opencode/scripts/verify-feature.sh sample    # full run: structure, build, tests, Swift tests
```

The scratch directory must not be under `/tmp`, `/var` or `/etc` (symlinks into `/private` on macOS): Xcode reports the symlinked path and the Toolchain's Xcode integration cannot resolve it, so the Swift tests cannot run there. `verify-feature.sh` reports that as `not run` and exits 1 instead of passing.

`apply-verbatim.sh` applies the archetype unchanged (feature name `sample`), so the result is the original working feature on top of the current starter. If the check fails, fix the archetype folder, update `validated_with` in `archetype.yaml`, and commit. `.opencode/scripts/test-verify-feature.sh` uses the same fixture for its quick structural run.

## Adding an archetype

1. Build the feature in a scratch copy of `temp/` until it compiles and passes tests on both platforms.
2. Copy its modules, iOS files, strings and Maestro flows into a new folder here, mirroring `local-crud/`.
3. Write `archetype.yaml` and `wiring.md`, add an `apply-verbatim.sh` that applies it unchanged to a scratch copy, and add a row to the table above.
4. Run the archetype check.
