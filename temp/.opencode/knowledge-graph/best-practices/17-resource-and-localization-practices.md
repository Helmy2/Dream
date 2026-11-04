# 17. Resource And Localization Practices

A strong resource rule is:

- all shared user-visible strings come from the dedicated `shared/core-strings` Compose Resources module

### 17.1 Single source of truth for strings

Best practice to copy directly:

- keep one shared strings source for shared UI layers
- do not create random feature-local string stores unless architecture truly requires it
- export the generated `Res` class publicly when another module consumes those resources (`settings.compose.resources.exposedAccessors: true`)
- Compose resources live in `composeResources/` at the module root and are Android-only; keep `settings.compose.resources.packageName` unchanged so `Res` imports stay stable, and run `./kotlin clean` after changing it
- iOS strings live in `Localizable.xcstrings` (see file 39)

### 17.2 No hardcoded strings in composables

This rule is especially important in multiplatform code.

Why it helps:

- localization remains feasible
- copy changes are centralized
- accessibility labels are treated as product text, not implementation detail

### 17.3 Strings-first workflow

Before writing any Compose UI for a feature, define ALL user-visible strings in `shared/core-strings/composeResources/values/strings.xml` first. Use a naming convention of `<feature>_<purpose>` (e.g., `expense_title`, `expense_add_button`, `expense_error_message`).

This ensures:
- UI code imports strings, not invents them
- string review can happen independently from UI review
- no UI text is "temporary" or forgotten

### 17.4 Standardize the `Res` import path

The project documents the correct `Res` import and avoids alias confusion. For the template strings module, use `com.dream.template.shared.core.strings.Res`.

Best practice:

- define one canonical resource import path and keep it consistent across modules
