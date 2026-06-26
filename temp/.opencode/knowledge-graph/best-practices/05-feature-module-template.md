# 5. Feature Module Template

The canonical feature shape in Dream is:

```text
feature/<name>/
  domain/
  data/
  api/
  impl/
```

Multi-word features must use one consistent naming convention across folders, Gradle module names, and Kotlin packages. Prefer a folder path that mirrors package segments for multi-word concepts, such as `feature/expense/details/...` with `com.example.feature.expense.details...`, or use a single slug consistently everywhere. Do not mix a hyphenated folder such as `feature/expense-details` with package segments such as `feature.expense.details` unless a documented migration reason exists.

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
- DataStore (setup lives in `shared:core:data` with expect/actual)

### 4.2 Data module responsibilities

`data` should contain:

- repository implementations
- DAO usage
- DTOs
- mappers
- remote calls
- local caching logic
- exception-to-domain-error mapping

### 4.3 API module responsibilities

`api` defines the public surface of the feature. Other modules navigate to this feature through its `api` without knowing anything about its implementation.

`api` should contain:

- feature `NavKey` route types (e.g. `data object SampleRoute : NavKey`)
- any other minimal public presentation contracts needed by the app shell or other features

`api` should not contain:

- screens or composables
- ViewModels
- Koin module definitions
- Room, Ktor, or data-layer code

Dependency rule: any module that needs to navigate to this feature depends on `feature/<name>/api`, never on `feature/<name>/impl`.

### 4.4 Impl module responsibilities

`impl` provides the full feature implementation. Nothing outside this feature should depend on it directly.

`impl` should contain:

- screens and content composables
- UI models: state, action, event, item, content
- mapper classes that map domain models into UI models
- ViewModels
- Koin module that registers the ViewModel and installs Navigation 3 entry providers via `navigation<Route> { FeatureScreen() }`
- platform UI effects only when needed

Presentation mapper rule:

- map from domain models to UI models inside the impl layer
- implement presentation mappers as classes, not extension functions
- keep mapping logic explicit so it is easy to inject, test, replace, and review

`impl` depends on:

- `feature/<name>/api`
- `feature/<name>/domain`
- shared presentation helpers and design system

### 4.5 Why this template scales

This split keeps the public navigation contract stable and small. Other features and the app shell depend only on `api`. The `impl` can be replaced, refactored, or tested in isolation without affecting callers. Review discussions are clearer because every class has an obvious home and cross-feature coupling is easy to detect from the dependency graph.

### 4.6 Feature DI Module boilerplate

Every feature `impl` module needs a Koin module that registers ViewModels and installs Navigation 3 entry providers. The pattern is identical across features:

```kotlin
// feature/<name>/impl/src/commonMain/kotlin/.../di/<Name>Module.kt
@OptIn(KoinExperimentalAPI::class)
val <name>UiModule = module {
    factory<PresentationMapper> { PresentationMapper(get()) }
    viewModel<FeatureViewModel>()

    navigation<FeatureListRoute> {
        val navigator = koinInject<Navigator>()
        FeatureListScreen(
            onNavigateDetail = { id -> navigator.goTo(FeatureDetailRoute(id)) },
        )
    }

    navigation<FeatureDetailRoute> { route ->
        val navigator = koinInject<Navigator>()
        FeatureDetailScreen(
            selectedId = route.id,
            onNavigateBack = { navigator.goBack() },
        )
    }
}
```

Registration steps for a new feature:

1. Create the DI module file in `feature/<name>/impl/.../di/`
2. Add `feature/<name>/impl` dependency to `shared/umbrella-ui/build.gradle.kts`:
   ```kotlin
   commonMainImplementation(projects.feature.name.impl)
   commonMainApi(projects.feature.name.api)
   ```
3. Import and add `<name>UiModule` to the `modules(...)` call in `UiModule.kt`
4. Verify with `./gradlew :shared:umbrella-ui:allTests` — the DI graph test confirms all registrations resolve without eager instantiation
