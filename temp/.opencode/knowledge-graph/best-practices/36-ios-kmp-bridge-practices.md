# 36. iOS KMP Bridge Practices

The iOS bridge is the most important boundary between native SwiftUI and shared Kotlin code.

### 36.1 Use one dedicated bridge per native host surface

Best practice:

- keep one bridge type such as `SharedCoreBridge` as the native app's main access point into shared Kotlin repositories or use cases
- expose small async methods shaped for the native host instead of letting views call exported Kotlin APIs directly
- keep Kotlin-to-Swift mapping logic inside the bridge boundary

### 36.2 Prefer Swift-friendly async throws APIs over raw Result wrappers

Best practice:

- wrap Kotlin `Result<T>`-based shared APIs with throwing extension functions before Swift consumes them
- let SwiftUI callers use `async throws` methods instead of pattern-matching Kotlin result wrappers
- keep error translation near the export boundary so views and ViewModels stay idiomatic in Swift

### 36.3 Trust SKIE's Swift surface, not guessed ObjC names

Best practice:

- verify generated Swift names for enum cases, initializer labels, and async wrappers after shared API changes
- use the Swift-native enum cases produced by SKIE instead of guessing underscore-suffixed ObjC names
- verify property and initializer differences when Kotlin symbols collide with NSObject names such as `description`

### 36.4 Export transitive shared types intentionally

Best practice:

- if Swift should see a type from another shared module, the framework binary must `export(...)` that module
- dependencies along that chain must use `api(...)` or the transitive types will disappear from the Swift surface
- when Swift suddenly cannot see a shared type, inspect framework export configuration before debugging Swift code

### 36.5 Keep the SwiftUI host decoupled enough to test without Kotlin rebuild tricks

Best practice:

- define a narrow bridge protocol for native ViewModels when it improves host-side unit testing
- inject the bridge into SwiftUI ViewModels instead of hard-coding concrete framework calls in every state holder
- keep the production bridge thin and keep mocks in the native test target
