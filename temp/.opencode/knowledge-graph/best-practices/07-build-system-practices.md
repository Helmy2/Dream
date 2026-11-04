# 7. Build System Practices

This approach uses the JetBrains Kotlin Toolchain (`./kotlin` CLI, YAML configuration, v0.13, Alpha). There is no Gradle: no `build.gradle.kts`, no `settings.gradle.kts`, no `build-logic/`, and no convention plugins. `project.yaml` lists the modules, each module has a `module.yaml`, and shared setup lives in templates.

Key practice:

- centralize plugin and toolchain setup in templates
- keep feature dependencies explicit inside each module's `module.yaml`

### 7.1 Templates replace convention plugins

Shared setup lives in `templates/*.module-template.yaml` and is pulled in with `apply:`.

- `common.module-template.yaml`: Kotlin version, `-Xexpect-actual-classes`, serialization, JVM release, Android SDK levels, coroutines-android, and Mokkery for tests only
- `compose.module-template.yaml`: Compose, lifecycle-runtime-compose, icons, ui-tooling (Android-only UI modules)
- `feature-impl.module-template.yaml`: compose plus turbine for tests

Best practice to copy:

- templates should configure build behavior, not hide all dependencies
- each `module.yaml` should still declare its real dependencies clearly
- keep templates narrow; do not build one giant template that hides the whole build

This makes module contracts readable during code review.

### 7.2 Module and project files

`project.yaml` is the list of modules. A new module needs a directory, a `module.yaml`, and an entry in `project.yaml`.

```yaml
product:
  type: kmp/lib                       # or android/app, ios/app
  platforms: [android, iosArm64, iosSimulatorArm64]   # [android] for Android-only UI modules

apply:
  - //templates/common.module-template.yaml

dependencies:                         # also dependencies@android, dependencies@ios, test-dependencies
  - //shared/core-domain: exported    # `exported` replaces Gradle `api`; default is implementation
  - $libs.kotlinx.coroutines.core

settings:
  android:
    namespace: com.dream.template.<path>
```

Why it is good:

- module references use `//shared/core-domain` style paths, not Gradle project accessors
- `exported` makes transitive visibility explicit
- `./kotlin show settings -m <module>` prints the resolved configuration

### 7.3 Unique module names

The module name is the directory name and must be unique across the project. Leaf directories are therefore `core-domain`, `core-data`, `sample-data`, `sample-impl`, and so on; never two directories both called `domain`. Kotlin package names are unaffected by the directory names.

### 7.4 Version catalog

`libs.versions.toml` at the project root contains `[versions]` and `[libraries]` only. Plugins are not declared there. Modules reference entries as `$libs.<name>`.

### 7.5 Source layout

| Purpose | Directory |
|---------|-----------|
| common main sources | `src` |
| Android-only sources | `src@android` |
| iOS-only sources | `src@ios` |
| common tests | `test` |
| Android host tests | `test@android` |
| Compose resources | `composeResources` (module root) |
| Android resources | `res` (module root) |
| Android app manifest | `src/AndroidManifest.xml` |

### 7.6 Compiler plugins, Koin, and Mokkery

- compiler plugins are declared under `settings.kotlin.compilerPlugins` and cannot be platform-scoped
- the Koin compiler plugin needs a unique `moduleId` per module (use the module name) to avoid duplicate generated hint classes
- KSP processors go in unqualified `settings.kotlin.ksp.processors`
- Mokkery is test-only: `test-dependencies` plus `test-settings.kotlin.compilerPlugins` in the common template; never apply it to main code

### 7.7 Module build files should stay small

Good example from this project:

- each `module.yaml` mostly declares product type, platforms, applied templates, namespace, and direct dependencies

That keeps module intent obvious.

### 7.8 Commands

- build: `./kotlin build`, `./kotlin build -m <module>`, `./kotlin build --platform=android|iosSimulatorArm64`
- test: `./kotlin test --platform=android`, then `./kotlin test --platform=iosSimulatorArm64`; one module with `-m <module>`
- inspect: `./kotlin show settings -m <module>`
- clean: `./kotlin clean`
- if something is unsupported by the Toolchain, stop and report; do not work around it with Gradle
