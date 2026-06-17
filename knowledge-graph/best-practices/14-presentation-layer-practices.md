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
- `reduce {}`
- `intent {}`
- `exclusiveIntent(key)`

The most reusable idea here is not complexity, but restraint. The base class is small and practical.

Best practice:

- keep MVI infrastructure minimal
- provide only the primitives teams actually use
- avoid building a framework inside the app unless necessary

### 13.3 Exclusive job handling for repeatable actions

`exclusiveIntent` is a good pattern for actions that should cancel older in-flight work by key.

Use this for:

- repeated toggles
- repeated refreshes for the same target
- rapid user taps that should collapse into one active job per item

### 13.4 Prefer observation functions grouped by responsibility

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

### 13.5 One caution from the current codebase

Not every presentation module in a real codebase will be fully aligned. For example, one screen may still use plain `ViewModel` plus internal `MutableStateFlow` rather than the shared MVI base.

For reuse in another project, prefer choosing one presentation pattern and applying it consistently.

Recommended standard:

- use the shared MVI base everywhere unless a screen has a compelling reason not to
