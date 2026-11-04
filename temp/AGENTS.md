# Dream Generated Project Instructions

This folder is a self-contained Dream Kotlin Multiplatform starter project. It is meant to be copied by the initializer, renamed for a new app, then opened directly in OpenCode.

## Start Here

When working in this generated project, read in this order:

1. [.opencode/guidance/opencode-agent-map.md](.opencode/guidance/opencode-agent-map.md)
2. [.opencode/guidance/dream-agent-operating-guide.md](.opencode/guidance/dream-agent-operating-guide.md)
3. [.opencode/knowledge-graph/README.md](.opencode/knowledge-graph/README.md)
4. Relevant topic files in [.opencode/knowledge-graph/best-practices/](.opencode/knowledge-graph/best-practices/README.md)
5. [README.md](README.md)
6. Existing `specs/` metadata when it exists

## Packaged Agent Layer

- [.opencode/opencode.json](.opencode/opencode.json): OpenCode instruction injection and MCP configuration
- [.opencode/agents/](.opencode/agents): Dream agent definitions (dream-general, dream-research, dream-designer, dream-implementer, dream-implementer-ui, dream-tester, dream-inspector)
- [.opencode/commands/](.opencode/commands): Dream slash-command workflows
- [.opencode/guidance/](.opencode/guidance): operating guide and prompt scaffolds
- [.opencode/knowledge-graph/](.opencode/knowledge-graph): architecture rules and retrieval sources
- [maestro/](maestro): Android and iOS smoke-flow scaffolding for generated feature validation
- [.opencode/scripts/dream-preflight.sh](.opencode/scripts/dream-preflight.sh): git, Android SDK, and tooling readiness preflight
- [.opencode/scripts/sync-pbxproj.rb](.opencode/scripts/sync-pbxproj.rb): obsolete under the Kotlin Toolchain (the Xcode project uses a file-system-synchronized folder, so new Swift files are picked up automatically); kept only for reference
- [.opencode/guidance/reference/kotlin-toolchain.md](.opencode/guidance/reference/kotlin-toolchain.md): Kotlin Toolchain commands, project layout, module names, and iOS/Android rules
- [.opencode/scripts/fix-ios-project.sh](.opencode/scripts/fix-ios-project.sh): re-applies hand-maintained `module.xcodeproj` settings (wrapper path, deployment target, bundle id) after the Toolchain regenerates the project
- [.opencode/scripts/remove-sample.sh](.opencode/scripts/remove-sample.sh): removes sample feature files, strips sample deps, and checks for leftovers
- [.opencode/guidance/new-feature-checklist.md](.opencode/guidance/new-feature-checklist.md): contract-first feature wiring, verification, and git checklist

## Build system: Kotlin Toolchain (Alpha)

This project builds with the JetBrains Kotlin Toolchain (`./kotlin` CLI, YAML config, v0.13, Alpha). There is no Gradle or `gradlew`. Modules are listed in `project.yaml`; each has a `module.yaml`, and module names equal directory names (for example `core-domain`, `sample-impl`).

Not supported by Toolchain 0.13: SKIE, Swift export, and the Xcode 27 JSON project format (`project.xcproj`). The Xcode project has an `app` target (the only one with the Kotlin integration script phase) and a hosted `appTests` unit-test target (Swift tests in `iosApp/test`, run with `xcodebuild test -project iosApp/module.xcodeproj -scheme app -destination 'platform=iOS Simulator,name=iPhone 17 Pro' CODE_SIGNING_ALLOWED=NO`; `./kotlin test` runs Kotlin tests only). Alpha tool: if something is unsupported, stop and report; do not work around it with Gradle. Full reference: [.opencode/guidance/reference/kotlin-toolchain.md](.opencode/guidance/reference/kotlin-toolchain.md).

## Working Rules

- Preserve Dream's architecture rules: thin app shells, centralized navigation, split shared core, and `Presentation -> Domain <- Data`.
- Keep domain logic pure Kotlin and keep product behavior in feature modules, not host app modules.
- Do not simplify the template by collapsing modules just to make a task faster.
- Treat Android and iOS as generated app product targets.
- iOS is native SwiftUI only (`iosApp/`); Android UI is Jetpack Compose in Android-only modules. There is no Compose iOS variant.
- When working on the iOS app, load the iOS best-practice files covering project structure, KMP bridge rules, Xcode configuration, and Swift unit-test (`appTests`) packaging.
- Use Android CLI for Android device/emulator setup, install, launch, inspection, logs, and repair loops.
- Use `xcodebuild` and `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture.
- Do not claim success from green commands alone; match the contract, tests, and review expectations in [.opencode/guidance/dream-agent-operating-guide.md](.opencode/guidance/dream-agent-operating-guide.md).

## Validation

Run commands from this project root:

```bash
./.opencode/scripts/dream-preflight.sh
./kotlin build
./kotlin test --platform=android
./kotlin test --platform=iosSimulatorArm64
./kotlin build -m androidApp --platform=android
./kotlin build -m iosApp --platform=iosSimulatorArm64
```

Focused iteration example: `./kotlin test -m sample-impl --platform=android`.

Dream's stricter final verification includes:

```bash
./kotlin build
./kotlin test --platform=android
./kotlin test --platform=iosSimulatorArm64
./kotlin build -m androidApp --platform=android
./kotlin build -m iosApp --platform=iosSimulatorArm64
# Android CLI install/launch/log inspection
# xcrun simctl install/launch/log inspection
```

Use focused commands for iteration, but do not report final success without the required verification behavior or a documented blocker.

## Initializer Boundary

The initializer should only copy this `temp` project, rename it, and perform deterministic identity rewrites. Feature generation, repair, verification, and review belong to the packaged OpenCode workflow.
