# 26. Anti-Patterns To Avoid When Reusing This Architecture

These are the main things to avoid in future projects, even if traces of them exist here.

- do not let feature data modules depend casually on other feature data modules
- do not mix raw preference parsing into UI or domain layers
- do not call system time directly throughout the app
- do not hardcode user-visible strings in shared UI code
- do not let navigation route types leak into every feature module by default
- do not instantiate repositories, use cases, or ViewModels inside recomposing composables
- do not create giant convention plugins that hide the whole build
- do not make the design system too opinionated for feature-specific layout needs
- do not build a complicated MVI framework if a small base class is enough
