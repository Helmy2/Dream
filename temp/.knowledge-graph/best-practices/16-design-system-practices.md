# 16. Design System Practices

A shared design system provides reusable UI primitives for all feature modules.

### 15.1 Shared tokens beyond colors and typography

A shared theme can expose composition locals for:

- spacing
- elevation
- size
- border radius
- motion

Best practice:

- treat spacing, size, radius, and motion as first-class tokens
- avoid scattering raw `dp` values everywhere

### 15.2 Reusable app-wide components

All shared components live under `designsystem/components/`. Each component or component family has its own file. The library is organized by category:

**Actions & Buttons (4)**
- `Button` — primary/secondary/destructive/outlined/tertiary variants via `ButtonVariant` enum
- `FloatingActionButton` — M3 FAB wrapper with Dream theme colors
- `IconButton` — M3 IconButton wrapper
- `SegmentedButton` — M3 `SingleChoiceSegmentedButtonRow` wrapper

**Text Input & Selection (5)**
- `TextField` — `OutlinedTextField` with Dream color styling
- `Checkbox` — M3 `Checkbox` wrapper
- `RadioButton` — M3 `RadioButton` wrapper
- `Switch` — M3 `Switch` wrapper
- `Slider` — M3 `Slider` wrapper

**Navigation (4)**
- `AppBar` — M3 `TopAppBar` wrapper
- `Navigation` — `NavigationBar` and `NavigationRail` with destination model
- `Tabs` — M3 `TabRow` wrapper
- `Pagination` — custom prev/next buttons with page indicator

**Containment & Layout (3)**
- `Card` — elevated/outlined/filled variants via `CardVariant` enum; slot-based (title, body, accent, leading, action, content)
- `Divider` — M3 `HorizontalDivider` wrapper
- `List` — `Column` of clickable `ListItem` composables

**Overlays, Dialogs & Layering (3)**
- `Dialog` — M3 `AlertDialog` wrapper
- `Sheet` — M3 `ModalBottomSheet` wrapper with 24dp top corners
- `Menu` — M3 `DropdownMenu` wrapper

**Feedback & Communication (5)**
- `Badge` — `BadgedBox` wrapper with overflow logic (max 99 → "99+")
- `Snackbar` — M3 `Snackbar` wrapper
- `Banner` — custom persistent banner with action/dismiss buttons
- `ProgressIndicator` — `LinearProgressIndicator` (determinate/indeterminate) and `CircularProgressIndicator` (three sizes)
- `Skeleton` — custom shimmer placeholder with animated gradient

Key patterns:
- 24dp corner radius for cards and bottom sheets
- `titleMedium`/`bodyMedium` typography convention
- `DreamTheme.spacing.md` (16dp) inner padding
- `ComponentSize` enum (Small/Medium/Large) for size variants
- Variant enums (`ButtonVariant`, `CardVariant`) map to different M3 implementations internally

### 15.3 Keep design-system components layout-agnostic where possible

Components define surface behavior and semantics, not screen-specific layout.

Best practice:
- slot-based APIs (leading, trailing, content, action) let callers supply layout
- avoid hardcoding outer padding or fixed widths

### 15.4 Preview every component

Every public composable in the design system has a `private` preview annotated with `@PreviewLightDark`. This ensures visual coverage across light/dark modes without polluting the public API.

Prefer slot-based previews that demonstrate the component with realistic sample content.
