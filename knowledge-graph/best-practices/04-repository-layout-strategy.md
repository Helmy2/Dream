# 4. Repository Layout Strategy

A flattened top-level structure often makes intent obvious:

```text
androidApp/
iosApp/
shared/
feature/
docs/
build-logic/
```

Best practice to reuse:

- keep native shells thin
- place reusable infrastructure under `shared/`
- place all product behavior under `feature/`
- keep documentation first-class in `docs/`

Recommended mental model:

- `androidApp/`: Android host only
- `iosApp/`: iOS host only
- `shared/core/`: foundational infrastructure
- `shared/designsystem/`: UI tokens, resources, shared components
- `shared/navigation/`: app shell and routing infrastructure
- `shared/umbrella-core/`: exported business logic for iOS
- `shared/umbrella-ui/`: Android-side app composition root for shared UI
- `feature/*`: business capabilities
