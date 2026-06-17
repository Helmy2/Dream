# 6. Dependency Rules To Keep

These are among the most valuable practices to carry into another project.

### 5.1 Strict dependency direction

- `feature:x:domain` should depend only on shared pure core modules such as `shared:core:domain`
- `feature:x:data` should depend on its own `domain` plus shared runtime infrastructure
- `feature:x:presentation` should depend on its own `domain`, shared presentation helpers, and the design system
- feature presentation modules should not depend on other feature presentation modules

### 5.2 Aggregator exception pattern

A useful exception pattern is allowing one aggregator-style feature:

- `feature:dashboard:presentation`

This module depends on other features' `domain` modules only, never their `presentation` modules. That is a good pattern for a dashboard or overview feature.

Best rule to copy:

- if a feature aggregates other features, let it depend only on their pure domain contracts

### 5.3 Project-specific exception to review before copying

In some codebases, one feature data module may end up depending on another feature's data and domain modules.

This is a coupling worth reviewing before reusing the pattern in another project. In a new app, prefer one of these alternatives:

- move shared logic into a shared core module
- expose a domain contract instead of depending on another feature's data implementation
- isolate setup or seeding responsibilities behind a dedicated shared service

Treat cross-feature data-module coupling as an exception, not a standard.
