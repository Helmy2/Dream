# 37. iOS Xcode Configuration Practices

The packaged iOS host should work cleanly with Xcode while remaining deterministic for generated projects.

### 37.1 Let the Toolchain drive framework building from Xcode

The Xcode project is `iosApp/module.xcodeproj` (targets `app` and `appTests`; test scheme `iosApp`), checked into git. The Toolchain regenerates `app.xcscheme` on every build and discards edits to it, so the test action lives in the separate `iosApp.xcscheme` (do not delete it); always pass `-scheme iosApp` to `xcodebuild test`. The Kotlin Toolchain builds the `KotlinModules` framework for it.

Best practice:

- keep `KOTLIN_CLI_WRAPPER_PATH = ../kotlin` in `module.xcodeproj/project.pbxproj` so Xcode can invoke the `./kotlin` wrapper
- do not add hand-written shell phases that invoke Gradle tasks; there is no Gradle
- keep `iosApp/module.yaml` as `product: ios/app` with `dependencies: [//shared/umbrella-core]`

### 37.2 Set the deployment target manually

Best practice:

- set `IPHONEOS_DEPLOYMENT_TARGET = 17.0` in `project.pbxproj`; the Toolchain does not set one, and without it the app only installs on the newest iOS
- re-apply `KOTLIN_CLI_WRAPPER_PATH`, `PRODUCT_BUNDLE_IDENTIFIER`, and the deployment target if the Xcode project is ever regenerated
- verify the resolved settings when simulator or device builds behave differently

### 37.3 Keep product identity in the project and Info.plist

Best practice:

- set the bundle id through `PRODUCT_BUNDLE_IDENTIFIER` (template value `com.dream.template.ios`) in `project.pbxproj`
- keep `CFBundleDisplayName` in `iosApp/src/Info.plist`
- there is no `Configuration/Config.xcconfig`; let project instantiation rewrite the bundle id and display name
- keep visible app identity and bundle identity easy to audit during template reuse

### 37.4 Sources are file-system-synchronized

Best practice:

- Swift sources, `Assets.xcassets`, `Localizable.xcstrings`, and `Info.plist` live in `iosApp/src`
- the project uses a file-system-synchronized folder, so new Swift files are picked up automatically and no pbxproj sync script is needed
- removing a Swift package dependency leaves stale references; delete `module.xcodeproj` and re-apply the settings above

### 37.5 Prefer shared schemes and keep project.pbxproj

Best practice:

- keep the shared scheme in the committed project; do not depend on user-specific `xcuserdata`
- keep the classic `project.pbxproj` format; the new Xcode 27 JSON project format (`project.xcproj`) is not supported by Kotlin Toolchain 0.13
- it can be converted later with `xcodebuild -convert-project "Xcode Project" -project module.xcodeproj`

### 37.6 Keep the Build Kotlin script phase a single string

The project has two targets: `app` (carries the `Build Kotlin` script phase) and `appTests` (hosted unit-test target, no Kotlin phase). The Toolchain requires exactly one target with the Kotlin integration script phase.

Best practice:

- `xcodebuild -convert-project` rewrites the phase `shellScript` from a single string into a multi-line list, and the Toolchain then rejects the project with "must contain a single target with the Kotlin Toolchain integration"; do not run it
- keep `shellScript` exactly the single string `"# !KOTLIN INTEGRATION STEP!\n# This script is managed by the Kotlin Toolchain, do not edit manually!\n\"${KOTLIN_CLI_WRAPPER_PATH}\" tool xcode-integration\n";`
- the Xcode 27 JSON project format (`project.xcproj`) is still not supported by Toolchain 0.13
