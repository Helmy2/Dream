# Dream Verification Commands

Run all commands from project root.

## Environment Preflights

```bash
# Git readiness
git status
git diff
git log --oneline -10

# Android SDK readiness
adb devices
```

## Gradle Tests

```bash
# All tests
./gradlew allTests

# Feature-specific tests
./gradlew :feature:<name>:domain:allTests
./gradlew :feature:<name>:data:allTests
./gradlew :feature:<name>:impl:allTests

# Shared module tests
./gradlew :shared:core:domain:allTests
./gradlew :shared:core:data:allTests
./gradlew :shared:core:presentation:allTests
./gradlew :shared:navigation:allTests
```

## Android Build

```bash
# Debug build
./gradlew :androidApp:assembleDebug

# Install on emulator
adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk

# Launch app
adb shell am start -n <package>/<main-activity>

# Uninstall (before fresh install)
adb uninstall <package>
```

## iOS Build

```bash
# Build for simulator
xcodebuild -project iosApp-swiftui/iosApp.xcodeproj \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro,OS=26.2' \
  build

# Build and test
xcodebuild -project iosApp-swiftui/iosApp.xcodeproj \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro,OS=26.2' \
  test

# Install on simulator
xcrun simctl install <device-id> <path-to-app>

# Launch on simulator
xcrun simctl launch <device-id> <bundle-id>

# Uninstall (before fresh install)
xcrun simctl uninstall <device-id> <bundle-id>
```

## UI Inspection (via mobile-mcp)

```
mobile_take_screenshot — capture current screen
mobile_list_elements_on_screen — inspect element tree
```

## Git Operations (after verification)

```bash
# Stage intended files
git add <files>

# Create commit
git commit -m "Add <feature> feature"
```
