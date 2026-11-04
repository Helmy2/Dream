# 35. iOS Project Structure Practices

Dream's iOS host is native SwiftUI only (`iosApp`), and it should stay structurally obvious.

### 35.1 Keep the native SwiftUI host organized by feature bucket with layer sub-folders

The native SwiftUI variant should group source files by feature first, then by technical role:

- `iosApp/src/Core` for bridge code and app-level dependency assembly
- `iosApp/src/Sample/Models` for feature-specific Swift models and form state
- `iosApp/src/Sample/ViewModels` for feature-specific `@Observable` state holders
- `iosApp/src/Sample/Views` for feature-specific SwiftUI screens
- `iosApp/module.xcodeproj` and `iosApp/module.yaml` for project identity and build configuration

This mirrors how the KMP side organizes `feature/` modules: feature-first, layer inside.

Best practice:

- group files by feature bucket (`Sample/`, `Search/`, `Settings/`, etc.) instead of putting every model file in a flat top-level `Models/` folder
- keep `Core/` for truly shared infrastructure only
- avoid mixing bridge code into views or form state into the bridge layer
- keep the host app easy to scan for both humans and retrieval-oriented agents

### 35.2 No Compose iOS host

Compose is Android-only on the Kotlin Toolchain setup; there is no Compose iOS variant.

Best practice:

- keep `iOSApp.swift` as the app entry point only
- build all iOS UI in SwiftUI under `iosApp/src`
- keep Compose-specific code in Android-only modules

### 35.3 Group by feature bucket, then by layer

Best practice:

- use feature buckets (`Sample/`, etc.) with `Models/`, `ViewModels/`, `Views/` sub-folders inside each bucket
- keep `Core/` at the top level for cross-feature bridge and dependency assembly code
- Swift unit tests live in `iosApp/test/` (part of the hosted `appTests` target; tests use `@testable import app`); see file 38
- keep identity values in `module.xcodeproj` and `Info.plist` so identity changes are explicit during project instantiation

### 35.4 Keep sample host copy obviously replaceable

Template starter text should explain the structure without pretending to be final product copy.

Best practice:

- keep packaged sample titles and messages clearly starter-oriented
- remove or rename sample-facing text during project instantiation
- do not let template branding or sample workflow names survive into the generated product unless explicitly retained
