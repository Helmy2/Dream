# local-crud wiring

Everything a new feature touches outside its own modules. Snippets are from the working sample; replace `Sample`/`sample` per `archetype.yaml` (`naming`). Order matters: modules first, then DI, then hosts.

The empty starter ships a placeholder home screen (`HomeRoute` and `placeholderHomeModule` in `shared/umbrella-ui`, an empty state in `ContentView.swift`) and calls `initKoin(appContext = applicationContext)`. The snippets below are the state after the first feature replaces them; the "First Feature" section of `guidance/new-feature-checklist.md` lists the removals. `apply-verbatim.sh` applies everything below unchanged to a scratch copy, which is how the archetype check works.

## 1. `project.yaml`

```yaml
  - feature/sample-domain
  - feature/sample-data
  - feature/sample-api
  - feature/sample-impl
```

Module names equal directory names and must be unique.

## 2. `shared/umbrella-core/module.yaml`

Export the data and domain modules so the iOS framework (`KotlinModules`) exposes them to Swift:

```yaml
dependencies:
  - //feature/sample-data: exported
  - //feature/sample-domain: exported
```

## 3. `shared/umbrella-ui/module.yaml`

```yaml
dependencies:
  - //feature/sample-data
  - //feature/sample-domain
  - //feature/sample-api: exported
  - //feature/sample-impl
```

## 4. `shared/umbrella-ui/src/**/di/UiModule.kt`

The start destination, the feature's data module, and its UI module are composed here. `coreModule` provides `TimeProvider`, which the data module needs.

```kotlin
val navigatorModule = module {
    single { Navigator(startDestination = SampleRoute) }
}

fun initKoin(
    sampleDatabaseFactory: SampleDatabaseFactory,
    appContext: Any? = null,
    appDeclaration: KoinAppDeclaration = {}
) {
    if (hasStartedKoin) return
    startKoin {
        appDeclaration()
        modules(
            coreModule(appContext),
            sampleDataModule(sampleDatabaseFactory),
            sampleUiModule,
            navigatorModule,
        )
    }
    hasStartedKoin = true
}
```

Imports: `...feature.sample.api.navigation.SampleRoute`, `...feature.sample.data.di.sampleDataModule`, `...feature.sample.data.local.SampleDatabaseFactory`, `...feature.sample.impl.di.sampleUiModule`.

## 5. `androidApp/src/**/MainActivity.kt`

```kotlin
initKoin(
    sampleDatabaseFactory = AndroidSampleDatabaseFactory(applicationContext),
    appContext = applicationContext,
)
```

`AndroidSampleDatabaseFactory` lives in `feature/sample-data` (`src@android`). The Android host reaches it through `umbrella-core`, which exports `sample-data`.

## 6. iOS

`iosApp/src/ContentView.swift`:

```swift
struct ContentView: View {
    var body: some View { SampleListView() }
}
```

`iosApp/src/Core/AppDependencies.swift` injects the bridge (view models default to `AppDependencies.shared.bridge`; tests pass a mock):

```swift
final class AppDependencies {
    static let shared = AppDependencies()
    let bridge: any SampleRepositoryBridge
    private init() { bridge = SampleBridge() }
}
```

Copy `ios/src/Sample` to `iosApp/src/Sample` and `ios/test/*.swift` to `iosApp/test`. Do not copy a second `BridgeError`; it already exists in `iosApp/src/Core`. After any Toolchain build that touched iOS sources, run `.opencode/scripts/fix-ios-project.sh` before `xcodebuild test`.

## 7. Strings

- Android: merge `strings/strings.xml` into `shared/core-strings/composeResources/values/strings.xml`.
- iOS: merge `strings/Localizable.xcstrings` into `iosApp/src/Localizable.xcstrings`.
- Keep keys identical on both platforms.

## 8. Maestro

Copy `maestro/features/sample` to `maestro/features/<feature>` and update `appId`, the seed text and the labels the flows tap.
