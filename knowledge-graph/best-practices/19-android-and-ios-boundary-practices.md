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
- export domain/data capabilities cleanly to Swift

### 18.3 Use provider classes for Swift access

A useful interop pattern is exposing feature use cases to Swift through dedicated Koin-backed provider classes.

Why this works well:

- Swift code does not need to know the full Koin graph
- feature ownership remains clear
- interop API stays smaller than exposing every class directly
