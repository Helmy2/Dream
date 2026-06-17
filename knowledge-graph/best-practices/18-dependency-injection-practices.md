# 18. Dependency Injection Practices

This approach uses Koin with a composition-root mindset.

### 17.1 DI starts at the top

Best practice:

- call `startKoin` only from top-level app initialization paths
- let modules contribute registrations, but do not let features bootstrap DI themselves

### 17.2 Split DI by layer and platform

Good reusable pattern:

- shared modules provide shared registrations
- Android and iOS provide platform implementations through dedicated modules
- composition roots assemble everything together

### 17.3 Constructor injection everywhere possible

Most of the codebase follows constructor injection for repositories, use cases, and view models.

That remains the cleanest default.

### 17.4 iOS bridge injection is explicit

`initializeKoin(...)` accepts Swift-provided implementations such as:

- encryptor
- location provider
- notification provider

Best practice:

- inject platform services through shared interfaces instead of reaching across boundaries directly
