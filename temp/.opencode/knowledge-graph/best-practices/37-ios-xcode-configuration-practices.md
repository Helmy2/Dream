# 37. iOS Xcode Configuration Practices

The packaged iOS hosts should work cleanly with Xcode while remaining deterministic for generated projects.

### 37.1 Use Gradle-driven framework embedding from Xcode

Best practice:

- add a shell build phase that runs the appropriate `embedAndSignAppleFrameworkForXcode` task
- use `:shared:umbrella-core:embedAndSignAppleFrameworkForXcode` for native SwiftUI hosts
- use `:shared:umbrella-ui:embedAndSignAppleFrameworkForXcode` for Compose iOS hosts
- do not duplicate shell phases that invoke the same Gradle task

### 37.2 Keep framework search paths deterministic

Best practice:

- point `FRAMEWORK_SEARCH_PATHS` at `$(SRCROOT)/../shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`
- avoid duplicate or newline-corrupted search path entries in the pbxproj
- verify the resolved framework directory when simulator or device builds behave differently

### 37.3 Keep product identity in xcconfig, not scattered through the project file

Best practice:

- store `TEAM_ID`, `BUNDLE_ID`, and `APP_NAME` in `Configuration/Config.xcconfig`
- let project instantiation rewrite those values instead of editing many pbxproj fields directly
- keep visible app identity and bundle identity easy to audit during template reuse

### 37.4 Prefer shared schemes over user schemes in the packaged template

Best practice:

- commit shared schemes under `xcshareddata/xcschemes`
- do not depend on user-specific `xcuserdata` schemes in the packaged template
- remove transient Xcode user state files from the shipped project structure
