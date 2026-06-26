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

### 36.6 Cast KotlinDouble explicitly in Swift dictionary values

SKIE exposes Kotlin `Double` as `KotlinDouble` in Swift. Dictionary values that originate from Kotlin retain the `KotlinDouble` wrapper type, which Swift's native `Double` does not implicitly unwrap.

Best practice:

- cast explicitly with `Double(...)` when reading `KotlinDouble` values from Kotlin-produced dictionaries
- do not assume Swift will auto-bridge numeric types through SKIE-produced containers
- keep a small helper or extension if the same conversion appears in multiple places:

```swift
// Kotlin returns Map<String, Any> where numeric values are KotlinDouble
let rawValue = kotlinDict["amount"] as? KotlinDouble
let amount = rawValue.map(Double.init) ?? 0.0
```

### 36.7 Extension function naming determines the generated Swift class name

SKIE generates Swift static methods on a class whose name derives from the Kotlin extension function's file and receiver type. Changing the function signature, file name, or receiver can silently break Swift call sites.

Best practice:

- inspect the generated Swift header after creating or renaming an extension function to confirm the expected class name
- the naming convention follows `{ReceiverType}ExtensionsKt` when the extension is top-level in a Kotlin file
- if Swift cannot find a previously visible extension method, the most likely cause is a rename or signature change that shifted the generated class
- verify the generated framework header under `shared/build/xcode-frameworks/` when Swift-side build errors mention unresolved extension methods

### 36.8 Bridge protocol template for new features

Every feature with an iOS presence follows the same bridge triad: protocol → concrete implementation → mock. The pattern is identical across features and should be copyable from the sample.

**Protocol + Concrete (one file in `iosApp/Core/`):**

```swift
// FeatureNameBridge.swift
protocol FeatureNameBridge {
    func loadItems() async throws -> [FeatureItemSwift]
    func createItem(title: String) async throws -> FeatureItemSwift
}

final class SharedFeatureNameBridge: FeatureNameBridge {
    private let repository: FeatureRepository

    init() {
        repository = iosFeatureRepository()
    }

    func loadItems() async throws -> [FeatureItemSwift] {
        try await repository.loadItems()
            .get()
            .map { self.mapFromKotlin($0) }
    }

    func createItem(title: String) async throws -> FeatureItemSwift {
        let kotlinItem = try await repository.createItem(title: title).get()
        return mapFromKotlin(kotlinItem)
    }

    private func mapFromKotlin(_ item: SharedCore.FeatureItem) -> FeatureItemSwift {
        FeatureItemSwift(
            id: item.id,
            title: item.title,
            amount: Double(truncating: item.amount as NSNumber)
        )
    }
}

private struct BridgeError: LocalizedError {
    let message: String
    var errorDescription: String? { message }
}
```

**Mock (separate file in `iosAppTests/`):**

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
        sampleBridge = SharedCoreBridge()
        featureBridge = SharedFeatureNameBridge()
    }
}
```

### 36.9 Adding new Swift files requires manual pbxproj edits

Xcode does not automatically register new `.swift` files added outside the Xcode GUI. The agent must edit the `.pbxproj` file when introducing new source files to the iOS host.

Best practice:

- add a `PBXBuildFile` entry for the new file referencing its `PBXFileReference`
- add the same `PBXBuildFile` reference to the `Sources` build phase of the `iosApp` target
- mirror the UUID format used by existing entries (the hex-string convention is stable across Xcode 15/16)
- verify the addition with `xcodebuild build` — a missing PBXBuildFile causes the compiler to silently skip the file
- consider keeping a "Sources phase inventory" comment block at the top of the project file if the file list grows long enough that manual tracking becomes error-prone
