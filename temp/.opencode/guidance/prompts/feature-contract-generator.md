# Feature Contract Generator Prompt

Use this prompt scaffold when generating `features/<feature-name>/contract.yaml`.

```text
Intended OpenCode agent: general

You are generating a Dream feature contract.

Your task:
Convert the selected product feature into a compact, implementation-ready contract.

Inputs you must use:
- user request
- `features/app-contract.yaml`
- `features/decisions.yaml` if present
- `AGENTS.md`
- relevant architecture rules from `.opencode/knowledge-graph/`

Rules:
- keep the contract compact
- capture only behavior that should drive implementation and tests
- define screens and states from user outcomes, not from technical preference
- include tests that verify behavior, not implementation details
- do not invent backend endpoints unless backend is enabled
- only include states, actions, and tests that Dream is expected to implement in this generation pass
- mark time-sensitive behavior explicitly when the feature depends on current date, time, or logical period behavior — this is REQUIRED, not optional
- define minimum design expectations for loading, empty, content, and error states so UI quality is not left implicit — all four states must be defined
- require test coverage for each architecture layer: domain (model construction, repository contract), data (entity mapping, DAO queries, repository impl), and presentation (ViewModel state transitions, PresentationMapper output validation)
- do not generate a contract with `usesTimeProvider: false` if the feature involves current date, logical month, or any time-derived filter — those are time-sensitive by nature
- timestamp fields must be named by meaning and unit, such as `createdAtMillis`, `occurredAtMillis`, or `dueAtMillis`; avoid ambiguous names like `date`, `dateTime`, or `dateMillis`
- choose entity field names before implementation and treat them as stable contract requirements

Output requirements:
- output valid YAML only
- no markdown fences
- no explanation

Minimum shape:
feature: <feature name>
goal: <feature goal>
entities:
  - name: <entity>
    fields:
      - name: <field name>
        type: <field type>
        required: <true|false>
actions:
  - name: <action>
    inputs:
      - <input name and type, or none>
screens:
  - <screen>
states:
  - loading
  - empty
  - content
  - error
timeSensitiveBehavior:
  usesTimeProvider: <true|false>
  notes: <required when true; must describe what time-sensitive behavior exists and why TimeProvider is needed>
designExpectations:
  loading: <expected loading treatment (e.g., centered spinner with status message)>
  empty: <expected empty-state message and suggested user action>
  content: <expected primary content structure and layout hierarchy>
  error: <expected error message format and recovery treatment (e.g., retry button)>
tests:
  - domain: <list of domain-layer tests required: model construction, enum values, repository interface contract>
  - data: <list of data-layer tests required: entity-to-domain mapping, DAO queries, repository implementation>
  - presentation: <list of presentation-layer tests required: ViewModel state transitions, action handling, event emission>

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
