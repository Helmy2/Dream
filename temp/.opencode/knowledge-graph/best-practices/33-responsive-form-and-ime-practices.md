# 33. Responsive Form And IME Practices

Editable forms must work with software keyboards, small screens, and tablets.

### 33.1 Keep primary actions reachable

`imePadding()` helps, but it does not guarantee that a save action is usable while a field is focused.

Reusable principle:

- place primary actions close to the active form area
- make the save action reachable without requiring a fragile manual keyboard-dismiss step
- use scrollable content when the form can exceed the viewport
- verify reachability while the IME may still be visible

### 33.2 Use compact and wide form arrangements intentionally

Forms should adapt layout without changing business behavior.

Reusable principle:

- compact width should use a clear single-column flow
- wider width may group related fields side by side
- avoid placing the primary action far away from the edited fields on wide layouts
- keep validation messages visually attached to the relevant field

### 33.3 Prefer adaptive wrapping over rigid chip rows

Choice chips and filters should not assume a fixed number per row.

Reusable principle:

- use adaptive wrapping for categories, tags, and filters
- avoid hardcoded two-per-row chip grids unless the contract requires that exact structure
- keep touch targets large enough across phone, tablet, and pointer-driven layouts

### 33.4 Smoke-test the user outcome, not keyboard internals

Keyboard state is not a stable cross-platform assertion.

Reusable principle:

- enter the required text
- assert the primary action is visible or reachable while the keyboard may be open
- tap save and assert success-state, updated summary, or changed list/detail content
- use `pressKey: Enter` as an iOS fallback for single-line fields when the keyboard blocks the action
- avoid treating `hideKeyboard` success as required proof of product behavior

### 33.5 Keep adaptive behavior presentation-only unless product rules differ

Most responsive form changes are layout concerns.

Reusable principle:

- keep domain and data untouched when only compact-vs-wide layout changes
- keep `XxxContent` stateless so layout variants can share state and callbacks
- reuse shared design tokens for spacing, shape, and action hierarchy
