# Maestro Smoke Checklist Template

Use this when generating or replacing the first real feature.

## App Identity

- Android app id: `<applicationId>`
- iOS app id: `<bundle id or simulator app id>`
- Feature: `<feature-name>`

## Android Smoke Flow

- [ ] Replace `maestro/android/smoke.yaml` `appId` with the generated Android app id.
- [ ] Replace starter text assertions with feature-specific visible text from shared string resources.
- [ ] Add at least one primary user journey from the feature contract.
- [ ] Run `maestro test maestro/android/smoke.yaml` after Android install and launch are healthy.

## iOS Smoke Flow

- [ ] Replace `maestro/ios/smoke.yaml` `appId` with the generated iOS app id.
- [ ] Replace starter text assertions with feature-specific visible text from shared string resources.
- [ ] Add at least one primary user journey from the feature contract where reliable on simulator.
- [ ] Run `maestro test maestro/ios/smoke.yaml` after iOS install and launch are healthy.

## Reporting

- [ ] Record smoke results in `.mvp/verification-report.md` as `passed`, `blocked by environment`, `not run`, or `not applicable`.
