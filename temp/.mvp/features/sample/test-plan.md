# Sample Feature Test Plan

## Domain

- Verify `SampleItem` stores title, description, category, `occurredAtMillis`, and `updatedAtMillis` without platform dependencies.
- Verify `SampleItem.update(...)` trims editable fields, changes category and `updatedAtMillis`, and preserves identity and `occurredAtMillis`.
- Verify `SampleRepository` contract examples return `Result`-wrapped list, detail, create, and update domain items.

## Data

- Verify mapper conversion between Room `SampleItemEntity` and domain `SampleItem`.
- Verify `RoomSampleRepository` seeds starter rows only with stable content keys, not user-visible copy.
- Verify create trims user input, assigns an id, and uses `FakeTimeProvider` for `occurredAtMillis` and `updatedAtMillis`.
- Verify update uses `FakeTimeProvider` for `updatedAtMillis` and preserves existing Room position.

## Presentation

- Verify loading, content, empty/error handling, detail selection, create, update, validation, and save success events.
- Verify starter content keys map to presentation resources while raw user-entered create/update text remains editable and persistent.
- Verify occurred labels are formatted through injected `TimeProvider.formatDate(...)`.

## Maestro Smoke

- Android flow: launch, assert starter read state, create a new item, edit detail content, assert updated content and `Occurred: .*` detail row.
- iOS flow: same behavior with `pressKey: Enter` after single-line title edits and full rendered detail-row selectors.
- Environment blocker policy: if `adb devices` reports 0 devices or no booted iOS simulator is available, record `blocked by environment` instead of treating smoke as passed.
