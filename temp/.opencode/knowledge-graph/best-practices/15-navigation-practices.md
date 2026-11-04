# 15. Navigation Practices

A centralized Navigation 3 structure is one of the clearest reusable patterns in this style of app.

### 15.1 Central app shell

A root app shell acts as the single structural entry point for the Android Compose UI (iOS navigation is native SwiftUI).

Best practice:

- keep app-wide navigation, theme application, and safe-area handling at the shell level
- keep feature screens unaware of the full routing graph when possible

### 15.2 Navigation 3 typed routes

Routes are modeled as typed values implementing `androidx.navigation3.runtime.NavKey`, such as `SampleRoute : NavKey`.

Concrete route types belong to the feature `api` module. Other modules that need to navigate to this feature depend on `feature/<name>-api` and reference the route type there. Shared navigation depends only on `NavKey`, not on a shared sealed hierarchy containing feature-specific destinations. Do not put feature-specific routes in shared core unless they are genuinely shared across multiple features.

Why this is good:

- callers depend on the public API surface, not the implementation details
- `impl` can be replaced or refactored without changing callers
- exhaustive route handling
- less stringly-typed navigation
- a direct path to serializable route keys when destinations need arguments or saved back stacks

> **See also:** [18.5 Feature impl installs Navigation 3 entry providers](18-dependency-injection-practices.md) for the Koin navigation entry pattern.

### 15.3 Navigator owns the back stack

Shared navigation should expose a small `Navigator` with a mutable back stack and intent-level operations such as `goTo()` and `goBack()`.

Best practice:

- keep the back stack in a stable shared navigation owner
- render it with Navigation 3 `NavDisplay`
- make back behavior a product rule instead of an accidental side effect of navigation defaults

### 15.4 Koin navigation entry providers live in impl

The feature `impl` module installs Koin Navigation 3 entry providers using `navigation<Route> { FeatureScreen() }`.

Best practice:

- keep route-to-screen wiring in the feature `impl` Koin module, not in shared core or the app shell
- keep feature `api` free of Koin and Compose dependencies
- retrieve DI-managed screen dependencies at the navigation entry boundary inside `impl`
- the app shell composition root (`shared/umbrella-ui`, Android-only) imports feature `impl` Koin modules so their navigation installers are registered before `NavDisplay` renders

### 15.5 Dependency direction for navigation

```text
other modules  -->  feature/<name>-api  (route types only)
feature/<name>-impl  -->  feature/<name>-api  (installs navigation entry)
shared/umbrella-ui  -->  feature/<name>-impl  (aggregates Koin navigation installers)
shared/navigation  -->  NavKey  (no feature-specific types)
```

### 15.6 Differentiate top-level tabs from push destinations

A well-structured app should clearly separate:

- top-level routes with persistent navigation chrome
- push destinations without persistent navigation chrome

This app-shell rule improves consistency as the graph grows.

### 15.7 Navigation 3 setup reference

**Dependencies** (in `shared/navigation/module.yaml`, an Android-only Compose module):
```yaml
product:
  type: kmp/lib
  platforms: [android]
dependencies:
  - $libs.androidx.navigation3.runtime: exported
  - $libs.androidx.navigation3.ui: exported
  - $libs.androidx.navigation3.koin: exported
```

**Route definition** (in `feature/<name>-api`):
```kotlin
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SampleListRoute : NavKey

@Serializable
data class SampleDetailRoute(val id: String) : NavKey
```

**Navigator** (in `shared/navigation`):
```kotlin
class Navigator {
    private val _backStack = mutableStateListOf<NavKey>(SampleListRoute)
    val backStack: List<NavKey> = _backStack

    fun goTo(route: NavKey) { _backStack.add(route) }
    fun goBack() { if (_backStack.size > 1) _backStack.removeLast() }
}
```

**NavDisplay** (in `shared/navigation` or app shell):
```kotlin
@Composable
fun AppNavigation(navigator: Navigator) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider {
            // Entry providers are installed by feature Koin modules
        }
    )
}
```

**Koin navigation entry** (in `feature/<name>-impl`):
```kotlin
@OptIn(KoinExperimentalAPI::class)
val sampleUiModule = module {
    navigation<SampleListRoute> {
        val navigator = koinInject<Navigator>()
        SampleListScreen(
            onNavigateDetail = { id -> navigator.goTo(SampleDetailRoute(id)) }
        )
    }
    navigation<SampleDetailRoute> { route ->
        val navigator = koinInject<Navigator>()
        SampleDetailScreen(
            sampleId = route.id,
            onNavigateBack = { navigator.goBack() }
        )
    }
}
```

Best practice:

- keep route types serializable for saved state support
- keep `NavKey` implementations as `data object` (no args) or `data class` (with args)
- install navigation entries in feature `impl` Koin modules, not in shared core
- aggregate all feature Koin modules in `shared/umbrella-ui` before `NavDisplay` renders

> **Reference:** [Android Navigation 3 documentation](https://developer.android.com/develop/ui/compose/navigation/navigation3)
