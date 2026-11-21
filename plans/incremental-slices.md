# Plan: Incremental slice workflow

Status: implemented (prompts and templates only; not yet exercised by a real `/dream-create` run).

## Goal

Replace the one-pass COMPLEX workflow (all domain/data, then all UI, then one verification) with a loop: `dream-general` cuts a feature into small vertical slices and finishes them one at a time, each verified and committed before the next starts. Smaller briefs and earlier failures give better work.

## Decisions

- **`dream-general` decides the slices and reports them.** They appear in the plan's Slices table. The existing plan confirmation (questions / edits / confirm / deny) stays, but there is no per-slice approval.
- **Every slice is verified on Android and iOS.** Full `verify-feature.sh` (build, Android and iOS tests, both app builds, Swift tests) plus device launch on both. No Android-only gate.
- **The loop stops only on failure.** A passing slice is committed on the feature branch and the next one starts. Merging to main still needs the user's confirmation.
- **Slice vertically, not by layer.** Slice 1 is the foundation: all four modules, tests, SwiftUI, bridge, Maestro flow, because the gate requires them from its first run. If an archetype is chosen, slice 1 adapts it.
- **Contract grows per slice.** Changes to an earlier slice's definitions go in the contract's change notes.
- **Iteration limits are per slice**; rollback targets the previous slice's commit.

## Speed

- **Focused inner loop.** While implementing and repairing, use focused module tests and `verify-feature.sh --quick`. The full gate runs once per slice, when those pass. A repair round no longer costs a full gate.
- **Launch only when UI changed.** The full gate still runs on both platforms every slice; the device install/launch step is skipped for slices with no UI change.
- **Merge tiny slices** into the previous one instead of giving them their own gate.
- Not done: changes to `verify-feature.sh` itself (overlap between `./kotlin build` and the app builds, running Android and iOS tests concurrently). Decide after timing the steps.

## Changed files (all under `temp/.opencode/`)

- `agents/dream-general.md`: COMPLEX workflow, new "Slice Loop" section, key rules, state handoff.
- `commands/dream-feature.md`, `commands/dream-create.md`, `commands/dream-resume.md`: slice loop, slice-aware resume.
- `guidance/templates/plan-template.md`: Slices table, per-slice loop, slicing rules.
- `guidance/new-feature-checklist.md`, `guidance/dream-agent-operating-guide.md`, `guidance/reference/skip-conditions.md`, `guidance/reference/complexity-routing.md`, `knowledge-graph/rules-condensed.md` (`ckpt-1`).

## Known costs and open questions

- A full gate with iOS on every slice is slow. Accepted for now; revisit after a real run.
- Slice 1 is heavier than the rest, since it has to satisfy the whole gate.
- Not verified: a real run. The Shelf test (see the archetypes plan) is the first one; check that slices are vertical, that slice 1 passes the gate, and that no slice is skipped or merged together.
