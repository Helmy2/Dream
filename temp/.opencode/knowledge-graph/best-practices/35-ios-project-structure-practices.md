# 35. iOS Project Structure Practices

Dream packages two different iOS host strategies, and each should stay structurally obvious.

### 35.1 Keep the native SwiftUI host organized by feature bucket with layer sub-folders

The native SwiftUI variant should group source files by feature first, then by technical role:

- `iosApp/Core` for bridge code and app-level dependency assembly
- `iosApp/Sample/Models` for feature-specific Swift models and form state
- `iosApp/Sample/ViewModels` for feature-specific `@Observable` state holders
- `iosApp/Sample/Views` for feature-specific SwiftUI screens
- `Configuration` for `.xcconfig` identity and build configuration

This mirrors how the KMP side organizes `feature/` modules: feature-first, layer inside.

Best practice:

- group files by feature bucket (`Sample/`, `Search/`, `Settings/`, etc.) instead of putting every model file in a flat top-level `Models/` folder
- keep `Core/` for truly shared infrastructure only
- avoid mixing bridge code into views or form state into the bridge layer
- keep the host app easy to scan for both humans and retrieval-oriented agents

### 35.2 Keep the Compose iOS host intentionally tiny

The Compose Multiplatform iOS variant should remain a thin native shell.

Best practice:

- keep `iOSApp.swift` as the app entry point only
- keep `ContentView.swift` focused on embedding the shared `UIViewController`
- avoid rebuilding feature-level native structure inside the Compose variant when the UI is owned by shared Kotlin code

### 35.3 Group by feature bucket, then by layer

Best practice:

- use feature buckets (`Sample/`, etc.) with `Models/`, `ViewModels/`, `Views/` sub-folders inside each bucket
- keep `Core/` at the top level for cross-feature bridge and dependency assembly code
- keep test files in a sibling `iosAppTests/` directory with matching naming
- keep configuration files outside the app source folder so identity changes are explicit during project instantiation

### 35.4 Keep sample host copy obviously replaceable

Template starter text should explain the structure without pretending to be final product copy.

Best practice:

- keep packaged sample titles and messages clearly starter-oriented
- remove or rename sample-facing text during project instantiation
- do not let template branding or sample workflow names survive into the generated product unless explicitly retained
