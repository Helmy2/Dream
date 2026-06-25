# 32. Feature Boundary And Reuse Practices

Feature reuse should preserve clear ownership instead of creating hidden cross-feature coupling.

### 32.1 Keep presentation ownership local

A feature presentation module owns its screens, UI state, actions, events, mappers, and Navigation 3 route keys.

Reusable principle:

- feature presentation modules should not depend directly on another feature presentation module
- cross-feature navigation should use callbacks, events, or route wiring at a composition root
- a feature may navigate to a destination owned by another feature, but it should not import that destination screen or route from inside its own presentation code unless the composition-root contract explicitly allows it

### 32.2 Reuse domain contracts carefully

One feature may read another feature's domain model or repository contract when it is an aggregator, dashboard, or clearly dependent workflow.

Reusable principle:

- domain-only reuse is safer than presentation or data reuse
- document why the dependency is product-level, not just convenient
- avoid bidirectional feature dependencies

### 32.3 Treat cross-feature data reuse as a temporary exception

Reusing another feature's data implementation can be acceptable during a first-pass MVP when the same entity and repository already exist, but it should be explicit and reviewed.

Reusable principle:

- prefer one owning feature data layer for one persisted aggregate
- if two features need equal ownership of the same persisted concept, extract the storage contract and implementation to an appropriate shared module or shared domain/core area
- do not let a second feature silently mutate another feature's data model without tests for both workflows
- record the decision in `.mvp/decisions.yaml` when cross-feature data reuse is chosen

### 32.4 Extract shared behavior when reuse becomes product infrastructure

Move behavior out of a feature when it is no longer feature-specific.

Extraction signals:

- three or more features need the same model, mapper, formatting, repository, validator, or storage table
- two features both create and update the same persisted aggregate as primary behavior
- ownership cannot be explained in one sentence during review
- cross-feature imports are needed only to avoid making a proper shared abstraction

Reusable principle:

- shared domain abstractions belong in pure shared modules
- shared storage policy belongs in shared data modules
- shared UI components belong in design system or shared presentation only after they are genuinely reusable
- do not extract prematurely when one feature clearly owns the behavior
