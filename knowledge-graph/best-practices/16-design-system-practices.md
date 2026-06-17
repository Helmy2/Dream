# 16. Design System Practices

A small but useful shared design system is often enough.

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

Shared components include:

- `SurfaceCard`
- `AppBottomSheet`
- `DateStrip`
- `PrimaryButton`
- `GhostButton`

Why this matters:

- feature modules reuse interaction patterns
- visual consistency comes from a shared source
- future polishing happens once instead of in every screen

### 15.3 Keep design-system components layout-agnostic where possible

A shared surface card is a good example because it avoids forcing inner padding.

Best practice:

- reusable UI primitives should define surface behavior and semantics, not too much screen-specific layout
