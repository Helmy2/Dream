# Dream OpenCode Agent Map

## Purpose

This document defines which OpenCode agent should own each major Dream workflow stage.

Dream should be OpenCode-agent-first.
That means workflow guidance should be written around real OpenCode agent responsibilities, not abstract agent roles.

## Available OpenCode Agents

- `general`: built-in orchestration, multi-step execution, and decision flow
- `dream-explore`: Dream-specific fast codebase discovery and targeted searching
- `dream-context-scout`: Dream-specific focused read-only context pack creation
- `implementer`: built-in or Dream-specific code-writing role depending on invocation strategy
- `inspector`: built-in or Dream-specific QA gate depending on invocation strategy

## Default Ownership

### `general`

Owns:

- overall Dream workflow orchestration
- deciding which subagent to call next
- contract-generation coordination
- verification-loop coordination
- final status reporting

Should do:

- interpret the user request
- decide whether backend is needed
- decide whether more context gathering is required
- launch `dream-context-scout`, `dream-explore`, `implementer`, or `inspector` as needed
- ensure that Dream does not report validated success before verification is green, contract parity is checked, required tests are present, and review status is documented

Should not do by default:

- large direct code edits when `implementer` should own them
- final QA signoff without `inspector` for substantial generation or repair work

### `dream-explore`

Owns:

- fast project and template discovery
- locating relevant modules, files, and symbols
- locating architecture rules by topic

Use when:

- the orchestrator needs to know where something lives
- the repair loop needs to find all affected files quickly
- a prompt needs targeted codebase discovery without deep planning

Expected output:

- concise path list
- exact matches
- minimal explanation of what was found

### `dream-context-scout`

Owns:

- focused read-only context packs before implementation or repair

Use when:

- a feature implementation needs a precise set of relevant files
- a repair needs exact surrounding context
- the template or target project is large enough that a focused read pack is better than ad hoc searching

Expected output:

- relevant paths
- small structural summary
- key snippets or file roles

### `implementer`

Owns:

- template instantiation
- feature implementation
- contract-driven code generation
- code repair
- `.mvp/` artifact updates when part of the current task

Use when:

- code must be written or edited
- template content must be adapted into a generated app
- tests or build failures require fixes

Must preserve:

- `Presentation -> Domain <- Data`
- thin app shells
- centralized navigation
- shared infrastructure boundaries

### `inspector`

Owns:

- architecture and behavior review
- final QA gate before Dream reports validated success

Use when:

- a substantial feature has been generated
- a repair loop claims green status
- the orchestrator needs an explicit pass or defect list

Expected output:

- exactly `APPROVED` when clean
- otherwise an itemized defect list

## Default Workflow Map

### Project Creation

1. `general` interprets the request.
2. `dream-explore` or `dream-context-scout` gathers relevant template and graph context if needed.
3. `general` generates or coordinates `.mvp/app-contract.yaml` and decisions.
4. `implementer` instantiates the selected Dream template.
5. `implementer` replaces or prepares sample feature scaffolding.
6. when the first real feature is present, `implementer` removes `feature/sample` from the generated app unless a documented temporary exception remains.

### Feature Generation

1. `general` selects the feature to generate.
2. `dream-context-scout` gathers the exact template and feature context.
3. `general` produces or refines the feature contract.
4. `implementer` generates the feature.
5. `implementer` adds or updates tests.
6. `inspector` reviews if the change is substantial.

### Verification And Repair

1. `general` runs or coordinates verification.
2. `dream-explore` or `dream-context-scout` narrows the failure surface when needed.
3. `implementer` applies the smallest correct fix.
4. `general` reruns verification.
5. `inspector` acts as the final review gate before validated status is reported.

If `inspector` fails at the tool level, `general` must run a manual review fallback and avoid reporting validated status from verification results alone.

## Required Inputs By Agent

### `general`

- user request
- `AGENTS.md`
- `.knowledge-graph/PRODUCT.md`
- `dream-agent-operating-guide.md`
- relevant `.mvp/` metadata

### `dream-explore`

- targeted search question
- path scope if known
- optional module or layer constraint

### `dream-context-scout`

- exact implementation or repair objective
- target module or feature if known
- any already-identified files

### `implementer`

- approved objective
- relevant contracts
- relevant context pack
- architecture constraints
- verification expectations

### `inspector`

- stated objective
- changed files or affected modules
- architecture rules and contract expectations
- verification status

## Handoff Rules

- `general` should hand off discovery to `dream-explore` or `dream-context-scout` instead of doing broad discovery itself
- `general` should hand off code changes to `implementer`
- `general` should use `inspector` before reporting validated success for substantial work
- `implementer` should not redefine architecture rules; it should apply the rules already established by Dream guidance and the graph
- `inspector` should judge against the contract, architecture rules, and verification status, not personal style preferences
