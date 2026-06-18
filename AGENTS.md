# Dream Agent Instructions

Dream is a local-first AI MVP builder for Kotlin Multiplatform apps. This repository is mainly a guidance, template, and orchestration workspace, not a single runnable app at the repository root.

## Start Here

When working on Dream itself, read in this order:

1. [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md)
2. [guidance/opencode-agent-map.md](guidance/opencode-agent-map.md)
3. [guidance/dream-agent-operating-guide.md](guidance/dream-agent-operating-guide.md)
4. [knowledge-graph/README.md](knowledge-graph/README.md)
5. Relevant topic files in [knowledge-graph/best-practices/](knowledge-graph/best-practices/README.md)
6. [templates/dream-kmp-base/README.md](templates/dream-kmp-base/README.md) when touching the template

Prefer linking to those documents in discussions and change summaries instead of restating them.

## Repository Map

- [guidance/README.md](guidance/README.md): Dream operating guidance and prompt scaffolds
- [knowledge-graph/README.md](knowledge-graph/README.md): reusable architecture rules and source graph
- [templates/dream-kmp-base/README.md](templates/dream-kmp-base/README.md): modular KMP base template and validation entry points
- [.opencode/opencode.json](.opencode/opencode.json): OpenCode instruction injection
- [docs/cli-plan.md](docs/cli-plan.md): planned CLI surface
- [docs/first-try-report.md](docs/first-try-report.md): known generation and review gaps

## Working Rules

- Preserve Dream's architecture rules: thin app shells, centralized navigation, split shared core, and `Presentation -> Domain <- Data`.
- Keep domain logic pure Kotlin and keep product behavior in feature modules, not host app modules.
- Do not simplify the template by collapsing modules just to make a task faster.
- Treat desktop as the main fast preview and repair surface for generated apps.
- Do not claim success from green commands alone; match the contract, tests, and review expectations in [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md).

## Validation

The repository root is not the main Gradle execution surface. For template validation, run commands from [templates/dream-kmp-base](templates/dream-kmp-base):

```bash
cd templates/dream-kmp-base
./gradlew :feature:sample:presentation:allTests :shared:core:domain:allTests :shared:core:data:allTests :shared:core:presentation:allTests :shared:core:storage:allTests :shared:core:testing:allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:compileKotlinJvm
```

Dream's stricter final verification target is documented in [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md):

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:run
```

Use the lighter template commands for focused iteration, but do not report final success without the required verification behavior or a documented blocker.

## When Updating Dream

- If you change generation workflow or success criteria, update [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md) and the relevant files under [guidance/prompts/](guidance/prompts/).
- If you change reusable architecture rules, update the corresponding files in [knowledge-graph/best-practices/](knowledge-graph/best-practices/README.md) instead of adding duplicate guidance elsewhere.
- If you change template structure or validation commands, update [templates/dream-kmp-base/README.md](templates/dream-kmp-base/README.md).

## Existing Agent Layer

OpenCode-specific agents and commands already live under [.opencode/](.opencode/). Reuse that guidance when relevant, but keep repository-wide instructions here minimal and tool-agnostic.