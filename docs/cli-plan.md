# Dream CLI Plan

## Purpose

This file defines the first CLI plan for Dream.

Dream should become a usable local command-line product that wraps the existing OpenCode-based generation workflow in a stable, repeatable interface.

OpenCode remains the execution engine.
The Dream CLI becomes the product entrypoint.

## Goal

Build the smallest useful Dream CLI that can:

- accept a plain-language app request
- create or target a project directory
- run the Dream generation workflow
- write `.mvp/` artifacts consistently
- run verification
- report status, cost, and output quality notes

## Non-Goals For The First CLI Version

- backend generation
- cloud execution
- remote deployment
- full visual editor
- complex interactive setup flows
- replacing OpenCode internals

## Why The CLI Exists

Dream needs a stable interface above raw agent usage.

The CLI should give Dream:

- a fixed command surface
- repeatable project lifecycle steps
- standard report locations
- stable run metadata
- clearer failure handling
- less dependence on prompt phrasing

Without the CLI, Dream remains a useful workflow, but not yet a clean product.

## Product Model

```text
Dream CLI
  -> Dream workflow rules
    -> OpenCode agents and tools
      -> template, code generation, verification, review
```

## First CLI Scope

### Required Commands

1. `dream create <prompt>`
- create a new project from a natural-language request
- generate `.mvp/app-contract.yaml`
- generate `.mvp/decisions.yaml`
- instantiate the base template
- generate the first feature
- run verification
- write `.mvp/verification-report.md`

2. `dream verify`
- run Dream verification on the current project
- repair if configured to do so
- update `.mvp/verification-report.md`

3. `dream review`
- run Dream review on the current project
- produce approval or defect output

4. `dream report`
- print a summary from `.mvp/` artifacts and run reports

### Likely Near-Term Commands

1. `dream feature <prompt>`
- add or update a feature in the current Dream project

2. `dream doctor`
- check required environment setup
- validate OpenCode availability
- validate Android SDK and Gradle readiness

## Command Behavior

### `dream create`

Inputs:

- app prompt
- optional destination path
- optional app name override

Outputs:

- project directory
- generated `.mvp/` files
- verification summary
- run report

Required behavior:

- fail clearly if destination is invalid
- record the prompt used
- record run status
- record verification outcomes
- record review status

### `dream verify`

Inputs:

- current project directory

Outputs:

- updated `.mvp/verification-report.md`
- console summary of pass, fail, or blocked state

### `dream review`

Inputs:

- current project directory

Outputs:

- approval result or defect list
- explicit note if delegated review failed and manual fallback was used

### `dream report`

Inputs:

- current project directory

Outputs:

- compact human-readable summary of:
  - app identity
  - latest verification status
  - latest review status
  - latest run cost and timing if available
  - generated feature list

## Required Files And Reports

Each Dream project should continue to use:

- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- `.mvp/verification-report.md`
- `.mvp/features/<feature>/contract.yaml`
- `.mvp/features/<feature>/test-plan.md`
- `.mvp/features/<feature>/implementation-report.md`

The CLI should also standardize run reporting.

Recommended report location:

```text
.mvp/runs/
  2026-06-18T10-30-00Z-create.md
```

Each run report should capture:

- command
- prompt if applicable
- project path
- start time
- end time
- elapsed time
- token usage if available
- dollar cost if available
- generated artifacts
- verification commands and outcomes
- review result
- final decision: `approved`, `partial`, or `blocked`

## First CLI UX Principles

- keep commands simple
- prefer explicit files over hidden state
- print concise progress updates
- store detailed results in `.mvp/`
- do not pretend quality is good just because builds passed
- keep the first version scriptable and local-first

## Minimum Success Rules

The CLI should not print final success unless:

- required verification commands passed or a real blocker is documented
- contract behavior is implemented
- required tests exist for the implemented behavior
- review result is approved or explicitly partial due to review-tool failure

## Architecture For The First CLI Version

The first version should be thin.

Suggested shape:

```text
cli/
  main
  commands/
    create
    verify
    review
    report
  runtime/
    project-resolution
    run-reporting
    process-exec
    opencode-adapter
```

Responsibilities:

- command parsing
- locating the active Dream project
- invoking Dream workflow entrypoints
- collecting run metadata
- writing standardized reports

The CLI should not reimplement Dream generation logic immediately.
It should orchestrate the existing workflow cleanly first.

## OpenCode Integration Strategy

First version:

- call the existing Dream command workflow
- keep `.opencode/` commands and agents as the main execution mechanism

Later version:

- move repeatable parts into more explicit Dream runtime code only when needed

This keeps the first CLI small and practical.

## Implementation Phases

### Phase 1: CLI Skeleton

Deliver:

- command parsing
- project path resolution
- `dream create`
- `dream verify`
- `dream review`
- `dream report`
- standard run report writing

### Phase 2: Better Run Metadata

Deliver:

- elapsed time tracking
- token and cost capture when available
- run IDs
- stable report indexing

### Phase 3: Better Environment Handling

Deliver:

- `dream doctor`
- environment validation
- clearer setup failures

### Phase 4: Better Workflow Quality

Deliver:

- stricter success criteria
- stronger review fallback behavior
- stronger UI and contract quality gates

## Risks

1. The CLI may become a thin wrapper with unclear value.
- Avoid this by making reporting, lifecycle, and failure handling better than raw OpenCode use.

2. The CLI may over-couple to current OpenCode behavior.
- Avoid this by keeping the CLI focused on inputs, outputs, and reports rather than agent internals.

3. The CLI may claim success too early.
- Avoid this by enforcing Dream's stronger success rules.

4. The CLI may hide important failure detail.
- Avoid this by printing concise summaries and storing detailed reports in `.mvp/`.

## Open Questions

1. What language should the CLI be written in?
2. Should the CLI invoke OpenCode through shell commands, config files, or another adapter layer?
3. Should run reports live only under project `.mvp/`, or also under a global Dream cache?
4. Should `dream create` always create a new folder, or allow in-place generation?

## Recommended Immediate Next Tasks

1. choose the CLI implementation language
2. define the exact command syntax for Phase 1
3. define the run report format under `.mvp/runs/`
4. define how the CLI invokes the current Dream workflow
5. implement the thinnest possible `dream create` path first
