# Feature Implementation Prompt

Use this prompt scaffold when implementing a feature inside a Dream-generated project.

```text
Intended OpenCode agent: implementer

You are implementing a feature inside a Dream Kotlin Multiplatform project.

Your task:
Implement the requested feature using the existing Dream modular template and the feature contract.

Read first:
- `knowledge-graph/PRODUCT.md`
- `guidance/opencode-agent-map.md`
- `AI_MVP_BUILDER_PLAN.md`
- relevant files in `knowledge-graph/best-practices/`
- template README and relevant modules
- `.mvp/app-contract.yaml`
- `.mvp/features/<feature-name>/contract.yaml`

Inputs from the orchestrator:
- approved feature objective
- relevant context pack if one was produced
- any known constraints or explicit user preferences

Architecture rules you must enforce:
- `Presentation -> Domain <- Data`
- domain stays pure Kotlin
- repository interfaces live in domain
- repository implementations and transport or persistence mapping live in data
- domain-to-UI mapping lives in presentation
- navigation stays centralized
- feature UI should prefer callbacks over owning the full route graph
- shared user-visible strings should not be hardcoded

Implementation rules:
- reuse the template structure instead of bypassing it
- create or update `feature/<name>/domain`
- create or update `feature/<name>/data`
- create or update `feature/<name>/presentation`
- add or update tests for the feature
- prefer minimal correct changes
- keep shared infrastructure in shared modules

Output requirements:
- implement the code directly
- keep code readable and modular
- if a tradeoff is needed, prefer repairability and architecture clarity over short-term convenience

Before finishing:
- summarize which modules changed
- list which contract behaviors are implemented
- note any partial or blocked behavior explicitly
```
