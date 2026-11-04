# 36. iOS KMP Bridge Practices

The iOS bridge is the most important boundary between native SwiftUI and shared Kotlin code. iOS uses plain Kotlin/Native Objective-C interop: SKIE, Swift export, and a platform-scoped KMP-NativeCoroutines are not supported by Kotlin Toolchain 0.13.

### 36.1 Use one dedicated bridge per native host surface

Best practice:

- keep one bridge type per feature as the native app's access point into shared Kotlin repositories or use cases
- expose small async methods shaped for the native host instead of letting views call exported Kotlin APIs directly
- keep Kotlin-to-Swift mapping logic inside the bridge boundary

### 36.2 Prefer Swift-friendly async throws APIs over raw Result wrappers

Best practice:

- wrap Kotlin `Result<T>`-based shared APIs with Kotlin `*OrThrow` suspend wrappers before Swift consumes them
- `suspend fun` is exposed to Swift as `async throws` automatically; cancelling the Swift task does not cancel the Kotlin coroutine
- let SwiftUI callers use `async throws` methods instead of pattern-matching Kotlin result wrappers
- keep error translation near the export boundary so views and ViewModels stay idiomatic in Swift

### 36.3 Verify the generated Objective-C names, do not guess

Best practice:

- Swift code uses `import KotlinModules`, the single framework that contains `iosApp`, its dependency modules, and their transitive code (the old `SharedCore` framework no longer exists)
- qualify types as `KotlinModules.SampleItem` when they clash with Swift names
- Kotlin enums and sealed classes are Swift classes, not Swift enums: `switch` needs a `default:` branch, and subtypes need a cast (`as! Foo.Bar`)
- for sealed types add a Kotlin-side `kind` enum so Swift can switch on it
- verify property and initializer differences when Kotlin symbols collide with NSObject names such as `description`

> **See also:** [37. iOS Xcode Configuration Practices](37-ios-xcode-configuration-practices.md) for Xcode project setup.

### 36.4 Make shared types visible intentionally

Best practice:

- the iOS app sees only modules reachable from `iosApp/module.yaml` dependencies (`//shared/umbrella-core`)
- if Swift should see a type from another shared module, that module must be a dependency of `umbrella-core`, and intermediate modules should use `exported` where types appear in public signatures
- when Swift suddenly cannot see a shared type, inspect the module dependency chain before debugging Swift code

### 36.5 Keep the SwiftUI host decoupled enough to test without Kotlin rebuild tricks

Best practice:

- define a narrow bridge protocol for native ViewModels when it improves host-side unit testing
- inject the bridge into SwiftUI ViewModels instead of hard-coding concrete framework calls in every state holder
- keep the production bridge thin and keep mocks separate from production code

### 36.6 Cast KotlinDouble explicitly in Swift dictionary values

Kotlin `Double` inside generic containers is exposed as `KotlinDouble` in Swift. Dictionary values that originate from Kotlin retain the `KotlinDouble` wrapper type, which Swift's native `Double` does not implicitly unwrap.

Best practice:

- cast explicitly with `Double(...)` when reading `KotlinDouble` values from Kotlin-produced dictionaries
- do not assume Swift will auto-bridge numeric types through Kotlin-produced containers
- keep a small helper or extension if the same conversion appears in multiple places:

```swift
// Kotlin returns Map<String, Any> where numeric values are KotlinDouble
let rawValue = kotlinDict["amount"] as? KotlinDouble
let amount = rawValue.map(Double.init) ?? 0.0
```

### 36.7 Top-level Kotlin functions become `<FileName>Kt` static methods

Top-level Kotlin functions are exposed as static methods on a generated class named after the Kotlin file, e.g. `SampleRepositoryFactoryKt.iosSampleRepository()` and `SampleRepositoryExtensionsKt.loadItemsOrThrow(repository)`. Renaming the file or the function can silently break Swift call sites.

Best practice:

- after creating or renaming a top-level function or extension, confirm the expected `<FileName>Kt` class name
- if Swift cannot find a previously visible method, the most likely cause is a file rename or signature change that shifted the generated class
- extension functions are called as static methods taking the receiver as the first argument, not as Swift instance methods

### 36.8 Bridge protocol template for new features

Every feature with an iOS presence follows the same bridge triad: protocol → concrete implementation → mock. The pattern is identical across features and should be copyable from the sample.

**Protocol + Concrete (one file in `iosApp/src/Core/`):**

```swift
// FeatureNameBridge.swift
import KotlinModules

protocol FeatureNameBridge {
    func loadItems() async throws -> [FeatureItemSwift]
    func createItem(title: String) async throws -> FeatureItemSwift
}

final class SharedFeatureNameBridge: FeatureNameBridge {
    private let repository: FeatureRepository

    init() {
        repository = FeatureRepositoryFactoryKt.iosFeatureRepository()
    }

    func loadItems() async throws -> [FeatureItemSwift] {
        try await FeatureRepositoryExtensionsKt.loadItemsOrThrow(repository)
            .map { self.mapFromKotlin($0) }
    }

    func createItem(title: String) async throws -> FeatureItemSwift {
        let kotlinItem = try await FeatureRepositoryExtensionsKt.createItemOrThrow(repository, title: title)
        return mapFromKotlin(kotlinItem)
    }

    private func mapFromKotlin(_ item: KotlinModules.FeatureItem) -> FeatureItemSwift {
        FeatureItemSwift(
            id: item.id,
            title: item.title,
            amount: Double(truncating: item.amount as NSNumber)
        )
    }
}
```

**Mock (separate file in `iosApp/test/`, part of the `appTests` target):**

```swift
// MockFeatureNameBridge.swift
final class MockFeatureNameBridge: FeatureNameBridge {
    var itemsToReturn: [FeatureItemSwift] = []
    var createdItem: FeatureItemSwift?
    var loadItemsError: Error?
    var createItemError: Error?
    var createCallCount = 0

    func loadItems() async throws -> [FeatureItemSwift] {
        if let error = loadItemsError { throw error }
        return itemsToReturn
    }

    func createItem(title: String) async throws -> FeatureItemSwift {
        createCallCount += 1
        if let error = createItemError { throw error }
        if let item = createdItem { return item }
        return FeatureItemSwift(id: UUID().uuidString, title: title, amount: 0)
    }
}
```

**Wiring in `AppDependencies.swift`:**

```swift
// Extend the existing AppDependencies
final class AppDependencies {
    static let shared = AppDependencies()
    let sampleBridge: any SampleRepositoryBridge
    let featureBridge: any FeatureNameBridge

    private init() {
        sampleBridge = SharedSampleBridge()
        featureBridge = SharedFeatureNameBridge()
    }
}
```

### 36.9 New Swift files need no project registration

The Xcode project (`iosApp/module.xcodeproj`) uses a file-system-synchronized folder, so new `.swift` files added under `iosApp/src` are picked up automatically. The old `sync-pbxproj.rb` registration step is no longer needed.

Best practice:

- add or remove Swift files directly in `iosApp/src`
- verify with `./kotlin build -m iosApp --platform=iosSimulatorArm64`

### 36.10 OrThrow bridge extensions for Kotlin Result unwrapping

The Dream template uses Kotlin-side `OrThrow` extension functions instead of unwrapping `Result` in Swift. This keeps error throwing on the Kotlin side.

Best practice:

- create `suspend fun Repository.methodNameOrThrow(...): ReturnType` extensions in `shared/umbrella-core` for each repository method that Swift needs
- unwrap `Result<T>` in the extension: return `result.value` on `Result.Success`, throw on `Result.Failure`
- place the extensions in a file named `<Feature>RepositoryExtensions.kt` under `shared/umbrella-core/src/`
- the bridge calls `try await <Feature>RepositoryExtensionsKt.methodNameOrThrow(repository)`

Template for new features:

```kotlin
// shared/umbrella-core/src/.../FeatureRepositoryExtensions.kt
package com.dream.template.shared.di

import com.dream.template.feature.<name>.domain.model.<Type>
import com.dream.template.feature.<name>.domain.repository.<Feature>Repository
import com.dream.template.shared.core.domain.Result

suspend fun <Feature>Repository.loadItemsOrThrow(): List<<Type>> = when (val result = loadItems()) {
    is Result.Success -> result.value
    is Result.Failure -> throw RuntimeException(result.error.toString())
}
```

Error fidelity note:

- `RuntimeException(result.error.toString())` loses typed `DomainError` information
- for MVP this is acceptable; for production, consider mapping domain errors to typed Swift enums at the bridge boundary

### 36.11 Flows in Swift without SKIE

Only add flow helpers when a screen needs them; the sample iOS app uses suspend functions only.

Best practice:

- expose a Kotlin helper that collects the flow on `Dispatchers.Main` and returns a cancellable subscription
- wrap that helper in an `AsyncStream` in Swift and cancel the subscription in `onTermination`
- generic ObjC classes need `T: AnyObject`, and Swift extensions cannot use the generic parameter, so use a free function instead of an extension
- Swift export and SKIE-style `for await` over a Kotlin `Flow` are not supported by Kotlin Toolchain 0.13

Verifying generated names:

- build with `./kotlin build -m iosApp --platform=iosSimulatorArm64` and read compiler errors for the exact Objective-C names
- verify enum/sealed subtype handling and async signatures after changing Kotlin APIs
- check for naming collisions with NSObject properties (e.g., `description`, `debugDescription`)
