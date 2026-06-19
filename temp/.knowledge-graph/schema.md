# Knowledge Graph Schema

## Goal

Represent architecture guidance as a structured rule graph the AI can query during generation, review, and repair.

## Source Model

Every fact in the graph must trace back to one or more source ranges in a repository document.

Source metadata shape:

```json
{
  "file": ".knowledge-graph/best-practices/03-core-architectural-philosophy.md",
  "startLine": 15,
  "endLine": 19
}
```

## Node Types

### Document

Represents a source document.

Required fields:

- `id`
- `type`
- `title`
- `path`

### Section

Represents a section within a source document.

Required fields:

- `id`
- `type`
- `title`
- `sectionNumber`
- `documentId`
- `source`

### Layer

Represents an architectural layer or shared area.

Examples:

- `domain`
- `data`
- `presentation`
- `navigation`
- `design_system`
- `shared_core`

Required fields:

- `id`
- `type`
- `name`
- `description`

### ModulePattern

Represents a reusable module or folder pattern.

Examples:

- `feature/<name>/domain`
- `feature/<name>/data`
- `feature/<name>/presentation`

Required fields:

- `id`
- `type`
- `name`
- `pattern`
- `source`

### Practice

Represents a reusable practice or pattern.

Required fields:

- `id`
- `type`
- `name`
- `statement`
- `classification`
- `strength`
- `source`

### Rule

Represents an actionable constraint.

Required fields:

- `id`
- `type`
- `name`
- `statement`
- `scope`
- `classification`
- `strength`
- `source`

### AntiPattern

Represents a forbidden or risky pattern.

Required fields:

- `id`
- `type`
- `name`
- `statement`
- `replacementGuidance`
- `classification`
- `strength`
- `source`

### Exception

Represents a narrowly allowed exception.

Required fields:

- `id`
- `type`
- `name`
- `statement`
- `source`

### PlatformPattern

Represents platform-specific guidance.

Examples:

- `shared_business_logic`
- `native_ios_ui`
- `platform_capability_contracts`

Required fields:

- `id`
- `type`
- `name`
- `statement`
- `classification`
- `strength`
- `source`

## Edge Types

### Structural

- `CONTAINS`
- `DERIVED_FROM`
- `APPLIES_TO`
- `EXPLAINED_BY`

### Constraints

- `DEPENDS_ON`
- `MUST_NOT_DEPEND_ON`
- `REQUIRES`
- `FORBIDS`
- `OWNS`

### Guidance

- `PREFERRED_OVER`
- `CLASSIFIED_AS`
- `SUPPORTED_BY`
- `EXCEPTION_TO`
- `REPLACED_BY`

Edge shape:

```json
{
  "from": "layer.presentation",
  "type": "DEPENDS_ON",
  "to": "layer.domain",
  "source": {
    "file": ".knowledge-graph/best-practices/03-core-architectural-philosophy.md",
    "startLine": 15,
    "endLine": 19
  }
}
```

## Classification Values

- `universal`
- `kmp_oriented`
- `project_specific`

## Strength Values

- `non_negotiable`
- `recommended`
- `default`
- `caution`

## Extraction Rules

- convert "must" and explicit prohibitions into `Rule` or `AntiPattern`
- convert stable reusable advice into `Practice`
- convert narrow allowed deviations into `Exception`
- create edges for dependency direction, ownership, and forbidden coupling
- prefer normalized statements over copied paragraphs
- keep wording implementation-friendly so the AI can evaluate generated code against the graph
