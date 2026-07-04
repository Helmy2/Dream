---
description: Run end-to-end Maestro smoke flows for features
agent: dream-general
---

Use Dream's Maestro end-to-end workflow to run feature-owned smoke flows.

Treat `$ARGUMENTS` as the feature name(s) to test, or leave blank to run all available flows.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `.opencode/guidance/templates/maestro-smoke-checklist-template.md`
- `.opencode/knowledge-graph/best-practices/34-maestro-smoke-verification-practices.md`
- `AGENTS.md`
- relevant feature contracts for features being tested

Then:

### Preflights

- verify the project is a git repository and `git status`, `git diff`, and `git log --oneline -10` work
- verify Maestro is installed (`maestro --version`), or record a precise Maestro environment blocker
- verify at least one Android emulator is booted (`adb devices`) or one iOS simulator is booted (`xcrun simctl list devices booted`)
- list available Maestro flows under `maestro/features/`

### Flow Generation (if needed)

If a feature has no Maestro flow yet:
- read the feature contract to identify key user journeys
- create `maestro/features/<feature-name>/android.yaml` with `clearState` before `launchApp`
- create `maestro/features/<feature-name>/ios.yaml` when the feature is supported on iOS
- use stable selectors (content-desc, accessibility labels, text) — avoid brittle index-based selectors

### Running Flows

For each feature being tested:

1. **Android**: After APK is installed and app launches cleanly:
   ```bash
   maestro test maestro/features/<feature-name>/android.yaml
   ```
2. **iOS**: After .app is installed and app launches cleanly:
   ```bash
   maestro test maestro/features/<feature-name>/ios.yaml
   ```

Run Maestro only after platform launch is verified. Start flows with `clearState` before `launchApp` unless the contract explicitly tests persisted state across launches.

### Reporting

- record Maestro status in `.features/verification-report.md`
- status buckets: `passed`, `blocked by environment`, `not run`, `not applicable`
- if Maestro `launchApp` fails on a physical Android device but `adb install` and `adb shell am start` work, record a Maestro/device-driver blocker and try emulator-backed Maestro when feasible

### Output

Produce a summary:
- which features were tested
- per-platform status (passed / blocked / not run)
- any failures or blockers
- recommendations for new flows if gaps were identified
