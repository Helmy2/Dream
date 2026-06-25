# 24. Documentation Practices

Strong process documentation adds long-term value.

### 23.1 Keep architecture docs in the repo

Reusable rule:

- keep living technical documents beside the code

### 23.2 Let docs define non-negotiable rules

Good architecture docs do more than explain; they enforce constraints such as:

- no direct `Clock.System` outside the dedicated production time provider
- no hardcoded UI strings
- clear navigation ownership rules
- strict layer separation

That is a good model. Documentation should define enforceable norms, not only aspirations.
