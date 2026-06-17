# 15. Navigation Practices

A centralized navigation structure is one of the clearest reusable patterns in this style of app.

### 14.1 Central app shell

A root app shell can act as the single structural entry point for shared Android UI.

Best practice:

- keep app-wide navigation, theme application, and layout direction at the shell level
- keep feature screens unaware of the full routing graph when possible

### 14.2 Typed routes

Routes are modeled as `@Serializable data object` instances implementing a sealed `Route` hierarchy.

Why this is good:

- exhaustive route handling
- safer saved-state serialization
- less stringly-typed navigation

### 14.3 Keep feature modules decoupled from routing when possible

The home feature does not depend on `shared:navigation`. Instead, it receives plain callbacks.

This is highly reusable.

Best practice:

- pass intent-level callbacks into feature UI modules
- keep routing infrastructure at the app shell or coordinator layer

### 14.4 Differentiate top-level tabs from push destinations

A well-structured app should clearly separate:

- top-level routes with bottom bar visibility
- push destinations without bottom bar visibility

This is a simple but effective app-shell rule that improves consistency.

### 14.5 Back behavior should be intentional

The app uses `AppBackHandler` and a `goHome()` pattern for non-home routes.

Best practice:

- define global back behavior as a product rule, not an accidental side effect of navigation defaults
