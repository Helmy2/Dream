# codebase-memory-mcp Tools Reference

Quick reference for Dream agents using the codebase-memory-mcp MCP server.

## Overview

codebase-memory-mcp is a structural analysis backend that builds and queries a persistent knowledge graph of the codebase. It complements Dream's prescriptive knowledge graph (rules/constraints) with descriptive code intelligence (what code actually does).

## Priority Order

1. `search_graph` — find functions, classes, routes, variables by pattern
2. `trace_path` — trace who calls a function or what it calls
3. `get_code_snippet` — read specific function/class source code
4. `query_graph` — run Cypher queries for complex patterns
5. `get_architecture` — high-level project summary

## When to fall back to grep/glob

- Searching for string literals, error messages, config values
- Searching non-code files (Dockerfiles, shell scripts, configs)
- When MCP tools return insufficient results

## Examples

```
search_graph(name_pattern=".*Handler.*")
trace_path(function_name="OrderHandler", direction="inbound")
get_code_snippet(qualified_name="pkg.orders.OrderHandler")
query_graph("MATCH (f:Function)-[:CALLS]->(g) WHERE f.name = 'main' RETURN g.name")
get_architecture()
```
