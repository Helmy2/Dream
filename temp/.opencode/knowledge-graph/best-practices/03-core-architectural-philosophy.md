# 3. Core Architectural Philosophy

Strong architecture is usually strongest where it stays strict about boundaries.

The main architectural ideas are:

- package by feature, not by technical layer alone
- split each feature into `domain`, `data`, `api`, and `impl` (see note below)
- keep domain pure Kotlin
- make data implement domain contracts
- keep UI dependent on domain, not on data internals
- centralize cross-cutting concerns in `shared/core-*`
- use physical modules (one `module.yaml` each) to enforce dependency direction

> **Terminology note:** The canonical dependency rule uses "Presentation" to describe the UI layer, but the actual Dream template implements this as `feature/<name>-impl` (which contains screens, ViewModels, UI models, and the Koin module). The `feature/<name>-api` module holds only public navigation contracts (`NavKey` route types). When reading this handbook, "presentation layer" or "Presentation" in diagrams refers to the `impl` module unless explicitly noted otherwise.

Canonical dependency rule:

```text
Presentation -> Domain <- Data
```

Why this works well:

- features can evolve independently
- build times stay more manageable than with a monolith
- shared business logic remains portable to iOS
- mistakes become visible at module boundaries instead of spreading silently
