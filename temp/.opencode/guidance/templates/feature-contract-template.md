# Feature Contract

## App Configuration

```yaml
app:
  name: ""
  package: ""
  iosUiStrategy: "swiftui"  # or "compose"
```

## Decisions

```yaml
decisions:
  storage: "room"  # or "datastore", "none"
  backend: false
  backend_base_url: ""
```

## Feature Definition

```yaml
feature:
  name: ""
  goal: ""
```

### Entities

```yaml
entities:
  - name: ""
    fields:
      - name: ""
        type: ""
        timestamp: false  # true for time fields
```

### Actions

```yaml
actions:
  - name: ""
    inputs: []
    description: ""
```

### Screens

```yaml
screens:
  - name: ""
    purpose: ""
    states:
      - Loading
      - Empty
      - Content
      - Error
```

### Time-Sensitive Behavior

```yaml
time_behavior:
  - screen: ""
    behavior: ""
    time_provider_method: ""  # nowMillis(), today(), currentYearMonth(), formatDate(), formatDateTime()
```

### Required Tests

```yaml
tests:
  domain:
    - ""
  data:
    - ""
  presentation:
    - ""
```

### Design Expectations

```yaml
design:
  - screen: ""
    state: ""
    expectation: ""
```
