# Dream KMP Base Template

This template follows JetBrains' new top-level Kotlin Multiplatform app structure and Dream's modular internal architecture.

Modules:

- `androidApp`: Android host app
- `iosApp`: iOS host app that consumes the exported `Shared` framework
- `desktopApp`: desktop preview surface and MCP hot reload integration point
- `shared/core/*`: split shared infrastructure modules
- `shared/designsystem`: theme, tokens, and reusable components
- `shared/navigation`: centralized shell and navigation infrastructure
- `shared/umbrella-core`: exported shared business logic aggregation
- `shared/umbrella-ui`: exported shared UI composition root
- `feature/sample/*`: sample feature split into `domain`, `data`, and `presentation`
- `build-logic`: Dream convention plugins used by the modular template

Key architecture rules:

- thin app shells
- `Presentation -> Domain <- Data`
- domain stays pure Kotlin
- shared core is split by responsibility
- navigation stays centralized

Verification commands:

```bash
./gradlew :feature:sample:presentation:allTests :shared:core:domain:allTests :shared:core:data:allTests :shared:core:presentation:allTests :shared:core:storage:allTests :shared:core:testing:allTests
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:compileKotlinJvm
```
