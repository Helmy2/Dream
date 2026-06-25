# Dream Agent Instructions

Dream is a local-first AI MVP builder for Kotlin Multiplatform apps. This repository now centers on [temp](temp): the self-contained starter project that the initializer copies and renames.

## Start Here

When working on Dream itself, read in this order:

1. [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md)
2. [ONBOARDING.md](ONBOARDING.md)
3. [temp/AGENTS.md](temp/AGENTS.md)
4. [temp/.opencode/guidance/opencode-agent-map.md](temp/.opencode/guidance/opencode-agent-map.md)
5. [temp/.opencode/guidance/dream-agent-operating-guide.md](temp/.opencode/guidance/dream-agent-operating-guide.md)
6. [temp/.opencode/knowledge-graph/README.md](temp/.opencode/knowledge-graph/README.md)
7. Relevant topic files in [temp/.opencode/knowledge-graph/best-practices/](temp/.opencode/knowledge-graph/best-practices/README.md)
8. [temp/README.md](temp/README.md) when touching the starter project

Prefer linking to those documents in discussions and change summaries instead of restating them.

## Repository Map

- [temp](temp): canonical starter project copied by the initializer
- [ONBOARDING.md](ONBOARDING.md): human-readable guide to the project architecture, workflow, and validation model
- [temp/.opencode/opencode.json](temp/.opencode/opencode.json): packaged OpenCode instruction injection and MCP config
- [temp/.opencode/agents/](temp/.opencode/agents): packaged Dream agents
- [temp/.opencode/commands/](temp/.opencode/commands): packaged Dream slash-command workflows
- [temp/.opencode/guidance/](temp/.opencode/guidance): packaged operating guidance and prompt scaffolds
- [temp/.opencode/knowledge-graph/README.md](temp/.opencode/knowledge-graph/README.md): packaged reusable architecture rules and source graph
- [docs/first-try-report.md](docs/first-try-report.md): known generation and review gaps

## Working Rules

- Preserve Dream's architecture rules: thin app shells, centralized navigation, split shared core, and `Presentation -> Domain <- Data`.
- Keep domain logic pure Kotlin and keep product behavior in feature modules, not host app modules.
- Do not simplify the template by collapsing modules just to make a task faster.
- Treat Android, iOS, and contract-selected Desktop as generated app product targets.
- Use Android CLI for Android device/emulator setup, install, launch, inspection, logs, and repair loops.
- Use `xcodebuild` and `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture.
- Use Maestro for black-box smoke flows after platform launch is healthy.
- When Desktop is included in the app contract, validate it as a supported target and use `:desktopApp:hotRunJvm` for interactive runs.
- Do not claim success from green commands alone; match the contract, tests, and review expectations in [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md).

## Validation

The repository root is not the main Gradle execution surface. For starter validation, run commands from [temp](temp):

```bash
cd temp
./gradlew :feature:sample:presentation:allTests :shared:core:domain:allTests :shared:core:data:allTests :shared:core:presentation:allTests :shared:core:testing:allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:compileKotlinJvm
```

Dream's stricter final verification target is documented in [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md):

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
# Android CLI install/launch/log inspection
# xcodebuild plus xcrun simctl build/install/launch/log inspection
# Maestro smoke flows for relevant Android and iOS user journeys
# Desktop target validation when desktop is in the app contract:
./gradlew :desktopApp:compileKotlinJvm
./gradlew :desktopApp:hotRunJvm
```

Use the lighter template commands for focused iteration, but do not report final success without the required verification behavior or a documented blocker.

## When Updating Dream

- If you change generation workflow or success criteria, update [AI_MVP_BUILDER_PLAN.md](AI_MVP_BUILDER_PLAN.md) and the relevant files under [temp/.opencode/guidance/prompts/](temp/.opencode/guidance/prompts/).
- If you change reusable architecture rules, update the corresponding files in [temp/.opencode/knowledge-graph/best-practices/](temp/.opencode/knowledge-graph/best-practices/README.md).
- If you change starter structure or validation commands, update [temp/README.md](temp/README.md) and [temp/AGENTS.md](temp/AGENTS.md).

## Existing Agent Layer

OpenCode-specific agents and commands live under [temp/.opencode/](temp/.opencode/). Work from [temp](temp) or from an initializer-created copy when exercising the packaged workflow.
