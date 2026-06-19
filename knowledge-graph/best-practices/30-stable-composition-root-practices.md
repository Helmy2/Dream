# 30. Stable Composition Root Practices

Dream-generated apps should assemble feature dependencies at stable roots, not inside recomposing UI bodies.

### 30.1 Keep dependency assembly outside recomposing composables

Repositories, use cases, and ViewModels should be created in stable entry points such as app initialization, navigation coordinators, or explicit factory layers.

Reusable principle:

- do not instantiate repositories or ViewModels directly in a composable body that may recompose
- prefer stable factories, DI entry points, or top-level assembly functions
- pass assembled dependencies down into feature UI instead of rebuilding them during rendering

### 30.2 Keep root composables orchestration-focused

Root screens and app shells should wire callbacks, state holders, and route-level dependencies together.
They should not hide business-object construction in the same rendering path.

Reusable principle:

- root composables may coordinate state holders and callbacks
- repeated visual sections should be extracted into focused composables
- rendering code should stay separate from dependency assembly

### 30.3 Avoid eager side effects at unstable creation sites

If a ViewModel or state holder performs initial loading, that work should start from a stable lifecycle-aware entry point.

Reusable principle:

- do not rely on repeated construction inside recomposing UI to trigger loading
- avoid coupling init-time loading to unstable composition paths
- make refresh and initial-load behavior explicit and testable

### 30.4 Let root composables retrieve stable ViewModels, not build them

DI-backed root composables may retrieve stable ViewModels from the container, but they should not construct repositories or coroutine scopes to manufacture them.

Reusable principle:

- use root composables to retrieve DI-managed ViewModels
- avoid pairing `rememberCoroutineScope()` with manual ViewModel factories for screen lifetime management
- keep platform lifecycle ownership aligned with the DI entry point
