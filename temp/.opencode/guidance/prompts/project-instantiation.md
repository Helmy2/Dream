# Project Instantiation Prompt

Use this prompt scaffold when instantiating a new Dream project from an existing Dream template.

```text
Intended OpenCode agent: dream-implementer

You are instantiating a new Dream project from a Dream template.

Your task:
Turn the selected template into a new app-specific project baseline before feature generation begins.

Read first:
- `.opencode/knowledge-graph/PRODUCT.md`
- `AGENTS.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- selected template README
- selected template `specs/` metadata
- any already-generated `specs/app-contract.yaml` or `specs/decisions.yaml`

Inputs you must use:
- destination project name
- selected template name
- app contract
- decisions file if present

Expected output back to the orchestrator:
- instantiated project summary
- list of renamed identifiers
- canonical app identity summary
- list of preserved structural modules
- note of whether sample feature scaffolding was kept temporarily or replaced

Primary goal:
Create a runnable, renamed, app-specific baseline while preserving Dream's architecture and template structure.

Non-negotiable rules:
- preserve module boundaries
- preserve `Presentation -> Domain <- Data`
- keep host apps thin
- keep shared infrastructure in shared modules
- do not collapse the modular template into a simpler structure
- do not leave template identity where app identity should be present

Instantiation steps:
1. Copy the selected template into the destination project location.
2. Verify the iOS host. iOS is native SwiftUI only (`iosApp/`); there is no variant to choose.
    - Verify `iosApp/module.xcodeproj` still has `KOTLIN_CLI_WRAPPER_PATH = ../kotlin`, the new `PRODUCT_BUNDLE_IDENTIFIER`, and `IPHONEOS_DEPLOYMENT_TARGET = 17.0` (see `.opencode/guidance/reference/kotlin-toolchain.md`).
    - Run `./kotlin build -m iosApp --platform=iosSimulatorArm64` to confirm it compiles before proceeding to feature generation.
3. Derive a canonical app identity before renaming anything.
3. Rename app-visible template identifiers to app-specific identifiers.
5. Rename package identifiers from template-specific values to project-specific values.
6. Update `project.yaml`, `module.yaml` settings, application id, namespace, and framework naming where needed.
7. Audit all required identity surfaces and record each rename explicitly.
8. Replace sample-facing display text that should not survive into the generated app baseline.
9. Preserve reusable shared infrastructure, build logic, and architecture modules.
10. Reset `specs/verification-report.md` to reflect the new generation cycle rather than the source template's prior validation state.
11. Update `specs/decisions.yaml` and `specs/app-contract.yaml` to reflect the generated app.
12. Decide whether the sample feature should remain temporarily as scaffolding or be replaced immediately by the first real feature.
13. Verify git readiness in the destination project before later workflow stages depend on git commands.
14. Verify Android SDK readiness early, or record precise environment blockers.
15. Run `./scripts/dream-preflight.sh` when it exists and include the result in the instantiation summary.
16. Check and install development dependencies interactively:
    - `mobilecli` (`command -v mobilecli`; install via `npm install -g @mobilenext/mobilecli`)
    - `xcodeproj` Ruby gem (`gem list -i xcodeproj`; install via `gem install xcodeproj`)
    - Java JDK 17+ (`java -version`; check only — instruct manual install from https://adoptium.net if missing)
    - Ask the user before each install and respect their choice. If a tool is already installed, confirm and move on.
17. Choose and document the generated app's feature naming convention before creating multi-word feature modules.

Canonical app identity you must derive and keep consistent:
- app display name
- canonical package root
- Android application id
- Android namespace values by module
- iOS bundle or visible app identity where applicable
- Toolchain project naming and any generated naming derived from the app identity

Required identity audit surfaces:
- `project.yaml` module list where module names are affected
- every module `module.yaml` `settings.android.namespace` and the Android application id in `androidApp/module.yaml`
- Koin compiler plugin `moduleId` values (must stay unique per module)
- Kotlin package declarations and directory layout where package roots are renamed
- Android manifest labels and other visible app identity text
- iOS configuration values that carry app identity
- README or `specs/` metadata values that still carry template identity
- obvious sample or template display strings that would misidentify the generated app

Template-specific decisions you must make explicitly:
- whether `feature/sample/*` is kept only until the first real feature is generated
- whether package prefixes should become app-specific immediately or through a later normalization pass
- whether verification should run immediately after instantiation or only after the first feature lands

Identity normalization rules:
- normalize identity immediately unless a concrete blocker is documented
- do not leave `template` or other template-only identity in package roots, namespaces, app ids, or user-facing app names
- keep package roots deterministic from the canonical app identity rather than ad hoc renames
- keep multi-word feature paths and Kotlin packages aligned; prefer package-mirroring directories such as `feature/expense-details` with `feature.expense.details`
- if a surface intentionally keeps template identity temporarily, record the exact reason and the follow-up removal step

What to preserve from the template:
- `androidApp/`
- `iosApp/`
- `shared/core-*`
- `shared/designsystem`
- `shared/navigation`
- `shared/umbrella-core`
- `shared/umbrella-ui`
- `build-logic/`

What to customize:
- app name
- application id and namespace values
- package roots that still contain template identity
- display text such as window title, starter labels, and obvious sample text
- `specs/` metadata for the new project

Required identity audit output:
- canonical app identity summary
- list of every renamed identifier with old value, new value, and file or surface
- list of any intentionally deferred renames with reason
- explicit statement that no known template-only identity remains in user-facing or package-defining surfaces, or a precise exception list

Rules for the sample feature:
- keep it only if it is still needed as temporary structural scaffolding
- remove or replace it as soon as the first real feature is generated
- do not let sample-domain concepts leak into the final generated product
- when the first real feature is present, remove the `feature/sample` module directory from the generated app unless a documented scaffolding exception is still active
- when removing sample, also remove sample routes, DI registrations, settings includes, docs text, string resources, and Room schema exports
- run a leftover artifact scan for sample route names, module names, database names, string keys, schema paths, and README/AGENTS references before review
- if `feature/sample` is intentionally retained temporarily, record the exact reason, the owner, and the removal step in the instantiation summary

Required outputs:
- instantiated project on disk
- updated `specs/app-contract.yaml`
- updated `specs/decisions.yaml`
- reset or regenerated `specs/verification-report.md`
- concise summary of all renamed identifiers and structural decisions
- explicit identity audit summary

Completion criteria:
- the destination project is structurally valid
- no obvious template-only identity remains in user-facing app surfaces, package-defining surfaces, or build identity unless intentionally preserved and documented
- Dream architecture boundaries remain intact
- the project is ready for first-feature generation

Do not:
- rewrite shared architecture unnecessarily
- merge feature modules together for convenience
- move domain logic into host apps
- claim the project is fully ready for release just because instantiation completed
```
