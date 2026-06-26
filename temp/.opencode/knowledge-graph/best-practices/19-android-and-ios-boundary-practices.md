# 19. Android And iOS Boundary Practices

A strong KMP architecture does not need to force the same UI stack on both platforms.

### 18.1 Share business logic, not UI by default

Current strong pattern:

- Android uses Compose Multiplatform UI
- iOS uses native SwiftUI
- both consume the same shared domain and data logic

This is an excellent model for product teams that want shared logic without sacrificing native iOS UX.

### 18.2 Export pure business logic to iOS

A dedicated iOS export module can aggregate domain and data modules for iOS.

Best practice:

- keep the iOS export framework free of Android-only UI dependencies
- keep SwiftUI-facing shared core modules free of Compose, Navigation 3 UI, and Koin Compose dependencies
- export domain/data capabilities cleanly to Swift

### 18.3 Use provider classes for Swift access

A useful interop pattern is exposing feature use cases to Swift through dedicated Koin-backed provider classes.

Why this works well:

- Swift code does not need to know the full Koin graph
- feature ownership remains clear
- interop API stays smaller than exposing every class directly

### 18.4 Split the exported iOS surface by UI strategy

Dream uses two different umbrella modules for iOS because the consumers are different:

- `shared/umbrella-core` exports non-Compose business logic for native SwiftUI hosts
- `shared/umbrella-ui` exports the Compose entry point for Compose Multiplatform iOS hosts

Best practice:

- keep native SwiftUI hosts dependent on `umbrella-core`, not on Compose UI aggregators
- keep Compose-based iOS hosts dependent on `umbrella-ui`, not on native Swift bridge code
- do not make the SwiftUI export framework pull in Compose, Navigation 3 UI, or Koin Compose dependencies just to simplify one host app

### 18.5 Export transitive shared types deliberately

Kotlin Multiplatform frameworks do not automatically expose every transitive dependency type to Swift.

Best practice:

- when an exported framework should expose types from dependent modules, declare `export(projects....)` for those modules in the framework binary configuration
- the exported dependency chain must use `api(...)`, not `implementation(...)`, or Swift will not see the transitive types cleanly
- verify the exported header and Swift module after changes instead of assuming Gradle dependency visibility matches Swift visibility

### 18.6 Keep the native host thin even when iOS is fully native

Native SwiftUI does not mean business logic belongs in Swift.

Best practice:

- keep SwiftUI app code focused on navigation, state presentation, and platform-specific interactions
- keep repository implementations, persistence, and domain behavior in shared Kotlin modules
- use the iOS host only as the composition boundary between native UI and exported shared logic
