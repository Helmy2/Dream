# 14. Presentation Layer Practices

One strong Android presentation style is a mostly MVI-oriented approach.

### 13.1 Separate state, actions, and events

The reusable structure is:

- `UiState`: long-lived renderable state
- `UiAction`: user or UI inputs into the view model
- `UiEvent`: one-off effects such as navigation

Why this helps:

- screens are easier to reason about
- actions become explicit API surface
- one-off effects do not leak into persistent state

### 13.2 Base MVI view model with small primitives

A base MVI view model can provide:

- `state: StateFlow<S>`
- `eventFlow: Flow<E>`
- a sequential action queue

The most reusable idea here is not complexity, but restraint. The base class is small and practical.

Best practice:

- keep MVI infrastructure minimal
- provide only the primitives teams actually use
- avoid building a framework inside the app unless necessary

### 13.3 Prefer lifecycle-backed state holders

In multiplatform Compose apps, the shared MVI base should extend lifecycle `ViewModel` so Android keeps state across configuration changes.

Best practice:

- prefer lifecycle-backed `ViewModel` over plain classes for long-lived screen state
- let the base class own the processing scope in production
- allow a scope override only where tests need deterministic control

### 13.4 Queue actions sequentially

If actions can arrive rapidly, the base MVI layer should process them through a single queue instead of launching one coroutine per action.

Best practice:

- send UI actions through one channel or equivalent queue
- handle actions sequentially to avoid interleaved state writes
- keep retry or force-refresh semantics explicit in the action model

### 13.5 Exclusive job handling for repeatable actions

`exclusiveIntent` is a good pattern for actions that should cancel older in-flight work by key.

Use this for:

- repeated toggles
- repeated refreshes for the same target
- rapid user taps that should collapse into one active job per item

### 13.6 Prefer observation functions grouped by responsibility

`HomeViewModel` is a good example of organizing long-running collectors into focused methods:

- `observeSettings()`
- `observeHabits()`
- `observePrayers()`
- `observeAthkar()`
- `observeQuran()`
- `observeTasbeeh()`

Best practice:

- split observation pipelines by source or business concern
- keep `init` readable by delegating collectors to named methods

### 13.7 Make initial loading explicit and stable

Initial loading should start from a stable lifecycle-aware entry point, not from unstable constructor timing or repeated dependency assembly.

Best practice:

- trigger initial loading from an explicit action or stable orchestration point
- make retry behavior explicit instead of relying on reconstruction side effects
- keep first-load behavior testable without a Compose runtime

### 13.8 One caution from the current codebase

Not every presentation module in a real codebase will be fully aligned. For example, one screen may still use plain `ViewModel` plus internal `MutableStateFlow` rather than the shared MVI base.

For reuse in another project, prefer choosing one presentation pattern and applying it consistently.

Recommended standard:

- use the shared MVI base everywhere unless a screen has a compelling reason not to
