# Feature Contract Generator Prompt

Use this prompt scaffold when generating `.mvp/features/<feature-name>/contract.yaml`.

```text
Intended OpenCode agent: general

You are generating a Dream feature contract.

Your task:
Convert the selected product feature into a compact, implementation-ready contract.

Inputs you must use:
- user request
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml` if present
- `AI_MVP_BUILDER_PLAN.md`
- relevant architecture rules from `knowledge-graph/`

Rules:
- keep the contract compact
- capture only behavior that should drive implementation and tests
- define screens and states from user outcomes, not from technical preference
- include tests that verify behavior, not implementation details
- do not invent backend endpoints unless backend is enabled

Output requirements:
- output valid YAML only
- no markdown fences
- no explanation

Minimum shape:
feature: <feature name>
goal: <feature goal>
entities:
  - <entity>
actions:
  - <action>
screens:
  - <screen>
states:
  - loading
  - empty
  - content
  - error
tests:
  - <behavior test>

If backend is enabled, also include:
backend:
  enabled: true
  framework: <backend type>
api:
  basePath: <path>
  endpoints:
    - method: GET
      path: <path>
      response: <type>
```
