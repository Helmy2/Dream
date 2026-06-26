# Implementation Report Template

Use this template when writing `.features/<feature-name>/implementation-report.md`.

```md
# Implementation Report: <feature-name>

## Modules Changed

- feature/<feature-name>/domain
- feature/<feature-name>/data
- feature/<feature-name>/presentation
- shared/<module> if applicable

## Contract Mapping

| item | type | status | code location | test coverage | notes |
| --- | --- | --- | --- | --- | --- |
| <contract item> | entity-field\|action\|state\|test | implemented\|partial\|blocked | <module/path> | <test name or file> | <important note> |

## Partial Items

- <item>: <why it is partial and what remains>

## Blocked Items

- <item>: <blocker, whether environment or code, and next step>

## Notes

- <tradeoffs, architecture decisions, or important implementation details>
```

Required usage rules:

- include one row for every contract action
- include one row for every contract state
- include one row for every contract entity field when the feature has persisted or user-entered entities
- include one row for every required contract test
- use `implemented`, `partial`, or `blocked` only
- do not omit partial or blocked behavior; record it explicitly
- prefer project-relative code locations that point to the owning module or file
