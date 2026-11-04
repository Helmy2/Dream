# Kotlin Toolchain Reference (replaces Gradle)

Dream builds with the JetBrains **Kotlin Toolchain** (`./kotlin` CLI, YAML config, v0.13, Alpha). There is no Gradle, no `build.gradle.kts`, no `settings.gradle.kts`, no `build-logic`, no convention plugins, no `gradlew`. Run every command from the project root.

## Commands

| Task | Command |
|------|---------|
| Build everything | `./kotlin build` |
| Build one module | `./kotlin build -m <module>` |
| Build one platform | `./kotlin build --platform=android` / `--platform=iosSimulatorArm64` |
| Run all tests | `./kotlin test --platform=android` then `./kotlin test --platform=iosSimulatorArm64` |
| Test one module | `./kotlin test -m <module> --platform=android` |
| Show resolved config | `./kotlin show settings -m <module>` |
| Clean | `./kotlin clean` (also required after changing a Compose resources `packageName`) |
| Update wrapper | `./kotlin update` |

Gradle task mapping: `allTests` -> `./kotlin test --platform=...` (run per platform); `:androidApp:assembleDebug` -> `./kotlin build -m androidApp --platform=android`; `:feature:x:impl:allTests` -> `./kotlin test -m x-impl --platform=android`.

Redirect long build/test output to a log file and grep it (`> /tmp/x.log 2>&1`).

## Project layout

```
project.yaml                      # list of modules
libs.versions.toml                # version catalog (versions + libraries only, no plugins)
templates/*.module-template.yaml  # replace convention plugins
kotlin, kotlin.bat                # CLI wrapper (commit these)
androidApp/                       # product: android/app
iosApp/                           # product: ios/app (native SwiftUI only)
shared/core-domain|core-data|core-presentation|core-testing|core-strings
shared/designsystem|navigation|umbrella-core|umbrella-ui
feature/<name>-domain|-data|-api|-impl
```

- **Module name = directory name and must be unique.** Leaf directories are therefore `core-domain`, `sample-data`, ... (never two directories both called `domain`). Refer to modules as `//shared/core-domain`, `//feature/sample-impl` in `dependencies`. Gradle paths like `:shared:core:domain` no longer exist. Kotlin package names are unchanged (`com.dream.template.shared.core.domain`).
- New module: create the directory, add `module.yaml`, add it to `project.yaml`.

## Source layout (replaces `src/commonMain/kotlin` etc.)

| Gradle | Toolchain |
|--------|-----------|
| `src/commonMain/kotlin` | `src` |
| `src/androidMain/kotlin` | `src@android` |
| `src/iosMain/kotlin` | `src@ios` |
| `src/commonTest/kotlin` | `test` |
| `src/androidHostTest/kotlin` | `test@android` |
| `src/commonMain/composeResources` | `composeResources` (module root) |
| `src/androidMain/res` | `res` (module root) |
| `src/main/AndroidManifest.xml` (app) | `src/AndroidManifest.xml` |

## module.yaml essentials

```yaml
product:
  type: kmp/lib                       # or android/app, ios/app
  platforms: [android, iosArm64, iosSimulatorArm64]   # [android] for Android-only UI modules

apply:
  - //templates/common.module-template.yaml           # always
  - //templates/compose.module-template.yaml          # Compose UI modules (Android-only)

dependencies:                         # also dependencies@android, dependencies@ios, test-dependencies
  - //shared/core-domain: exported    # `exported` replaces Gradle `api`; default is `implementation`
  - $libs.kotlinx.coroutines.core     # version catalog reference

settings:
  android:
    namespace: com.dream.template.<path>
  kotlin:
    compilerPlugins:                  # NOT platform-scopable
      - id: io.insert-koin.compiler.plugin
        dependency: $libs.koin.compiler.plugin
        options:
          moduleId: <module name>     # REQUIRED and unique per module (avoids duplicate Koin hint classes in the APK)
    ksp:
      processors: [$libs.androidx.room.compiler]
      processorOptions:
        room.schemaLocation: ./schemas
```

Templates (`templates/`): `common` (Kotlin 2.4.0, `-Xexpect-actual-classes`, serialization json, JVM release 17, Android SDK 37/28, coroutines-android, Mokkery in tests only), `compose` (Compose 1.11.1, lifecycle-runtime-compose, icons, ui-tooling), `feature-impl` (compose + turbine for tests).

## Platform scope rules (Dream architecture on the Toolchain)

- **Android UI is Jetpack Compose, Android-only.** `designsystem`, `navigation`, `core-strings`, `umbrella-ui`, `<feature>-impl` are `kmp/lib` with `platforms: [android]`. There is no Compose for iOS and no `iosApp-compose`.
- **iOS UI is native SwiftUI only** (`iosApp/src`). Shared code that iOS needs lives in KMP modules (`core-domain`, `core-data`, `<feature>-domain`, `<feature>-data`, `<feature>-api`, `core-presentation`, `umbrella-core`).
- `<feature>-impl` (ViewModel + Compose screens + DI) is Android-only; iOS gets its feature logic through the domain/data modules and a Swift ViewModel.
- Domain stays pure Kotlin. Dependency direction stays `Presentation -> Domain <- Data`. Do not collapse modules.
- Mokkery is test-only: `test-dependencies` plus `test-settings.kotlin.compilerPlugins` in the common template. Never in main code.

## Swift interop (no SKIE, no Swift export, no KMP-NativeCoroutines)

Not supported by Toolchain 0.13: SKIE, Swift export, a platform-scoped KMP-NativeCoroutines. iOS uses plain Kotlin/Native Objective-C interop:

- `import KotlinModules` (single framework containing `iosApp`, its dependency modules, and their transitive code). Qualify types as `KotlinModules.SampleItem` when they clash with Swift names. Old `import SharedCore` no longer exists.
- `suspend fun` is exposed as Swift `async throws` automatically. Cancelling the Swift task does not cancel the Kotlin coroutine.
- Top-level Kotlin functions are static methods on a generated `<FileName>Kt` class: `SampleRepositoryFactoryKt.iosSampleRepository()`, `SampleRepositoryExtensionsKt.loadItemsOrThrow(repository)`. Prefer `*OrThrow` Kotlin wrappers that turn `Result` into exceptions.
- Kotlin enums and sealed classes are Swift classes, not Swift enums: `switch` needs a `default:`, and subtypes need a cast (`as! Foo.Bar`). For sealed types add a Kotlin-side `kind` enum.
- Flows: expose a Kotlin helper that collects on `Dispatchers.Main` and returns a cancellable subscription, and wrap it in an `AsyncStream` in Swift. Generic ObjC classes need `T: AnyObject`, and extensions cannot use the generic parameter, so use a free function.
- Only add these helpers when a screen needs flows; the sample iOS app uses suspend functions only.

## iOS app and Xcode project

- `iosApp/module.yaml` is `product: ios/app` with `dependencies: [//shared/umbrella-core]`. Swift sources, `Assets.xcassets`, `Localizable.xcstrings`, `Info.plist` live in `iosApp/src`. The Xcode project is `iosApp/module.xcodeproj` (targets `app` and `appTests`; test scheme `iosApp`), checked into git. The Toolchain regenerates `app.xcscheme` on every build and discards edits to it, so the test action lives in the separate `iosApp.xcscheme` (do not delete it); always pass `-scheme iosApp` to `xcodebuild test`.
- The project uses a file-system-synchronized folder, so new Swift files in `iosApp/src` are picked up automatically; `sync-pbxproj.rb` is no longer needed.
- Hand-maintained settings in `module.xcodeproj/project.pbxproj`: `KOTLIN_CLI_WRAPPER_PATH = ../kotlin`, `PRODUCT_BUNDLE_IDENTIFIER = com.dream.template.ios`, `IPHONEOS_DEPLOYMENT_TARGET = 17.0`. The Toolchain does not set a deployment target; without one the app only installs on the newest iOS. If the project is regenerated, re-apply these. `CFBundleDisplayName` is in `iosApp/src/Info.plist`.
- Removing a Swift package dependency leaves stale references; delete `module.xcodeproj` and re-apply the settings above.
- Build: `./kotlin build -m iosApp --platform=iosSimulatorArm64`. App bundle: `build/tasks/_iosApp_buildIosAppIosSimulatorArm64Debug/bin/Debug-iphonesimulator/iosApp.app`.
- Install/launch: `xcrun simctl install <udid> <app>` then `xcrun simctl launch <udid> com.dream.template.ios`.
- Not supported by Toolchain 0.13: the new Xcode 27 JSON project format (`project.xcproj`). Keep `project.pbxproj`. Convertible later with `xcodebuild -convert-project "Xcode Project" -project module.xcodeproj`.
- The Toolchain requires exactly one target WITH the Kotlin integration script phase (`app`). A hosted unit-test target without that phase (`appTests`, bundle id `com.dream.template.ios.tests`) is accepted. Swift tests live in `iosApp/test/*.swift` and use `@testable import app`; run them with `xcodebuild test` (see verification-commands.md). `./kotlin test` runs only Kotlin tests.
- Caveat: `xcodebuild -convert-project` rewrites the `Build Kotlin` phase `shellScript` from a single string into a multi-line list, after which the Toolchain rejects the project ("must contain a single target with the Kotlin Toolchain integration"). Keep it exactly the single string `"# !KOTLIN INTEGRATION STEP!\n# This script is managed by the Kotlin Toolchain, do not edit manually!\n\"${KOTLIN_CLI_WRAPPER_PATH}\" tool xcode-integration\n";`.

## Android app

- `androidApp/module.yaml` (`product: android/app`), `src/AndroidManifest.xml`, `src/com/dream/template/android/MainActivity.kt`, `res/`. Namespace and applicationId `com.dream.template.android`.
- Build: `./kotlin build -m androidApp --platform=android`. APK: `build/tasks/_androidApp_buildAndroidDebug/gradle-project-debug.apk`.
- Install/launch: `adb install -r <apk>` then `adb shell am start -n com.dream.template.android/.MainActivity`.

## Gotchas

- Room schemas export to `<module>/schemas` via `ksp.processorOptions`.
- Compose resources: `settings.compose.resources.packageName` and `exposedAccessors: true`; keep the package equal to the old one so imports (`com.dream.template.shared.core.strings.Res`) do not change.
- `compilerPlugins` cannot be `@platform` scoped; KSP processors go in unqualified `settings.kotlin.ksp.processors`.
- Alpha tool: if something is unsupported, stop and report; do not work around it with Gradle.
