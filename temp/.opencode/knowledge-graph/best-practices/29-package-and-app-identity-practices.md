# 29. Package And App Identity Practices

Dream-generated apps should normalize identity early and consistently.

### 29.1 Derive one canonical app identity first

Before renaming project files or package roots, derive a canonical identity that includes:

- app display name
- canonical package root
- Android application id (`androidApp/module.yaml`)
- Android namespace values (each `module.yaml`)
- iOS bundle id (`PRODUCT_BUNDLE_IDENTIFIER` in `iosApp/module.xcodeproj`) and `CFBundleDisplayName` in `iosApp/src/Info.plist`

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

- `project.yaml` / `module.yaml` module names and namespace values
- Android application id values (`androidApp/module.yaml`)
- Kotlin package declarations and package directories where renamed
- Android manifest labels or other visible app-name surfaces
- iOS bundle id and display name (`module.xcodeproj`, `Info.plist`)
- `specs/` metadata and documentation values that still mention template identity
- obvious sample or template-facing display strings

### 29.4 Do not confuse sample scaffolding with final product identity

Temporary sample scaffolding may survive briefly for structural reasons, but sample naming should not leak into the generated app identity.

Reusable principle:

- keep structural scaffolding decisions separate from app identity decisions
- record any temporary sample module retention explicitly
- remove sample-domain naming as soon as the first real feature is generated

### 29.5 Keep feature paths and packages aligned

Feature module names (= directory names), `module.yaml` references, and Kotlin package declarations should tell the same story.

Reusable principle:

- choose one multi-word feature convention before generation writes code
- module names must be unique and equal the directory name, so use a slug such as `expense-details-impl` consistently in directories, `project.yaml`, and `//feature/...` dependency paths
- keep Kotlin packages (`feature.expense.details`) independent of directory names and document the mapping
- do not leave reviewers to mentally translate between slugs and packages without an explicit convention
