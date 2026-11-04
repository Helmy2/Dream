# 38. iOS Testing Practices

Native iOS hosts should keep a copyable test pattern so generated projects can extend it.

### 38.1 Swift unit tests run in a hosted `appTests` target

`iosApp/module.xcodeproj` has two targets: `app` (application, carries the Kotlin Toolchain `Build Kotlin` script phase) and `appTests` (bundle.unit-test, hosted by `app`, bundle id `com.dream.template.ios.tests`). The Toolchain requires exactly one target with the Kotlin integration script phase; a test target without that phase is accepted. The `app` scheme has a TestAction including `appTests`.

Best practice:

- put Swift tests in `iosApp/test/*.swift` (SampleListViewModelTests, SampleDetailViewModelTests, MockSampleRepositoryBridge); that folder belongs to the `appTests` target
- use `@testable import app` (the app's Swift module name is `app`, not `iosApp`)
- run with `xcodebuild test -project iosApp/module.xcodeproj -scheme app -destination 'platform=iOS Simulator,name=iPhone 17 Pro' CODE_SIGNING_ALLOWED=NO` (or `-destination "id=<simulator-udid>"`); this also runs the Toolchain integration script, so no separate `./kotlin build` is needed
- `./kotlin test` runs only Kotlin tests, never Swift tests; cover shared logic there with `./kotlin test --platform=iosSimulatorArm64`
- do not add a second target carrying the Kotlin integration script phase
- if editing the Xcode project, avoid `xcodebuild -convert-project` (see 37.6)

### 38.2 Test native ViewModels through the bridge protocol

Best practice:

- mock the native bridge protocol instead of mocking Kotlin framework internals directly
- test state transitions such as loading, content, error, editing, and save completion in native ViewModel tests
- inject time closures or simple native collaborators when timestamp assertions are part of the host logic

### 38.3 Keep SwiftUI host tests focused on native orchestration

Best practice:

- test bridge wiring, state transitions, and native formatting behavior in Swift tests
- keep shared business logic assertions in Kotlin tests where the logic actually lives
- avoid duplicating shared-domain behavior tests across both Kotlin and Swift unless the host adds real native logic

### 38.4 Verify native hosts with the Toolchain build and runtime smoke flows

Best practice:

- use `./kotlin build -m iosApp --platform=iosSimulatorArm64` for structural validation of the native iOS host
- use `./kotlin test --platform=iosSimulatorArm64` for Kotlin tests running on the iOS simulator
- use the `xcodebuild test` command from 38.1 for Swift unit tests
- use `xcrun simctl` for install, launch, and log inspection
