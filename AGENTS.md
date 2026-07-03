# Dream Agent Instructions

Dream is a local-first AI app builder for Kotlin Multiplatform apps. This repository now centers on [temp](temp): the self-contained starter project that the initializer copies and renames.

## Start Here

When working on Dream itself, read in this order:

1. [ONBOARDING.md](ONBOARDING.md)
2. [temp/AGENTS.md](temp/AGENTS.md)
3. [temp/.opencode/guidance/opencode-agent-map.md](temp/.opencode/guidance/opencode-agent-map.md)
4. [temp/.opencode/guidance/dream-agent-operating-guide.md](temp/.opencode/guidance/dream-agent-operating-guide.md)
5. [temp/.opencode/knowledge-graph/README.md](temp/.opencode/knowledge-graph/README.md)
6. Relevant topic files in [temp/.opencode/knowledge-graph/best-practices/](temp/.opencode/knowledge-graph/best-practices/README.md)

Prefer linking to those documents in discussions and change summaries instead of restating them.

## Repository Map

- [temp](temp): canonical starter project copied by the initializer
- [ONBOARDING.md](ONBOARDING.md): human-readable guide to the project architecture, workflow, and validation model
- [temp/.features/](temp/.features): generated app contracts, feature specs, decisions, reports, and retrospectives
- [temp/maestro/](temp/maestro): Android and iOS smoke-flow scaffolding
- [temp/.opencode/opencode.json](temp/.opencode/opencode.json): packaged OpenCode instruction injection and MCP config
- [temp/.opencode/agents/](temp/.opencode/agents): Dream agent definitions (dream-general, dream-research, dream-designer, dream-implementer, dream-implementer-ui, dream-tester, dream-inspector)
- [temp/.opencode/commands/](temp/.opencode/commands): Dream slash-command workflows
  - `dream-init` — Initialize a Dream project (git, Android SDK, tooling check)
  - `dream-create` — Create a Dream MVP project
  - `dream-feature` — Generate or update a Dream feature
  - `dream-bug-fix` — Fix logic, crash, runtime, data, and state bugs in any layer
  - `dream-ui-fix` — Fix visual, layout, and rendering issues in Compose and SwiftUI screens
  - `dream-quick-fix` — Quick fix for trivial changes (1-3 files, <50 lines, no new modules)
  - `dream-verify` — Verify and repair a Dream project
  - `dream-review` — Review a Dream project against Dream rules
  - `dream-resume` — Resume a Dream session from saved state
  - `dream-improve` — Apply unapplied feature retrospectives and token optimization
  - `dream-info` — Display Dream tool overview, commands, agents, and features
- [temp/.opencode/guidance/](temp/.opencode/guidance): packaged operating guidance and prompt scaffolds
- [temp/.opencode/guidance/reference/](temp/.opencode/guidance/reference): agent constraints, non-negotiable rules, verification commands/patterns, skip conditions, and token optimization guidelines
- [temp/.opencode/guidance/templates/](temp/.opencode/guidance/templates): feature contract, feature report, feature spec, plan, maestro smoke checklist, retrospective, and verification report templates
- [temp/.opencode/knowledge-graph/README.md](temp/.opencode/knowledge-graph/README.md): packaged reusable architecture rules and source graph
- [temp/.opencode/scripts/dream-preflight.sh](temp/.opencode/scripts/dream-preflight.sh): local readiness check for git, Android SDK, and Maestro
- [temp/.opencode/scripts/sync-pbxproj.rb](temp/.opencode/scripts/sync-pbxproj.rb): auto-registers orphaned Swift files in Xcode project.pbxproj
- [temp/.opencode/scripts/remove-sample.sh](temp/.opencode/scripts/remove-sample.sh): removes sample feature files, strips sample deps, and checks for leftovers

## Working Rules

- Preserve Dream's architecture rules: thin app shells, centralized navigation, split shared core, and `Presentation -> Domain <- Data`.
- Keep domain logic pure Kotlin and keep product behavior in feature modules, not host app modules.
- Do not simplify the template by collapsing modules just to make a task faster.
- Treat Android and iOS as generated app product targets.
- Use Android CLI for Android device/emulator setup, install, launch, inspection, logs, and repair loops.
- Use `xcodebuild` and `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture.
- Use Maestro for black-box smoke flows after platform launch is healthy.
- Do not claim success from green commands alone; match the contract, tests, and review expectations in [temp/.opencode/guidance/dream-agent-operating-guide.md](temp/.opencode/guidance/dream-agent-operating-guide.md).

## Validation

The repository root is not the main Gradle execution surface. For starter validation, run commands from [temp](temp):

```bash
cd temp
./.opencode/scripts/dream-preflight.sh
./gradlew :feature:sample:impl:allTests :shared:navigation:allTests :shared:core:domain:allTests :shared:core:data:allTests :shared:core:presentation:allTests :shared:core:testing:allTests
./gradlew :androidApp:assembleDebug
xcodebuild -project iosApp-swiftui/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17 Pro,OS=26.2' build
```

Dream's stricter final verification target is documented in [temp/.opencode/guidance/dream-agent-operating-guide.md](temp/.opencode/guidance/dream-agent-operating-guide.md):

```bash
./gradlew allTests
./gradlew :androidApp:assembleDebug
# Android CLI install/launch/log inspection
# xcodebuild plus xcrun simctl build/install/launch/log inspection
# Maestro smoke flows for relevant Android and iOS user journeys
```

Use the lighter template commands for focused iteration, but do not report final success without the required verification behavior or a documented blocker.

## When Updating Dream

- If you change generation workflow or success criteria, update [temp/.opencode/guidance/dream-agent-operating-guide.md](temp/.opencode/guidance/dream-agent-operating-guide.md) and the relevant files under [temp/.opencode/guidance/prompts/](temp/.opencode/guidance/prompts/).
- If you change reusable architecture rules, update the corresponding files in [temp/.opencode/knowledge-graph/best-practices/](temp/.opencode/knowledge-graph/best-practices/README.md).
- If you change starter structure or validation commands, update [temp/AGENTS.md](temp/AGENTS.md).
- If you add or change scripts, update the relevant files under [temp/.opencode/scripts/](temp/.opencode/scripts/) and document them in root [ONBOARDING.md](ONBOARDING.md) and [AGENTS.md](AGENTS.md).

## Existing Agent Layer

OpenCode-specific agents and commands live under [temp/.opencode/](temp/.opencode/). Work from [temp](temp) or from an initializer-created copy when exercising the packaged workflow.

Skills provide specialized instructions and workflows for specific tasks.
Use the skill tool to load a skill when a task matches its description.
