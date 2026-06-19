# Project Instantiation Prompt

Use this prompt scaffold when instantiating a new Dream project from an existing Dream template.

```text
Intended OpenCode agent: implementer

You are instantiating a new Dream project from a Dream template.

Your task:
Turn the selected template into a new app-specific project baseline before feature generation begins.

Read first:
- `.knowledge-graph/PRODUCT.md`
- `AGENTS.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- selected template README
- selected template `.mvp/` metadata
- any already-generated `.mvp/app-contract.yaml` or `.mvp/decisions.yaml`

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
2. Derive a canonical app identity before renaming anything.
3. Rename app-visible template identifiers to app-specific identifiers.
4. Rename package identifiers from template-specific values to project-specific values.
5. Update Gradle, application id, namespace, and framework naming where needed.
6. Audit all required identity surfaces and record each rename explicitly.
7. Replace sample-facing display text that should not survive into the generated app baseline.
8. Preserve reusable shared infrastructure, build logic, and architecture modules.
9. Reset `.mvp/verification-report.md` to reflect the new generation cycle rather than the source template's prior validation state.
10. Update `.mvp/decisions.yaml` and `.mvp/app-contract.yaml` to reflect the generated app.
11. Decide whether the sample feature should remain temporarily as scaffolding or be replaced immediately by the first real feature.

Canonical app identity you must derive and keep consistent:
- app display name
- canonical package root
- Android application id
- Android namespace values by module
- desktop app window title, when Desktop is included in the app contract
- iOS bundle or visible app identity where applicable
- Gradle root project name and any generated naming derived from the app identity

Required identity audit surfaces:
- `settings.gradle.kts` root project name and included module naming where affected
- every module `build.gradle.kts` namespace and Android application id values
- Kotlin package declarations and directory layout where package roots are renamed
- Android manifest labels and other visible app identity text
- desktop app window title or launcher-visible identity text, when Desktop is included in the app contract
- iOS configuration values that carry app identity
- README or `.mvp/` metadata values that still carry template identity
- obvious sample or template display strings that would misidentify the generated app

Template-specific decisions you must make explicitly:
- whether `feature/sample/*` is kept only until the first real feature is generated
- whether package prefixes should become app-specific immediately or through a later normalization pass
- whether verification should run immediately after instantiation or only after the first feature lands

Identity normalization rules:
- normalize identity immediately unless a concrete blocker is documented
- do not leave `template` or other template-only identity in package roots, namespaces, app ids, or user-facing app names
- keep package roots deterministic from the canonical app identity rather than ad hoc renames
- if a surface intentionally keeps template identity temporarily, record the exact reason and the follow-up removal step

What to preserve from the template:
- `androidApp/`
- `iosApp/`
- `desktopApp/` when Desktop is included as a generated app target
- `shared/core/*`
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
- `.mvp/` metadata for the new project

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
- if `feature/sample` is intentionally retained temporarily, record the exact reason, the owner, and the removal step in the instantiation summary

Required outputs:
- instantiated project on disk
- updated `.mvp/app-contract.yaml`
- updated `.mvp/decisions.yaml`
- reset or regenerated `.mvp/verification-report.md`
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
