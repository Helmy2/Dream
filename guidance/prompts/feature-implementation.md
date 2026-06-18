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
- `guidance/templates/implementation-report-template.md`
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
- time-sensitive behavior must flow through `TimeProvider`
- composition roots must stay stable across recomposition

Implementation rules:
- reuse the template structure instead of bypassing it
- create or update `feature/<name>/domain`
- create or update `feature/<name>/data`
- create or update `feature/<name>/presentation`
- add or update tests for the feature
- prefer minimal correct changes
- keep shared infrastructure in shared modules
- map every contract action, state, and required test to code and test coverage explicitly
- do not generate placeholder states unless they are implemented and testable
- do not instantiate repositories, use cases, or ViewModels directly inside recomposing composables
- do not let feature UI own routing infrastructure when callbacks or events are sufficient
- when the contract marks time-sensitive behavior, inject `TimeProvider` instead of reading system time directly
- include loading, empty, content, and error state handling when the contract requires them

Output requirements:
- implement the code directly
- keep code readable and modular
- if a tradeoff is needed, prefer repairability and architecture clarity over short-term convenience

Before finishing:
- summarize which modules changed
- provide a contract mapping table with one row per action, state, and required test
- mark each mapped item as `implemented`, `partial`, or `blocked`
- identify the code location and test coverage for each mapped item
- note any partial or blocked behavior explicitly
- write `.mvp/features/<feature-name>/implementation-report.md` using `guidance/templates/implementation-report-template.md`

Required final report shape:
- follow `guidance/templates/implementation-report-template.md` exactly unless the orchestrator requests a stricter variant
```
