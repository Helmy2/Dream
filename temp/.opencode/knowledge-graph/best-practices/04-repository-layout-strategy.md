# 4. Repository Layout Strategy

A flattened top-level structure often makes intent obvious:

```text
androidApp/
iosApp/
shared/
feature/
docs/
templates/
```

Best practice to reuse:

- keep native shells thin
- place reusable infrastructure under `shared/`
- place all product behavior under `feature/`
- keep documentation first-class in `docs/`
- keep shared build setup in `templates/` and the module list in `project.yaml`

Recommended mental model:

- `androidApp/`: Android host only
- `iosApp/`: iOS host only
- `shared/core-*`: foundational infrastructure
- `shared/designsystem/`: UI tokens, resources, shared components (Android-only Compose)
- `shared/navigation/`: Android-only Compose app shell and routing infrastructure
- `shared/umbrella-core/`: exported non-Compose business logic for SwiftUI and other native UI consumers
- `shared/umbrella-ui/`: Android-only Compose app composition root, Koin startup, and Navigation 3 entry aggregation
- `feature/*`: business capabilities
