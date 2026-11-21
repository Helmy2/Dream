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

1. Copy `temp/` to a scratch directory (not inside this repo).
2. Delete the existing `feature/sample-*` modules and sample wiring, or start from an empty shell once one exists.
3. Apply the archetype from `.opencode/archetypes/<name>/` following `wiring.md`.
4. Run every command under `verification` in `archetype.yaml` and confirm the tests pass.
5. If it fails, fix the archetype folder, update `validated_with`, and commit.

## Adding an archetype

1. Build the feature in a scratch copy of `temp/` until it compiles and passes tests on both platforms.
2. Copy its modules, iOS files, strings and Maestro flows into a new folder here, mirroring `local-crud/`.
3. Write `archetype.yaml` and `wiring.md`, and add a row to the table above.
4. Run the archetype check.
