# 20. Platform-Specific Capability Patterns

This architecture style supports good patterns for hardware and platform services.

### 19.1 Abstract platform capabilities behind shared contracts

Examples in this app include:

- location
- notifications
- encryption
- compass sensors

Best practice:

- define the contract in shared code
- provide platform implementations separately
- inject them through DI

### 19.2 Native UI is acceptable for performance-sensitive or platform-heavy cases

The project history around Qibla and the final native SwiftUI architecture both support the same lesson:

- use native UI where platform fidelity or performance warrants it
- do not overcommit to shared UI if it makes platform behavior worse

### 19.3 Prefer explicit iOS factories over starting shared DI from SwiftUI

Native SwiftUI hosts should not need the full shared DI container just to load one feature.

Best practice:

- provide explicit iOS-facing factory functions such as `iosSampleRepository()` from `iosMain` when the SwiftUI host needs a small shared surface
- keep those factories in `umbrella-core` or another non-Compose export module
- avoid forcing SwiftUI code to understand or start Koin when a direct factory is enough

### 19.4 Keep platform bridge code close to the host boundary

The Swift-to-Kotlin bridge is platform-specific glue, not shared business logic.

Best practice:

- place native bridge code in the iOS host app under a dedicated `Core/` folder or equivalent boundary
- keep Kotlin-to-Swift mapping localized to the bridge instead of spreading exported model conversion throughout views
- keep platform capability adapters close to the host boundary so they can evolve without leaking through feature UI files
