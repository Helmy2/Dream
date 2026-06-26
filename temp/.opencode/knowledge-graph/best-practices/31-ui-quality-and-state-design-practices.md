# 31. UI Quality And State Design Practices

Dream should not treat compile success as sufficient UI quality.
Generated features should make loading, empty, content, and error states feel intentional.

### 31.1 Define state-specific UX expectations in the contract

Feature contracts should describe what users see in loading, empty, content, and error states.
Those expectations should be implementable and testable.

Reusable principle:

- define user-facing state behavior, not only technical state names
- make recovery paths explicit for error states
- define the primary action or message for empty states when relevant

### 31.2 Use shared tokens instead of ad hoc spacing and sizing

Feature screens should follow the shared design system for spacing, size, and interaction affordances.

Reusable principle:

- prefer shared spacing, size, and motion tokens
- avoid scattering raw layout values without reason
- keep interaction targets large enough to feel deliberate instead of placeholder-like

### 31.3 Make every primary state feel complete enough to review

A structurally valid feature can still feel unfinished if only the content state is designed.

Reusable principle:

- loading should communicate that work is in progress
- empty should explain what is missing and what the user can do next
- content should establish a clear hierarchy for the main task
- error should explain failure clearly and offer a retry or recovery action when possible

### 31.4 Review UI quality with the same seriousness as architecture quality

Dream should document UI quality expectations before implementation and review them before approval.

Reusable principle:

- do not mark a feature approved when state handling is visually or behaviorally placeholder-level
- require contracts, implementation notes, and review output to mention state design quality when relevant

### 31.5 All user-visible strings must come from shared resources

No hardcoded string literals should appear in Compose UI code. Every user-visible string — labels, titles, buttons, error messages, content descriptions, hints — must be defined in `shared/core/strings/src/commonMain/composeResources/values/strings.xml` and referenced via `stringResource()`.

Workflow:
1. Before writing any Compose UI, define all needed strings in `strings.xml` with a clear naming convention (`<feature>_<purpose>`).
2. In Compose code, use `stringResource(Res.string.<feature>_<purpose>)` exclusively.
3. During review, grep for unescaped string literals in `@Composable` functions to catch any missed hardcoded text.

Reusable principle:

- shared strings are the single source of truth for user-facing text across supported platforms (Android and iOS)
- hardcoded strings in Compose code will not be translated, cannot be reviewed centrally, and violate the template's localization strategy
