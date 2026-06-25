# 15. Navigation Practices

A centralized Navigation 3 structure is one of the clearest reusable patterns in this style of app.

### 15.1 Central app shell

A root app shell acts as the single structural entry point for shared UI.

Best practice:

- keep app-wide navigation, theme application, and safe-area handling at the shell level
- keep feature screens unaware of the full routing graph when possible

### 15.2 Navigation 3 typed routes

Routes are modeled as typed values implementing `androidx.navigation3.runtime.NavKey`, such as `SampleRoute : NavKey`.

Concrete route types belong to the feature `api` module. Other modules that need to navigate to this feature depend on `feature/<name>/api` and reference the route type there. Shared navigation depends only on `NavKey`, not on a shared sealed hierarchy containing feature-specific destinations. Do not put feature-specific routes in shared core unless they are genuinely shared across multiple features.

Why this is good:

- callers depend on the public API surface, not the implementation details
- `impl` can be replaced or refactored without changing callers
- exhaustive route handling
- less stringly-typed navigation
- a direct path to serializable route keys when destinations need arguments or saved back stacks

### 15.3 Navigator owns the back stack

Shared navigation should expose a small `Navigator` with a mutable back stack and intent-level operations such as `goTo()` and `goBack()`.

Best practice:

- keep the back stack in a stable shared navigation owner
- render it with Navigation 3 `NavDisplay`
- make back behavior a product rule instead of an accidental side effect of navigation defaults

### 15.4 Koin navigation entry providers live in impl

The feature `impl` module installs Koin Navigation 3 entry providers using `navigation<Route> { FeatureScreen() }`.

Best practice:

- keep route-to-screen wiring in the feature `impl` Koin module, not in shared core or the app shell
- keep feature `api` free of Koin and Compose dependencies
- retrieve DI-managed screen dependencies at the navigation entry boundary inside `impl`
- the app shell composition root (`shared/umbrella-ui`) imports feature `impl` Koin modules so their navigation installers are registered before `NavDisplay` renders

### 15.5 Dependency direction for navigation

```
other modules  -->  feature/<name>/api  (route types only)
feature/<name>/impl  -->  feature/<name>/api  (installs navigation entry)
shared/umbrella-ui  -->  feature/<name>/impl  (aggregates Koin navigation installers)
shared/navigation  -->  NavKey  (no feature-specific types)
```

### 15.6 Differentiate top-level tabs from push destinations

A well-structured app should clearly separate:

- top-level routes with persistent navigation chrome
- push destinations without persistent navigation chrome

This app-shell rule improves consistency as the graph grows.
