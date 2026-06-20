# 26. Anti-Patterns To Avoid When Reusing This Architecture

These are the main things to avoid in future projects, even if traces of them exist here.

- do not let feature data modules depend casually on other feature data modules
- do not mix raw preference parsing into UI or domain layers
- do not call system time directly throughout the app
- do not hardcode user-visible strings in shared UI code
- do not let navigation route types leak into every feature module by default
- do not instantiate repositories, use cases, or ViewModels inside recomposing composables
- do not create giant convention plugins that hide the whole build
- do not make the design system too opinionated for feature-specific layout needs
- do not build a complicated MVI framework if a small base class is enough
- do not use `String.format` or `java.text.*` or `java.util.*` in `commonMain` source sets — these are JVM-only APIs that break iOS compilation
- do not use `ExposedDropdownMenuBox.menuAnchor()` — this API is deprecated in Material3; use `MenuAnchorType` instead
- do not keep dead code in shared core modules (e.g., unused sealed classes, unused data classes, unused expect/actual declarations) — remove it during implementation
- do not let documentation (README, AGENTS.md, verification commands) reference modules that no longer exist — update them when changing the module structure
- do not generate feature tests for only the presentation layer — domain model tests and data layer tests are required for every feature
- do not leave `Clock.System.now()` calls in feature code — always inject `TimeProvider`
