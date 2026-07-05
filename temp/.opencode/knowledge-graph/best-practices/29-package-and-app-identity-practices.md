# 29. Package And App Identity Practices

Dream-generated apps should normalize identity early and consistently.

### 29.1 Derive one canonical app identity first

Before renaming project files or package roots, derive a canonical identity that includes:

- app display name
- canonical package root
- Android application id
- Android namespace values
- iOS bundle or visible app identity where applicable
- Gradle root project name

Generated apps should not rename these surfaces independently from one another.
They should all flow from the same canonical identity decision.

### 29.2 Normalize package-defining surfaces immediately

Package roots, namespaces, and app ids should be normalized during project instantiation unless a concrete blocker is documented.

Reusable principle:

- do not leave template identity in package roots or build identity values after instantiation
- prefer deterministic package roots derived from the app identity instead of ad hoc replacements
- document any intentionally deferred rename with the exact follow-up step

### 29.3 Audit all identity-bearing surfaces explicitly

Dream should record a rename audit for the generated project.

At minimum, the audit should cover:

- Gradle root project name
- module namespace values
- Android application id values
- Kotlin package declarations and package directories where renamed
- Android manifest labels or other visible app-name surfaces
- iOS configuration values that carry app identity
- `features/` metadata and documentation values that still mention template identity
- obvious sample or template-facing display strings

### 29.4 Do not confuse sample scaffolding with final product identity

Temporary sample scaffolding may survive briefly for structural reasons, but sample naming should not leak into the generated app identity.

Reusable principle:

- keep structural scaffolding decisions separate from app identity decisions
- record any temporary sample module retention explicitly
- remove sample-domain naming as soon as the first real feature is generated

### 29.5 Keep feature paths and packages aligned

Feature directory names, Gradle module paths, and Kotlin package declarations should tell the same story.

Reusable principle:

- choose one multi-word feature convention before generation writes code
- prefer package-mirroring directories for nested concepts, such as `feature/expense/details` with `feature.expense.details`
- if using a slug such as `expense-details`, use the same slug consistently in generated module names and documentation
- do not leave reviewers to mentally translate between `expense-details` folders and `expense.details` packages without an explicit convention
