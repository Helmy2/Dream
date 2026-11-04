# Dream Agent Constraints

## dream-general

- Do not edit implementation files directly — delegate to relevant implementer
- Do not write UI code — Compose screens, SwiftUI views, ViewModels
- Do not modify `feature/<name>-impl` or `iosApp/`
- Do not collapse the modular template
- Do not fix compilation errors directly — identify the failing layer and delegate
- Do not commit blocked, partial, unreviewed, or unsafe worktree states
- Never amend, force push, reset, or revert unrelated changes unless user explicitly requests
- **Do not skip Phase 0 (Plan & User Review)** — the plan must be written and user-confirmed before any implementation, research, or design begins
- **Do not commit directly to main** — create a feature branch after Phase 0, commit to the branch, and only merge to main after user confirmation
- **Do not merge without user approval** — present merge summary and get explicit confirmation before squash-merging

## dream-implementer

- Do not write UI code — delegate to `dream-implementer-ui`
- Do not modify `feature/<name>-impl` — owned by `dream-implementer-ui`
- Do not modify `iosApp/` — owned by `dream-implementer-ui`
- Do not collapse the modular template
- Do not move business logic into host app modules
- Do not introduce cross-feature coupling by default
- Do not claim verified unless `dream-general` coordinated verification
- Do not commit directly unless `dream-general` completed verification and review

## dream-implementer-ui

- Do not modify domain, data, or api modules — owned by `dream-implementer`
- Do not introduce cross-feature `impl`-to-`impl` coupling
- Do not claim complete without both platform surfaces (when platform=both)
- Do not commit directly
- When platform=android: do not modify iOS files
- When platform=ios: do not modify Kotlin source files, do not modify shared domain or data logic
- Do not use hardcoded string literals for user-visible text

## dream-tester

- Do not modify feature implementation code
- Do not modify contracts, specs, or architecture rules
- Do not claim green status when any check is `blocked by environment` or `not run`
- Do not commit directly

## dream-inspector

- Report `APPROVED` when clean, otherwise itemized defect list
- If tool-level failure prevents review, reply `INSPECTOR_FAILED: <precise error>`

## dream-designer

- Do not edit files outside `specs/`
- Do not write any code
- Do not act as final reviewer

## dream-research

- Do not edit files
- Do not perform repair
- Do not provide architecture signoff
