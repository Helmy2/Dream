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

## Kotlin Toolchain Tests

```bash
# All tests
./kotlin test --platform=android
./kotlin test --platform=iosSimulatorArm64

# Feature-specific tests
./kotlin test -m <name>-domain --platform=android
./kotlin test -m <name>-data --platform=android
./kotlin test -m <name>-impl --platform=android

# Shared module tests (module name = directory name)
./kotlin test -m core-domain --platform=android
./kotlin test -m core-data --platform=android
./kotlin test -m core-presentation --platform=android
./kotlin test -m navigation --platform=android
```

## Android Build

```bash
# Debug build
./kotlin build -m androidApp --platform=android

# Install on emulator
adb install -r build/tasks/_androidApp_buildAndroidDebug/gradle-project-debug.apk

# Launch app
adb shell am start -n com.dream.template.android/.MainActivity

# Uninstall (before fresh install)
adb uninstall <package>
```

## iOS Build

```bash
# Build for simulator
./kotlin build -m iosApp --platform=iosSimulatorArm64

# Install on simulator (app bundle path from the Kotlin Toolchain reference)
xcrun simctl install <device-id> build/tasks/_iosApp_buildIosAppIosSimulatorArm64Debug/bin/Debug-iphonesimulator/iosApp.app

# Launch on simulator
xcrun simctl launch <device-id> com.dream.template.ios

# Uninstall (before fresh install)
xcrun simctl uninstall <device-id> com.dream.template.ios
```

Swift unit tests (hosted `appTests` target, sources in `iosApp/test/`; also triggers the Kotlin Toolchain integration script, so no prior `./kotlin build` is needed). `./kotlin test` runs only Kotlin tests, never Swift tests:

```bash
xcodebuild test -project iosApp/module.xcodeproj -scheme app -destination 'platform=iOS Simulator,name=iPhone 17 Pro' CODE_SIGNING_ALLOWED=NO
# or: -destination "id=<simulator-udid>"
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
