# Dream Rules Index

Maps topics to full best-practice files. Use this to find specific rules quickly.

## By Layer

| Topic | File | Key Rules |
|-------|------|-----------|
| Architecture | `03-core-architectural-philosophy.md` | Feature-based modules, dependency direction |
| Feature Template | `05-feature-module-template.md` | domain/data/api/impl split, DI boilerplate |
| Dependency Rules | `06-dependency-rules-to-keep.md` | Strict direction, aggregator exception |
| Domain | `09-domain-layer-practices.md` | Pure Kotlin, typed results, TimeProvider |
| Data | `10-data-layer-practices.md` | Repositories, safeCall, cache-first, mappers |
| Presentation | `14-presentation-layer-practices.md` | MVI, state/actions/events, Screen/Content |
| Navigation | `15-navigation-practices.md` | NavKey, Navigator, Koin entries |
| Resources | `17-resource-and-localization-practices.md` | Shared strings, no hardcoded text |
| DI | `18-dependency-injection-practices.md` | Koin compiler plugin DSL, startKoin |
| Composition Root | `30-stable-composition-root-practices.md` | Stable factories, no recomposing assembly |
| Testing | `23-testing-oriented-practices-to-reuse.md` | FakeTimeProvider, Mokkery, shared test utils |

## By Concern

| Topic | File | Key Rules |
|-------|------|-----------|
| Build System | `07-build-system-practices.md` | Convention plugins, narrow scope |
| Shared Core Split | `08-shared-core-splitting.md` | Split by responsibility |
| Offline-First | `11-offline-first-storage-practices.md` | Cache strategy |
| Settings/Preferences | `12-settings-and-preference-storage.md` | Repository boundary mapping |
| Time/Date | `13-time-and-logical-date-practices.md` | TimeProvider API |
| Design System | `16-design-system-practices.md` | Shared tokens, components |
| Platform Boundary | `19-android-and-ios-boundary-practices.md` | Share logic, native UI |
| Platform Capabilities | `20-platform-specific-capability-patterns.md` | Contracts, DI injection |
| Screen Composition | `21-screen-composition-practices.md` | Screen/Content split |
| Error Handling | `22-error-handling-and-fallback-practices.md` | Typed errors |
| Documentation | `24-documentation-practices.md` | README, inline docs |
| Defaults | `25-recommended-default-rules-for-future-projects.md` | Recommended defaults |
| Anti-Patterns | `26-anti-patterns-to-avoid-when-reusing-this-architecture.md` | Things to avoid |
| Refinements | `27-open-refinements-for-this-handbook.md` | Open items |
| Summary | `28-short-summary.md` | Quick overview |
| App Identity | `29-package-and-app-identity-practices.md` | Package naming |
| UI Quality | `31-ui-quality-and-state-design-practices.md` | State design |
| Feature Boundary | `32-feature-boundary-and-reuse-practices.md` | Cross-feature rules |
| Forms/IME | `33-responsive-form-and-ime-practices.md` | Keyboard handling |
| Maestro | `34-maestro-smoke-verification-practices.md` | Smoke flows |

## By Platform

| Topic | File | Key Rules |
|-------|------|-----------|
| iOS Structure | `35-ios-project-structure-practices.md` | SwiftUI host layout |
| iOS Bridge | `36-ios-kmp-bridge-practices.md` | Swift↔Kotlin boundary |
| iOS Xcode | `37-ios-xcode-configuration-practices.md` | module.xcodeproj, deployment target |
| iOS Testing | `38-ios-testing-practices.md` | Single-app-target limitation, Kotlin iOS tests |
| iOS Strings | `39-ios-string-localization-practices.md` | xcstrings sync |

## Quick Lookup

**"How do I structure a feature?"** → `05-feature-module-template.md`
**"What are the dependency rules?"** → `06-dependency-rules-to-keep.md`
**"How do I use TimeProvider?"** → `13-time-and-logical-date-practices.md`
**"How do I set up Koin?"** → `18-dependency-injection-practices.md`
**"How do I structure Navigation?"** → `15-navigation-practices.md`
**"How do I set up iOS bridge?"** → `36-ios-kmp-bridge-practices.md`
**"What should I avoid?"** → `26-anti-patterns-to-avoid-when-reusing-this-architecture.md`
**"How do I write tests?"** → `23-testing-oriented-practices-to-reuse.md`
