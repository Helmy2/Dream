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
- `AGENTS.md`
- relevant architecture rules from `.knowledge-graph/`

Rules:
- keep the contract compact
- capture only behavior that should drive implementation and tests
- define screens and states from user outcomes, not from technical preference
- include tests that verify behavior, not implementation details
- do not invent backend endpoints unless backend is enabled
- only include states, actions, and tests that Dream is expected to implement in this generation pass
- mark time-sensitive behavior explicitly when the feature depends on current date, time, or logical period behavior
- define minimum design expectations for loading, empty, content, and error states so UI quality is not left implicit

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
timeSensitiveBehavior:
  usesTimeProvider: <true|false>
  notes: <required when true>
designExpectations:
  loading: <expected loading treatment>
  empty: <expected empty-state message or action>
  content: <expected primary content structure>
  error: <expected error message and recovery treatment>
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
