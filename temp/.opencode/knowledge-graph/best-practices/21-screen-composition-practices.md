# 21. Screen Composition Practices

From the feature screens and shared components, a few UI composition rules stand out.

### 20.1 Build screens from focused sections

Examples in the codebase include:

- summary cards on Home
- dedicated components for prayer rows and hero blocks
- bottom sheets for focused mutations instead of cluttering the main screen

Best practice:

- keep root screens orchestration-focused
- move repeated visual sections into named composables
- keep them close to the feature unless shared broadly

### 20.2 Split every screen into a Screen/Content pair

Each screen has two composable responsibilities:

- **`XxxScreen`**: wires the ViewModel, collects state, observes `eventFlow` for one-shot effects, accepts navigation callbacks, and delegates rendering.
- **`XxxContent`**: a pure composable that receives only the state, an event callback (`onAction`), navigation callbacks, and a `modifier`. No ViewModel dependency, no effect handling.

This follows a pattern similar to "smart" and "dumb" components:

```
Screen (smart container)
  ├── collects viewModel.state
  ├── observes viewModel.eventFlow (navigation, snackbar, etc.)
  ├── accepts navigation callbacks as lambda parameters
  └── delegates to Content

Content (pure presentation)
  ├── wrapped in Scaffold with TopAppBar
  ├── receives state + onAction + onNavigateBack
  ├── no ViewModel reference
  └── emits actions via onAction(SampleAction.Load(force = true))
```

Best practice:

- keep `XxxScreen` thin — wire ViewModel, collect effects, delegate layout
- keep `XxxContent` stateless and ViewModel-free
- wrap every `XxxContent` in a `Scaffold` with a `TopAppBar` and back navigation
- add a `@PreviewScreenSizes` annotated wrapper to `XxxContent` with sample data for IDE preview

### 20.3 Use bottom sheets for bounded editing flows

Bottom sheets work well for actions such as:

- logging reading
- setting goals
- updating positions
- managing habits

Reusable principle:

- use bottom sheets for short, contextual tasks that should not feel like full navigation
