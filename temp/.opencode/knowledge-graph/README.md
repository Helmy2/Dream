# Knowledge Graph

This folder contains the initial architecture knowledge graph used by Dream, the AI app builder.

The first version is sourced from the split files under `.opencode/knowledge-graph/best-practices/`.

## Purpose

The graph is optimized for three AI tasks:

- retrieval of architecture rules and rationale
- generation guardrails for new code
- architecture review and violation detection

## Files

- `schema.md`: canonical node, edge, and metadata definitions
- `graph.json`: structured nodes and edges with source line references
- `source-chunks.json`: reusable source excerpts grouped by topic

## Modeling Rules

- prefer explicit constraints over vague topic tags
- attach every node and edge to a source line range
- classify guidance as `universal`, `kmp_oriented`, or `project_specific`
- mark strength as `non_negotiable`, `recommended`, `default`, or `caution`
- keep anti-patterns and exceptions first-class so the AI can review generated code against them

## Initial Scope

The initial graph covers:

- architecture boundaries
- feature module structure
- dependency rules
- shared core splitting
- domain practices
- data practices
- presentation practices
- navigation practices
- resources and localization
- dependency injection
- default rules
- anti-patterns
- package and app identity normalization
- stable composition roots
- UI quality and state design
- iOS host structure, KMP bridge rules, Xcode configuration, and XCTest packaging

## Next Extensions

Future iterations can add:

- `AGENTS.md` as a separate source document
- generated query presets for the builder
- automated validation scripts for graph completeness

## Complementary Tools

This folder contains the **prescriptive** knowledge graph — architecture rules, constraints, and anti-patterns that define what agents *should* do.

For **descriptive** code intelligence — what code *actually* does — Dream uses `codebase-memory-mcp`, a separate MCP server that builds a persistent knowledge graph of the codebase:

- Functions, classes, call chains, dependencies
- Symbol search, call tracing, impact analysis
- Architecture overview, dead code detection

The two work together: the prescriptive graph enforces rules, while codebase-memory-mcp provides code-level understanding. Agents should prefer MCP queries over grep/read for codebase exploration.

See `.opencode/guidance/reference/codebase-memory-mcp-tools.md` for the full tool reference.

## Product Identity

- product codename: `Dream`
- product role: local AI app builder
