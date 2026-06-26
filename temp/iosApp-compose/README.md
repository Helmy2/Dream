# iOS Compose Variant

This packaged iOS host is intentionally thin. It embeds the exported `Shared` framework from `shared/umbrella-ui` and lets Compose Multiplatform render the full UI.

Structure:

- `iosApp/iOSApp.swift`: app entry point
- `iosApp/ContentView.swift`: `UIViewControllerRepresentable` wrapper for `MainViewController()`
- `Configuration/Config.xcconfig`: bundle identity and visible app name

Build notes:

- Xcode runs `:shared:umbrella-ui:embedAndSignAppleFrameworkForXcode`
- `FRAMEWORK_SEARCH_PATHS` points at `shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`
- Keep the native shell minimal and move product behavior into shared Kotlin modules
