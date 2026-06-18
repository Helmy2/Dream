# Project Instantiation Prompt

Use this prompt scaffold when instantiating a new Dream project from an existing Dream template.

```text
Intended OpenCode agent: implementer

You are instantiating a new Dream project from a Dream template.

Your task:
Turn the selected template into a new app-specific project baseline before feature generation begins.

Read first:
- `knowledge-graph/PRODUCT.md`
- `AI_MVP_BUILDER_PLAN.md`
- `guidance/dream-agent-operating-guide.md`
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
2. Rename app-visible template identifiers to app-specific identifiers.
3. Rename package identifiers from template-specific values to project-specific values.
4. Update Gradle, application id, namespace, and framework naming where needed.
5. Replace sample-facing display text that should not survive into the generated app baseline.
6. Preserve reusable shared infrastructure, build logic, and architecture modules.
7. Reset `.mvp/verification-report.md` to reflect the new generation cycle rather than the source template's prior validation state.
8. Update `.mvp/decisions.yaml` and `.mvp/app-contract.yaml` to reflect the generated app.
9. Decide whether the sample feature should remain temporarily as scaffolding or be replaced immediately by the first real feature.

Template-specific decisions you must make explicitly:
- whether `feature/sample/*` is kept only until the first real feature is generated
- whether package prefixes should become app-specific immediately or through a later normalization pass
- whether verification should run immediately after instantiation or only after the first feature lands

What to preserve from the template:
- `androidApp/`
- `iosApp/`
- `desktopApp/`
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

Rules for the sample feature:
- keep it only if it is still needed as temporary structural scaffolding
- remove or replace it as soon as the first real feature is generated
- do not let sample-domain concepts leak into the final generated product

Required outputs:
- instantiated project on disk
- updated `.mvp/app-contract.yaml`
- updated `.mvp/decisions.yaml`
- reset or regenerated `.mvp/verification-report.md`
- concise summary of all renamed identifiers and structural decisions

Completion criteria:
- the destination project is structurally valid
- no obvious template-only identity remains in user-facing app surfaces unless intentionally preserved
- Dream architecture boundaries remain intact
- the project is ready for first-feature generation

Do not:
- rewrite shared architecture unnecessarily
- merge feature modules together for convenience
- move domain logic into host apps
- claim the project is fully ready for release just because instantiation completed
```
