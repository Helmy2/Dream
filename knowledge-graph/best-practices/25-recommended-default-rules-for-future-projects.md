# 25. Recommended Default Rules For Future Projects

If this handbook is used to seed another project, these should be the default rules unless there is a clear reason to change them.

### 24.1 Architecture defaults

- use feature-based modules
- split features into `domain`, `data`, and `presentation`
- enforce `Presentation -> Domain <- Data`
- keep domain pure Kotlin

### 24.2 Core defaults

- split shared core into smaller responsibility-based modules
- centralize time access behind `TimeProvider`
- centralize storage and external service contracts in shared code

### 24.3 UI defaults

- use a shared design system
- centralize strings
- separate state, actions, and events
- keep navigation infrastructure outside feature UI where practical

### 24.4 Data defaults

- repositories return typed results
- keep mapping at the data boundary
- prefer cache-first reads for stable remote data
- use per-feature clients when service behavior differs

### 24.5 Platform defaults

- share business logic broadly
- keep platform implementations behind interfaces
- do not force shared UI when native UI is the better product choice
