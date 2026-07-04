# Dream Generated Project Instructions

This folder is a self-contained Dream Kotlin Multiplatform starter project. It is meant to be copied by the initializer, renamed for a new app, then opened directly in OpenCode.

## Start Here

When working in this generated project, read in this order:

1. [.opencode/guidance/opencode-agent-map.md](.opencode/guidance/opencode-agent-map.md)
2. [.opencode/guidance/dream-agent-operating-guide.md](.opencode/guidance/dream-agent-operating-guide.md)
3. [.opencode/knowledge-graph/README.md](.opencode/knowledge-graph/README.md)
4. Relevant topic files in [.opencode/knowledge-graph/best-practices/](.opencode/knowledge-graph/best-practices/README.md)
5. [README.md](README.md)
6. Existing `.features/` metadata when it exists

## Packaged Agent Layer

- [.opencode/opencode.json](.opencode/opencode.json): OpenCode instruction injection and MCP configuration
- [.opencode/agents/](.opencode/agents): Dream agent definitions (dream-general, dream-research, dream-designer, dream-implementer, dream-implementer-ui, dream-tester, dream-inspector)
- [.opencode/commands/](.opencode/commands): Dream slash-command workflows
- [.opencode/guidance/](.opencode/guidance): operating guide and prompt scaffolds
- [.opencode/knowledge-graph/](.opencode/knowledge-graph): architecture rules and retrieval sources
- [maestro/](maestro): Android and iOS smoke-flow scaffolding for generated feature validation
- [scripts/dream-preflight.sh](scripts/dream-preflight.sh): git, Android SDK, and tooling readiness preflight
- [scripts/sync-pbxproj.rb](scripts/sync-pbxproj.rb): auto-registers orphaned Swift files in Xcode project.pbxproj
- [scripts/remove-sample.sh](scripts/remove-sample.sh): removes sample feature files, strips sample deps, and checks for leftovers
- [.opencode/guidance/new-feature-checklist.md](.opencode/guidance/new-feature-checklist.md): contract-first feature wiring, verification, and git checklist

## Working Rules

- Preserve Dream's architecture rules: thin app shells, centralized navigation, split shared core, and `Presentation -> Domain <- Data`.
- Keep domain logic pure Kotlin and keep product behavior in feature modules, not host app modules.
- Do not simplify the template by collapsing modules just to make a task faster.
- Treat Android and iOS as generated app product targets.
- `iosApp-compose` is the Compose Multiplatform iOS variant; `iosApp-swiftui` is the native SwiftUI variant. The agent removes the unused option during project instantiation.
- When working on the native SwiftUI variant, load the iOS best-practice files covering project structure, KMP bridge rules, Xcode configuration, and XCTest packaging.
- Use Android CLI for Android device/emulator setup, install, launch, inspection, logs, and repair loops.
- Use `xcodebuild` and `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture.
- Do not claim success from green commands alone; match the contract, tests, and review expectations in [.opencode/guidance/dream-agent-operating-guide.md](.opencode/guidance/dream-agent-operating-guide.md).

## Validation

Run commands from this project root:

```bash
./scripts/dream-preflight.sh
./gradlew :feature:sample:impl:allTests :shared:navigation:allTests :shared:core:domain:allTests :shared:core:data:allTests :shared:core:presentation:allTests :shared:core:testing:allTests
./gradlew :androidApp:assembleDebug
xcodebuild -project iosApp-swiftui/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17 Pro,OS=26.2' build
```

Dream's stricter final verification includes:

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
# Android CLI install/launch/log inspection
# xcodebuild plus xcrun simctl build/install/launch/log inspection
```

Use focused commands for iteration, but do not report final success without the required verification behavior or a documented blocker.

## Initializer Boundary

The initializer should only copy this `temp` project, rename it, and perform deterministic identity rewrites. Feature generation, repair, verification, and review belong to the packaged OpenCode workflow.
