# Dream Agent Constraints

## dream-general

- Do not edit implementation files directly — delegate to relevant implementer
- Do not write UI code — Compose screens, SwiftUI views, ViewModels
- Do not modify `feature/<name>/impl` or `iosApp-swiftui/`
- Do not collapse the modular template
- Do not fix compilation errors directly — identify the failing layer and delegate
- Do not commit blocked, partial, unreviewed, or unsafe worktree states
- Never amend, force push, reset, or revert unrelated changes unless user explicitly requests

## dream-implementer

- Do not write UI code — delegate to `dream-implementer-android` and `dream-implementer-ios`
- Do not modify `feature/<name>/impl` — owned by `dream-implementer-android`
- Do not modify `iosApp-swiftui/` — owned by `dream-implementer-ios`
- Do not collapse the modular template
- Do not move business logic into host app modules
- Do not introduce cross-feature coupling by default
- Do not claim verified unless `dream-general` coordinated verification
- Do not commit directly unless `dream-general` completed verification and review

## dream-implementer-android

- Do not modify domain, data, or api modules — owned by `dream-implementer`
- Do not modify iOS files — owned by `dream-implementer-ios`
- Do not introduce cross-feature `impl`-to-`impl` coupling
- Do not claim complete without iOS surface
- Do not commit directly

## dream-implementer-ios

- Do not modify Kotlin source files — owned by `dream-implementer` or `dream-implementer-android`
- Do not modify shared domain or data logic
- Do not introduce cross-feature coupling
- Do not commit directly
- Do not use hardcoded string literals for user-visible text
- Do not claim complete without Android surface

## dream-tester

- Do not modify feature implementation code
- Do not modify contracts, specs, or architecture rules
- Do not claim green status when any check is `blocked by environment` or `not run`
- Do not commit directly

## dream-inspector

- Report `APPROVED` when clean, otherwise itemized defect list
- If tool-level failure prevents review, reply `INSPECTOR_FAILED: <precise error>`

## dream-designer

- Do not edit files outside `.features/`
- Do not write any code
- Do not act as final reviewer

## dream-explore, dream-context-scout, dream-search

- Do not edit files
- Do not perform repair
- Do not provide architecture signoff
