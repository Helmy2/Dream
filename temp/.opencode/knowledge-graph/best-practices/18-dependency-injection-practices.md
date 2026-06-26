# 18. Dependency Injection Practices

This approach uses Koin with a composition-root mindset.

Dream-generated modules should use the Koin compiler plugin with Koin DSL registrations for compile-time safety. Do not use annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan` in generated feature code unless the user explicitly requests annotation style.

### 17.1 DI starts at the top

Best practice:

- call `startKoin` only from top-level app initialization paths
- on Android, initialize Koin in a custom `Application.onCreate()`, not in `MainActivity.onCreate()` — this ensures DI is available before any Activity, Service, or BroadcastReceiver starts
- expose a shared guarded startup helper for host apps so repeated Android, iOS, Desktop, preview, or test entry points do not accidentally start Koin twice
- let modules contribute registrations, but do not let features bootstrap DI themselves

Rationale for `Application` over `Activity`: navigation graph resolution, Koin ViewModel injection, and early-lifecycle hooks all depend on the Koin container being available before the first Activity's `onCreate`. Starting Koin in an Activity causes non-deterministic failures when navigation is resolved during Activity construction or when a custom `ContentProvider` (e.g., WorkManager init) needs DI before the Activity exists.

### 17.2 Split DI by layer and platform

Good reusable pattern:

- non-Compose shared modules provide domain/data registrations
- feature `impl` modules provide ViewModel and Navigation 3 entry-provider registrations
- feature `api` modules contain no Koin definitions; they are pure public contracts
- Android, iOS, and contract-selected Desktop provide product runtime platform factories or implementations through dedicated startup paths
- composition roots assemble everything together without making exported core modules depend on Compose

### 17.3 Constructor injection everywhere possible

Most of the codebase follows constructor injection for repositories, use cases, and view models.

That remains the cleanest default.

### 17.4 Prefer DI-managed ViewModel creation

If a screen state holder is a lifecycle `ViewModel`, register it with the DI container through the dedicated view-model DSL instead of a plain factory.

Best practice:

- use Koin's `viewModel` or `viewModelOf` DSL for shared view models inside the feature `impl` Koin module
- let Compose retrieve view models through `koinViewModel()` instead of building them from `rememberCoroutineScope()`
- keep runtime parameters explicit and rare
- keep ViewModel and Navigation 3 entry-provider modules out of SwiftUI-facing `umbrella-core` modules
- use compiler-plugin DSL imports from `org.koin.plugin.module.dsl.*` when using compile-safe registrations such as `viewModel<FeatureViewModel>()`
- do not use annotation-based DI such as `@KoinViewModel`, `@Module`, or `@ComponentScan` unless the user explicitly requests annotation style

### 17.5 Feature impl installs Navigation 3 entry providers

The feature `impl` Koin module uses the Navigation 3 DSL to register entry provider installers for the feature's screens.

Best practice:

- use `navigation<Route> { FeatureScreen() }` inside the `impl` module's Koin module
- the route type comes from `feature/<name>/api`; the screen comes from `feature/<name>/impl`
- `shared/umbrella-ui` imports feature `impl` Koin modules so all navigation installers are available before `NavDisplay` renders
- do not register navigation entry providers in shared core, app shells, or feature `api` modules

### 17.6 iOS bridge injection is explicit

`initializeKoin(...)` accepts Swift-provided implementations such as:

- encryptor
- location provider
- notification provider

Best practice:

- inject platform services through shared interfaces instead of reaching across boundaries directly
