# Dream Non-Negotiable Rules

All Dream agents must preserve these rules unless a higher-priority project-specific document explicitly overrides them.

## Architecture

- Preserve thin app shells
- Keep reusable infrastructure in shared modules
- Place product behavior in feature modules
- Enforce `Presentation -> Domain <- Data`
- Keep domain pure Kotlin — no platform dependencies
- Keep navigation centralized
- Keep shared MVI primitives small
- Keep routing concerns out of feature UI where practical

## Resources

- Keep user-visible shared UI strings in shared resources (`shared/core-strings`)

## Platform Targets

- Treat Android and iOS as generated app product targets

## Android Tooling

- Use Android CLI for environment checks, emulator/device setup, APK install, launch, layout/screenshot inspection, logs, and repair loops

## iOS Tooling

- Use `./kotlin build -m iosApp --platform=iosSimulatorArm64` plus `xcrun simctl` for iOS build, simulator install, launch, logs, and crash capture
## iOS UI

- iOS is native SwiftUI only (`iosApp/`); Compose UI is Android-only and there is no Compose-for-iOS variant
- Load the dedicated iOS best-practice files (35-39) before editing native iOS code

## Verification

- Do not report success until verification is green or a blocker is clearly documented
