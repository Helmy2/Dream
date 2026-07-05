# 32. Feature Boundary And Reuse Practices

Feature reuse should preserve clear ownership instead of creating hidden cross-feature coupling.

### 32.1 Keep navigation contracts in api, implementation in impl

A feature is split into two presentation-facing sub-modules:

- `feature/<name>/api`: the public navigation contract. Contains `NavKey` route types and any minimal public types needed for navigation. No screens, no ViewModels, no Koin module.
- `feature/<name>/impl`: the private implementation. Contains screens, ViewModels, UI state/actions/events, presentation mappers, and the Koin module that installs Navigation 3 entry providers.

Reusable principle:

- other features and the app shell depend on `feature/<name>/api` only
- no module outside the feature should depend on `feature/<name>/impl`
- cross-feature navigation goes through the target feature's `api`, never its `impl`

### 32.2 Reuse domain contracts carefully

One feature may read another feature's domain model or repository contract when it is an aggregator, dashboard, or clearly dependent workflow.

Reusable principle:

- domain-only reuse is safer than impl reuse
- document why the dependency is product-level, not just convenient
- avoid bidirectional feature dependencies

### 32.3 Treat cross-feature data reuse as a temporary exception

Reusing another feature's data implementation can be acceptable during a first-pass MVP when the same entity and repository already exist, but it should be explicit and reviewed.

Reusable principle:

- prefer one owning feature data layer for one persisted aggregate
- if two features need equal ownership of the same persisted concept, extract the storage contract and implementation to an appropriate shared module or shared domain/core area
- do not let a second feature silently mutate another feature's data model without tests for both workflows
- record the decision in `features/decisions.yaml` when cross-feature data reuse is chosen

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
