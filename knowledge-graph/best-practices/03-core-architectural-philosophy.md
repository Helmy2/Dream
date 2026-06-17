# 3. Core Architectural Philosophy

Strong architecture is usually strongest where it stays strict about boundaries.

The main architectural ideas are:

- package by feature, not by technical layer alone
- split each feature into `domain`, `data`, and `presentation`
- keep domain pure Kotlin
- make data implement domain contracts
- keep UI dependent on domain, not on data internals
- centralize cross-cutting concerns in `shared/core/*`
- use physical Gradle modules to enforce dependency direction

Canonical dependency rule:

```text
Presentation -> Domain <- Data
```

Why this works well:

- features can evolve independently
- build times stay more manageable than with a monolith
- shared business logic remains portable to iOS
- mistakes become visible at module boundaries instead of spreading silently
