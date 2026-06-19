# 5. Feature Module Template

The most reusable module shape in this repository is:

```text
feature/<name>/
  domain/
  data/
  presentation/
```

### 4.1 Domain module responsibilities

`domain` should contain:

- domain models
- repository interfaces
- use cases
- domain-specific errors
- pure business rules

`domain` should not contain:

- Compose
- Android APIs
- iOS APIs
- Room annotations
- Ktor clients
- DataStore

### 4.2 Data module responsibilities

`data` should contain:

- repository implementations
- DAO usage
- DTOs
- mappers
- remote calls
- local caching logic
- exception-to-domain-error mapping

### 4.3 Presentation module responsibilities

`presentation` should contain:

- screens
- composables
- UI models such as state, action, event, item, and content models
- mapper classes that map domain models into UI models
- view models
- platform UI effects only when needed

Presentation mapper rule:

- map from domain models to UI models inside the presentation layer
- implement presentation mappers as classes, not extension functions
- keep mapping logic explicit so it is easy to inject, test, replace, and review

### 4.4 Why this template scales

This split reduces ambiguity. Every new class has an obvious home, and review discussions are clearer because people argue about layer placement using fixed rules instead of taste.
