# 34. Maestro Smoke Verification Practices

Maestro smoke flows should prove high-value user behavior without depending on brittle UI internals.

### 34.0 Feature-level coverage

Smoke coverage is owned by features, not by one global app file.

Reusable principle:

- every user-visible generated or touched feature should have an Android smoke flow when Android supports that feature
- every user-visible generated or touched feature should have an iOS smoke flow when iOS supports that feature and the flow is reliable on simulator
- store feature flows under `maestro/features/<feature-name>/android.yaml` and `maestro/features/<feature-name>/ios.yaml`
- app-level startup smoke checks are optional and do not replace feature-level coverage
- do not rely on one global smoke file to cover multiple features

### 34.1 Runtime preconditions

Maestro is a smoke validator, not the platform install step.

Reusable principle:

- boot or open the target Android emulator, Android device, or iOS simulator first
- install the app with platform tooling before `maestro test ...`
- confirm the app launches before classifying Maestro failures as product defects
- start every smoke flow with `clearState` before `launchApp` unless the contract explicitly tests persisted state across launches
- if the runtime is unavailable or the app is not installed, mark the smoke flow `blocked by environment` or `not run`

### 34.2 Editable form flows

For single-line text replacement on iOS, this pattern is preferred:

```yaml
- tapOn: "Title"
- eraseText
- inputText: "Updated title"
- pressKey: Enter
- tapOn: "Save changes"
```

Reusable principle:

- prefer `pressKey: Enter` when `hideKeyboard` is unreliable
- verify the completed user outcome after save
- avoid direct keyboard-state assertions as the only proof

### 34.3 Detail-screen assertions

Detail screens often render labels and values as one accessibility node.

Reusable principle:

- assert the destination title when visible
- assert the item title or core content
- assert formatted amount or primary value when stable
- prefer full rendered-row regex selectors such as `Occurred: .*` or `Created: .*`
- avoid standalone label-only assertions when labels and values render together

### 34.4 Platform-specific brittleness

Smoke flows should tolerate known platform differences without becoming meaningless.

Reusable principle:

- do not assume Android exposes the app/window title as visible screen text
- on iOS simulator, prefer updated summary/list/detail assertions over exact currency-node visibility when rendering is inconsistent
- distinguish a product failure from a device-driver, simulator, or Maestro issue

### 34.5 Environment fallback reporting

Environment blockers must be precise.

Reusable principle:

- no attached Android device or emulator is `blocked by environment`
- an iOS simulator without the app installed is `not run` or `blocked by environment`, not a product failure
- Maestro `launchApp` failure on physical Android with successful `adb install` and `adb shell am start` is a Maestro/device-driver blocker
- emulator-backed Maestro should be tried when feasible after physical-device driver issues
- iOS simulator keyboard issues should be worked around with reliable input patterns before declaring the product blocked
- Gradle build success does not replace smoke validation
