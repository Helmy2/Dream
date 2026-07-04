# 38. iOS Testing Practices

Native iOS hosts should ship with a copyable test pattern so generated projects can extend it immediately.

### 38.1 Package a real XCTest target with the SwiftUI host

Best practice:

- include an `iosAppTests` unit-test target in the native SwiftUI variant
- keep sample ViewModel tests in that target so generated apps inherit a working baseline
- use shared schemes so `xcodebuild test` can run without relying on local Xcode user state

### 38.2 Test native ViewModels through the bridge protocol

Best practice:

- mock the native bridge protocol instead of mocking SKIE-generated framework internals directly
- test state transitions such as loading, content, error, editing, and save completion in native ViewModel tests
- inject time closures or simple native collaborators when timestamp assertions are part of the host logic

### 38.3 Keep SwiftUI host tests focused on native orchestration

Best practice:

- test bridge wiring, state transitions, and native formatting behavior in XCTest
- keep shared business logic assertions in Kotlin tests where the logic actually lives
- avoid duplicating shared-domain behavior tests across both Kotlin and Swift unless the host adds real native logic

### 38.4 Verify native hosts with both xcodebuild and runtime smoke flows

Best practice:

- use `xcodebuild build` and `xcodebuild test` for structural validation of the native iOS host
- use `xcrun simctl` for install, launch, and log inspection
