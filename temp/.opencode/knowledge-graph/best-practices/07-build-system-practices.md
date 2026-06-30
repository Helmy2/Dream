# 7. Build System Practices

This approach uses convention plugins through `build-logic/` and includes that build in `settings.gradle.kts`.

Key practice:

- centralize plugin and toolchain setup
- keep feature dependencies explicit inside each module

### 7.1 Convention plugin philosophy

The convention plugins are most useful when they stay narrow:

- `app.kmp`: base KMP setup
- `app.kmp.compose`: Compose-specific KMP setup
- `app.kmp.koin`: Koin shorthand

Best practice to copy:

- convention plugins should configure build behavior, not hide all dependencies
- module `build.gradle.kts` files should still declare their real library dependencies clearly

This makes module contracts readable during code review.

### 7.2 Typesafe project accessors

`settings.gradle.kts` enables `TYPESAFE_PROJECT_ACCESSORS`.

Why it is good:

- reduces string-based project references
- improves autocompletion
- makes refactors safer

### 7.3 Module build files should stay small

Good example from this project:

- each feature build file mostly declares plugins, namespace, framework config, and direct dependencies

That keeps module intent obvious.
