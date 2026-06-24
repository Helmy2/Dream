# 15. Navigation Practices

A centralized Navigation 3 structure is one of the clearest reusable patterns in this style of app.

### 15.1 Central app shell

A root app shell acts as the single structural entry point for shared UI.

Best practice:

- keep app-wide navigation, theme application, and safe-area handling at the shell level
- keep feature screens unaware of the full routing graph when possible

### 15.2 Navigation 3 typed routes

Routes are modeled as typed values implementing `androidx.navigation3.runtime.NavKey`, such as `SampleRoute : NavKey`.

Concrete route types belong to the feature presentation module that owns the destination. Shared navigation should depend on `NavKey`, not a shared sealed hierarchy containing feature-specific destinations. Put a route in shared core only when there is a concrete cross-feature reason.

Why this is good:

- exhaustive route handling
- less stringly-typed navigation
- a direct path to serializable route keys when destinations need arguments or saved back stacks
- clearer feature ownership without leaking feature-specific destinations into shared core

### 15.3 Navigator owns the back stack

Shared navigation should expose a small `Navigator` with a mutable back stack and intent-level operations such as `goTo()` and `goBack()`.

Best practice:

- keep the back stack in a stable shared navigation owner
- render it with Navigation 3 `NavDisplay`
- make back behavior a product rule instead of an accidental side effect of navigation defaults

### 15.4 Koin owns entry providers

Composition roots or feature presentation modules should register Koin Navigation 3 entries with `navigation<Route> { ... }`.

Best practice:

- keep route-to-screen wiring in Compose/UI modules, not non-Compose core export modules
- keep feature modules free of global route dependencies unless the feature owns the destination or nested graph
- retrieve DI-managed screen dependencies at the navigation entry boundary

### 15.5 Differentiate top-level tabs from push destinations

A well-structured app should clearly separate:

- top-level routes with persistent navigation chrome
- push destinations without persistent navigation chrome

This app-shell rule improves consistency as the graph grows.
