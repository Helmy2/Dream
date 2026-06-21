---
description: Review a Dream project against Dream rules
agent: dream-inspector
subtask: true
---

Review the current Dream project against the knowledge graph, Dream operating guide, and feature contracts.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `.knowledge-graph/README.md`
- relevant `.knowledge-graph/best-practices/*.md`
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- relevant feature contracts

Focus on:
- architectural violations
- regressions
- contract mismatches
- missing verification coverage
- field-level contract integrity: every entity field, action signature, state field, time-sensitive behavior, and required test in each feature contract must map to code and tests
- generated-feature DI must use Koin compiler plugin DSL registrations, not annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan`

Report concrete findings with file references before any summary.
