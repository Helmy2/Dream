# Sample Cleanup Checklist

Run this checklist after the first real feature replaces the starter sample.

## Files to Remove

- [ ] `feature/sample/` directory (run `./scripts/remove-sample.sh`)

## Files to Update

### Settings/Configuration
- [ ] `androidApp/src/main/AndroidManifest.xml` — remove sample activity references
- [ ] `androidApp/src/main/java/*/MainActivity.kt` — remove sample navigation entries
- [ ] `shared/umbrella-ui/src/*/UiModule.kt` — remove sample Koin module import
- [ ] `shared/umbrella-core/src/*/CoreModule.kt` — remove sample core imports

### Navigation
- [ ] `shared/navigation/src/*/AppNavigation.kt` — remove sample routes
- [ ] Any file referencing `SampleRoute`, `SampleListRoute`, `SampleDetailRoute`

### Strings
- [ ] `shared/core-strings/composeResources/values/strings.xml` — remove `sample_*` keys
- [ ] `iosApp/src/Localizable.xcstrings` — remove `sample_*` entries

### Database
- [ ] `shared/core-data/src/*/database/` — remove sample DAO references
- [ ] Room schema exports — remove sample entity schemas

### Documentation
- [ ] `README.md` — update to reflect real feature
- [ ] `AGENTS.md` — update module references
- [ ] `.opencode/guidance/reference/verification-commands.md` — update example paths

### Build Files
- [ ] `project.yaml` — remove `feature/sample-*` module entries
- [ ] `shared/umbrella-ui/module.yaml` and `shared/umbrella-core/module.yaml` — remove `//feature/sample-*` dependencies

## Verification

After cleanup:
- [ ] `./kotlin build -m androidApp --platform=android` succeeds
- [ ] `./kotlin test --platform=android` + `./kotlin test --platform=iosSimulatorArm64` passes
- [ ] No references to `sample` in grep across project (excluding `.git`, `build/`, `node_modules/`)
