# Dream Generated Project Instructions

This folder is a self-contained Dream Kotlin Multiplatform starter project. It is meant to be copied by the initializer, renamed for a new app, then opened directly in OpenCode.

## Start Here

When working in this generated project, read in this order:

1. [.opencode/guidance/opencode-agent-map.md](.opencode/guidance/opencode-agent-map.md)
2. [.opencode/guidance/dream-agent-operating-guide.md](.opencode/guidance/dream-agent-operating-guide.md)
3. [.opencode/knowledge-graph/README.md](.opencode/knowledge-graph/README.md)
4. Relevant topic files in [.opencode/knowledge-graph/best-practices/](.opencode/knowledge-graph/best-practices/README.md)
5. [README.md](README.md)
6. Existing `.mvp/` metadata when it exists

## Packaged Agent Layer

- [.opencode/opencode.json](.opencode/opencode.json): OpenCode instruction injection and MCP configuration
- [.opencode/agents/](.opencode/agents): Dream agent definitions
- [.opencode/commands/](.opencode/commands): Dream slash-command workflows
- [.opencode/guidance/](.opencode/guidance): operating guide and prompt scaffolds
- [.opencode/knowledge-graph/](.opencode/knowledge-graph): architecture rules and retrieval sources
- [maestro/](maestro): Android and iOS smoke-flow scaffolding for generated feature validation
- [scripts/dream-preflight.sh](scripts/dream-preflight.sh): git, Android SDK, and Maestro readiness preflight
- [.opencode/guidance/new-feature-checklist.md](.opencode/guidance/new-feature-checklist.md): contract-first feature wiring, verification, and git checklist

## Working Rules

- Preserve Dream's architecture rules: thin app shells, centralized navigation, split shared core, and `Presentation -> Domain <- Data`.
- Keep domain logic pure Kotlin and keep product behavior in feature modules, not host app modules.
- Do not simplify the template by collapsing modules just to make a task faster.
- Treat Android, iOS, and contract-selected Desktop as generated app product targets.
- Use Android CLI for Android device/emulator setup, install, launch, inspection, logs, and repair loops.
- Use `xcodebuild` and `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture.
- Use Maestro for black-box smoke flows after platform launch is healthy.
- When Desktop is included in the app contract, validate it as a supported target and use `:desktopApp:hotRunJvm` for interactive runs.
- Do not claim success from green commands alone; match the contract, tests, and review expectations in [.opencode/guidance/dream-agent-operating-guide.md](.opencode/guidance/dream-agent-operating-guide.md).

## Validation

Run commands from this project root:

```bash
./scripts/dream-preflight.sh
./gradlew :feature:sample:presentation:allTests :shared:navigation:allTests :shared:core:domain:allTests :shared:core:data:allTests :shared:core:presentation:allTests :shared:core:testing:allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:compileKotlinJvm
```

Dream's stricter final verification includes:

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

Use focused commands for iteration, but do not report final success without the required verification behavior or a documented blocker.

## Initializer Boundary

The initializer should only copy this `temp` project, rename it, and perform deterministic identity rewrites. Feature generation, repair, verification, and review belong to the packaged OpenCode workflow.
