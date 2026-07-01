# Sample Cleanup Checklist

Run this checklist after the first real feature replaces the starter sample.

## Files to Remove

- [ ] `feature/sample/` directory (run `./scripts/remove-sample.sh`)

## Files to Update

### Settings/Configuration
- [ ] `androidApp/src/main/AndroidManifest.xml` — remove sample activity references
- [ ] `androidApp/src/main/java/*/MainActivity.kt` — remove sample navigation entries
- [ ] `shared/umbrella-ui/src/commonMain/kotlin/*/UiModule.kt` — remove sample Koin module import
- [ ] `shared/umbrella-core/src/commonMain/kotlin/*/CoreModule.kt` — remove sample core imports

### Navigation
- [ ] `shared/navigation/src/commonMain/kotlin/*/AppNavigation.kt` — remove sample routes
- [ ] Any file referencing `SampleRoute`, `SampleListRoute`, `SampleDetailRoute`

### Strings
- [ ] `shared/core/strings/src/commonMain/composeResources/values/strings.xml` — remove `sample_*` keys
- [ ] `iosApp-swiftui/iosApp/Localizable.xcstrings` — remove `sample_*` entries

### Database
- [ ] `shared/core/data/src/commonMain/kotlin/*/database/` — remove sample DAO references
- [ ] Room schema exports — remove sample entity schemas

### Documentation
- [ ] `README.md` — update to reflect real feature
- [ ] `AGENTS.md` — update module references
- [ ] `.opencode/guidance/reference/verification-commands.md` — update example paths

### Build Files
- [ ] `settings.gradle.kts` — remove `include(":feature:sample:...")` entries
- [ ] `shared/umbrella-ui/build.gradle.kts` — remove sample module dependencies

## Verification

After cleanup:
- [ ] `./gradlew :androidApp:assembleDebug` succeeds
- [ ] `./gradlew allTests` passes
- [ ] No references to `sample` in grep across project (excluding `.git`, `build/`, `node_modules/`)
