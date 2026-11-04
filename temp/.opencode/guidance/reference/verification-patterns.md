# Verification Patterns

This file tracks common verification failure patterns across Dream features. Use this to proactively avoid failures during planning and implementation.

## Purpose

- Track recurring verification failures
- Identify patterns in failure types
- Provide mitigation strategies
- Feed insights into planning phase

## Pattern Format

```yaml
patterns:
  - type: <failure-type>
    frequency: <count>
    last_seen: <YYYY-MM-DD>
    mitigation: <concrete-action>
    affected_phases: [<phase-list>]
    symptoms: [<observable-signs>]
```

## Common Patterns

### DI Resolution Failures

```yaml
- type: di-resolution
  frequency: 0
  last_seen: N/A
  mitigation: Always verify Koin module registration in Phase 2 and Phase 4
  affected_phases: [Phase 2, Phase 4]
  symptoms:
    - "No definition found for type"
    - "KoinComponent injection failed"
    - "ViewModel creation error"
```

### iOS Bridge Export Missing

```yaml
- type: ios-bridge-export
  frequency: 0
  last_seen: N/A
  mitigation: Ensure the module is a dependency of `umbrella-core` so it is exported in `KotlinModules`
  affected_phases: [Phase 3]
  symptoms:
    - "Cannot find type in scope"
    - "Undefined symbol"
    - "Module not found"
```

### String Resource Missing

```yaml
- type: string-resource-missing
  frequency: 0
  last_seen: N/A
  mitigation: Define all strings in strings.xml before writing UI (Phase 2)
  affected_phases: [Phase 2, Phase 3]
  symptoms:
    - "Resource not found"
    - "MissingResourceException"
    - "String key not defined"
```

### Navigation Entry Not Registered

```yaml
- type: navigation-entry-missing
  frequency: 0
  last_seen: N/A
  mitigation: Verify Koin navigation entries in Phase 4 (umbrella wiring)
  affected_phases: [Phase 3, Phase 4]
  symptoms:
    - "No navigation entry for route"
    - "Screen not found in NavDisplay"
    - "Navigator.goTo() fails"
```

### Test Mock Missing

```yaml
- type: test-mock-missing
  frequency: 0
  last_seen: N/A
  mitigation: Use Mokkery for all repository/interface mocks in tests
  affected_phases: [Phase 2, Phase 5]
  symptoms:
    - "Unresolved reference" in test files
    - "Mock not configured"
    - "NullPointerException in test"
```

## How to Use

### During Planning (Phase 0)

1. Read this file before creating plan
2. Check if current feature type matches any pattern
3. Add mitigation steps to plan if pattern matches
4. Example: "DI resolution failures are common → add explicit Koin verification step in Phase 2"

### During Implementation

1. If you encounter a failure, check if it matches a pattern
2. Apply the mitigation strategy
3. If it's a new pattern, add it to this file after resolution

### During Verification (Phase 5)

1. After verification completes, update pattern frequencies
2. Add new patterns if discovered
3. Update `last_seen` dates

## Pattern Maintenance

- Review patterns monthly
- Remove patterns with frequency < 2 and last_seen > 6 months ago
- Promote high-frequency patterns to "Common Pitfalls" section in operating guide
