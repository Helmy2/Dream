# 14. Presentation Layer Practices

One strong Android presentation style is a mostly MVI-oriented approach.

### 14.1 Separate state, actions, and events

The reusable structure is:

- `UiState`: long-lived renderable state
- `UiAction`: user or UI inputs into the view model
- `UiEvent`: one-off effects such as navigation

Why this helps:

- screens are easier to reason about
- actions become explicit API surface
- one-off effects do not leak into persistent state

### 14.2 Base MVI view model with small primitives

A base MVI view model can provide:

- `state: StateFlow<S>`
- `eventFlow: Flow<E>`
- a sequential action queue

The most reusable idea here is not complexity, but restraint. The base class is small and practical.

Best practice:

- keep MVI infrastructure minimal
- provide only the primitives teams actually use
- avoid building a framework inside the app unless necessary

### 14.3 Prefer lifecycle-backed state holders

In multiplatform Compose apps, the shared MVI base should extend lifecycle `ViewModel` so Android keeps state across configuration changes.

Best practice:

- prefer lifecycle-backed `ViewModel` over plain classes for long-lived screen state
- let the base class own the processing scope in production
- allow a scope override only where tests need deterministic control

### 14.4 Queue actions sequentially

If actions can arrive rapidly, the base MVI layer should process them through a single queue instead of launching one coroutine per action.

Best practice:

- send UI actions through one channel or equivalent queue
- handle actions sequentially to avoid interleaved state writes
- keep retry or force-refresh semantics explicit in the action model

### 14.5 Exclusive job handling for repeatable actions

`exclusiveIntent` is a good pattern for actions that should cancel older in-flight work by key.

Use this for:

- repeated toggles
- repeated refreshes for the same target
- rapid user taps that should collapse into one active job per item

### 14.6 Prefer observation functions grouped by responsibility

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

### 14.7 Make initial loading explicit and stable

Initial loading should start from a stable lifecycle-aware entry point, not from unstable constructor timing or repeated dependency assembly.

Best practice:

- trigger initial loading from an explicit action or stable orchestration point
- make retry behavior explicit instead of relying on reconstruction side effects
- keep first-load behavior testable without a Compose runtime

### 14.8 Split screens into a Screen/Content pair

Each screen should be composed of two composable functions:

- **`XxxScreen`** — the smart container. Accepts the ViewModel (defaulting to `koinViewModel()`) and navigation callbacks. Collects state, observes `eventFlow` for one-shot effects, dispatches initial actions, and delegates rendering to `XxxContent`.
- **`XxxContent`** — the pure presentation composable. Accepts only the state, an event callback, and an optional `modifier`. No ViewModel dependency, no effect handling, no navigation awareness.

This keeps rendering testable without a ViewModel and makes effect handling (navigation, snackbars) explicit at the screen boundary.

> **See also:** [21.2 Split every screen into a Screen/Content pair](21-screen-composition-practices.md) for additional composition patterns.

```kotlin
@Composable
fun SampleScreen(
    viewModel: SampleViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            // handle one-shot effects
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.onAction(SampleAction.Load())
    }

    SampleContent(
        state = state,
        onAction = { action -> viewModel.onAction(action) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleContent(
    state: SampleState,
    onAction: (SampleAction) -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.sample_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        // pure rendering, no ViewModel reference
    }
}
```

Best practice:

- keep `XxxScreen` thin — wire ViewModel, collect effects, delegate layout
- keep `XxxContent` stateless — derive everything from `state`, emit actions via `onAction`
- wrap every `XxxContent` in a `Scaffold` with a `TopAppBar` for consistent screen structure
- pass navigation as explicit lambda parameters (`onNavigateBack`, etc.), not via a navigator singleton
- add a `@PreviewScreenSizes` annotated wrapper to `XxxContent` with sample state for IDE preview

### 14.9 UiState must hold UI models, not domain types

State classes in `impl` must use UI model types for all data fields, never raw domain types.

Best practice:

- create dedicated UI model data classes in `feature/<name>/impl` for each piece of renderable data (list items, detail summaries, formatted values)
- inject `PresentationMapper` into the ViewModel via Koin `factory` — do not instantiate mappers in composables
- use the mapper inside the ViewModel to convert domain models to UI models before publishing to state
- composable views receive already-mapped UI models — they perform zero domain-to-UI transformation

```kotlin
// Correct: ViewModel uses mapper, state holds UI model
data class TransactionUiItem(
    val id: String,
    val formattedAmount: String,
    val formattedDate: String,
    val categoryLabel: String,
    val typeLabel: String,
)

// ViewModel
class TransactionViewModel(
    private val repository: TransactionRepository,
    private val mapper: TransactionPresentationMapper,
) : ViewModel() {
    private val _state = MutableStateFlow(TransactionState())
    val state: StateFlow<TransactionState> = _state.asStateFlow()

    fun onAction(action: TransactionAction) {
        when (action) {
            is TransactionAction.Load -> load()
        }
    }

    private fun load() {
        // ... fetch domain models, then map to UI models
        val transactions = repository.getAll()
        _state.update { it.copy(
            contentState = TransactionContentState.Content(
                items = transactions.map { mapper.toUiItem(it) }
            )
        ) }
    }
}
```

### 14.10 One caution from the current codebase

Not every presentation module in a real codebase will be fully aligned. For example, one screen may still use plain `ViewModel` plus internal `MutableStateFlow` rather than the shared MVI base.

For reuse in another project, prefer choosing one presentation pattern and applying it consistently.

Recommended standard:

- use the shared MVI base everywhere unless a screen has a compelling reason not to
