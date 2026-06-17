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

### 20.2 Use bottom sheets for bounded editing flows

Bottom sheets work well for actions such as:

- logging reading
- setting goals
- updating positions
- managing habits

Reusable principle:

- use bottom sheets for short, contextual tasks that should not feel like full navigation
