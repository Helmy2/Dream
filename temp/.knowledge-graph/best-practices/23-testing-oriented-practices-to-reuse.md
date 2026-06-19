# 23. Testing-Oriented Practices To Reuse

Even without reviewing the full test suite, the architecture already exposes several good testing practices.

### 22.1 Inject time

This is the single highest-value testing rule in the app.

### 22.2 Keep domain pure

Pure domain modules are easier to unit test and easier to port.

### 22.3 Keep repository boundaries explicit

Typed results and repository interfaces reduce mocking complexity.

### 22.4 Prefer flows for observable state sources

Flows make settings, logs, and derived state easier to observe consistently in both production and tests.

### 22.5 Keep shared test utilities in shared/core/testing

Reusable fake providers and coroutine-aware helpers should live in a shared testing module instead of being recreated in each feature.

Best practice:

- keep fake time and dispatcher providers in `shared/core/testing`
- add coroutine test dependencies once in the shared testing module and reuse them from feature tests
- test feature behavior at the feature layer and queue semantics at the shared presentation layer
