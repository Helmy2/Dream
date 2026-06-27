# Maestro Smoke Checklist Template

Use this when generating or replacing a real feature.

## App Identity

- Android app id: `<applicationId>`
- iOS app id: `<bundle id or simulator app id>`
- Feature: `<feature-name>`
- Android smoke file: `maestro/features/<feature-name>/android.yaml`
- iOS smoke file: `maestro/features/<feature-name>/ios.yaml`

## Feature Coverage Rule

- [ ] Create or update one feature-owned Android smoke flow for this feature when Android is supported.
- [ ] Create or update one feature-owned iOS smoke flow for this feature when iOS is supported.
- [ ] Do not rely on a single app-level smoke file to cover this feature.
- [ ] Keep app-level startup smoke checks optional; they do not replace feature-level smoke coverage.

## Android Smoke Flow

- [ ] Replace `maestro/features/<feature-name>/android.yaml` `appId` with the generated Android app id.
- [ ] Replace starter text assertions with feature-specific visible text from shared string resources.
- [ ] Add at least one primary user journey from the feature contract.
- [ ] Boot an Android emulator or connect a device, install the APK, and confirm it launches before running Maestro.
- [ ] Start the flow with `clearState` before `launchApp` unless the contract explicitly tests persisted state across app launches.
- [ ] For forms, assert the primary action is visible or reachable while the IME may be open, then verify success-state content after save.
- [ ] For detail screens, prefer destination title, item title, formatted amount, and full rendered-row regex selectors such as `Occurred: .*` instead of standalone labels.
- [ ] Do not require app/window title text to be visible on Android; it may not be exposed as a node even after launch succeeds.
- [ ] Run `maestro test maestro/features/<feature-name>/android.yaml` after Android install and launch are healthy.

## iOS Smoke Flow

- [ ] Replace `maestro/features/<feature-name>/ios.yaml` `appId` with the generated iOS app id.
- [ ] Replace starter text assertions with feature-specific visible text from shared string resources.
- [ ] Add at least one primary user journey from the feature contract where reliable on simulator.
- [ ] Boot the target iOS simulator, install the app, and confirm it launches before running Maestro.
- [ ] Start the flow with `clearState` before `launchApp` unless the contract explicitly tests persisted state across app launches.
- [ ] For single-line text replacement, prefer `tapOn`, `eraseText`, `inputText`, `pressKey: Enter`, then tap the save action.
- [ ] Avoid relying on `hideKeyboard` as the only path to reach a save action.
- [ ] Prefer updated summary/list/detail assertions over exact currency-node visibility when simulator rendering is inconsistent.
- [ ] Run `maestro test maestro/features/<feature-name>/ios.yaml` after iOS install and launch are healthy.

## Editable Form Snippets

Single-line iOS replacement fallback:

```yaml
- tapOn: "Title"
- eraseText
- inputText: "Updated title"
- pressKey: Enter
- tapOn: "Save changes"
```

Detail-screen rendered-row assertion examples:

```yaml
- assertVisible: "Expense Detail"
- assertVisible: "Groceries"
- assertVisible: "\\$42.50"
- assertVisible:
    text: "Occurred: .*"
- assertVisible:
    text: "Created: .*"
```

## MCP Deep Inspection

After Maestro flows complete, validate the rendered UI against the contract. Prefer mobile-mcp tools; fall back to mobilecli, then to Android CLI if MCP is unavailable:

- [ ] Use `mobile_take_screenshot` (MCP) or `mobilecli screenshot --device <id> -o <file>` (mobilecli) or `adb exec-out screencap -p` (CLI) to capture the current screen state.
- [ ] Use `mobile_list_elements_on_screen` (MCP) or `mobilecli dump ui --device <id>` (mobilecli) or `adb shell uiautomator dump` (CLI) to inspect the element tree and accessibility hierarchy.
- [ ] Verify contract-mandated UI elements appear in the element tree with matching accessibility labels.
- [ ] Cross-reference visible text content against `shared/core/strings` string resources.
- [ ] Verify that form save/output actions produce expected success-state content in the element tree.
- [ ] Include screenshot descriptions and element tree excerpts in `.features/verification-report.md`.
- [ ] If none of MCP, mobilecli, or device CLI are available, skip UI inspection and record it as `not run`.

## Environment Fallbacks

- [ ] If the emulator/simulator is not open or the app is not installed, mark the smoke flow `blocked by environment` or `not run`; do not classify it as a product-code failure.
- [ ] If no Android device or emulator is attached, mark Android runtime and smoke checks `blocked by environment` with the setup action needed.
- [ ] If Maestro fails at `launchApp` on a physical Android device but `adb install` and `adb shell am start` work, record a Maestro/device-driver blocker, try emulator-backed Maestro when feasible, and do not report physical-device Maestro as passed.
- [ ] If iOS keyboard behavior blocks direct access to the save action, use `pressKey: Enter` for single-line fields and verify the completed user outcome.

## Reporting

- [ ] Record smoke results in `.features/verification-report.md` as `passed`, `blocked by environment`, `not run`, or `not applicable`.
