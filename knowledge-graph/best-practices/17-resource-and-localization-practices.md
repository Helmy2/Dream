# 17. Resource And Localization Practices

A strong resource rule is:

- all user-visible strings come from `shared/designsystem` resources

### 16.1 Single source of truth for strings

Best practice to copy directly:

- keep one shared strings source for shared UI layers
- do not create random feature-local string stores unless architecture truly requires it

### 16.2 No hardcoded strings in composables

This rule is especially important in multiplatform code.

Why it helps:

- localization remains feasible
- copy changes are centralized
- accessibility labels are treated as product text, not implementation detail

### 16.3 Standardize the `Res` import path

The project documents the correct `Res` import and avoids alias confusion.

Best practice:

- define one canonical resource import path and keep it consistent across modules
