# 17. Resource And Localization Practices

A strong resource rule is:

- all shared user-visible strings come from the dedicated `shared/core/strings` Compose Resources module

### 16.1 Single source of truth for strings

Best practice to copy directly:

- keep one shared strings source for shared UI layers
- do not create random feature-local string stores unless architecture truly requires it
- export the generated `Res` class publicly when another module consumes those resources

### 16.2 No hardcoded strings in composables

This rule is especially important in multiplatform code.

Why it helps:

- localization remains feasible
- copy changes are centralized
- accessibility labels are treated as product text, not implementation detail

### 16.3 Standardize the `Res` import path

The project documents the correct `Res` import and avoids alias confusion. For the template strings module, use `com.dream.template.shared.core.strings.Res`.

Best practice:

- define one canonical resource import path and keep it consistent across modules
