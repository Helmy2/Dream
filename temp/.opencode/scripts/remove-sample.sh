#!/usr/bin/env bash
set -euo pipefail

DRY_RUN=false
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

usage() {
  printf 'Usage: %s [--dry-run]\n' "$(basename "$0")"
  printf '  --dry-run  Show what would be deleted without making changes\n'
  exit 1
}

while [ $# -gt 0 ]; do
  case "$1" in
    --dry-run) DRY_RUN=true; shift ;;
    -h|--help) usage ;;
    *) printf 'Unknown option: %s\n' "$1"; usage ;;
  esac
done

deleted=0
skipped=0

delete_path() {
  local path="$1"
  local full_path="$PROJECT_ROOT/$path"
  if [ -e "$full_path" ]; then
    if $DRY_RUN; then
      printf '  would delete: %s\n' "$path"
    else
      rm -rf "$full_path"
      printf '  deleted: %s\n' "$path"
    fi
    deleted=$((deleted + 1))
  else
    skipped=$((skipped + 1))
  fi
}

section() {
  printf '\n%s\n' "$1"
}

printf '%s\n' "=== Sample Feature Removal ==="
if $DRY_RUN; then
  printf '%s\n' "Mode: DRY RUN (no changes will be made)"
fi

# --- 1. Feature module (includes the Room database, DI module, and iOS bridge helpers) ---
section "1. Feature module"
for m in domain data api impl; do
  delete_path "feature/sample-$m"
done
# DI composition test in umbrella-core that exercises sampleDataModule
delete_path "shared/umbrella-core/test/com/dream/template/shared/di/SampleDataModuleTest.kt"

# --- 2. iOS host sample files ---
section "2. iOS host (sample files)"
delete_path "iosApp/src/Sample"
delete_path "iosApp/test/SampleListViewModelTests.swift"
delete_path "iosApp/test/SampleDetailViewModelTests.swift"
delete_path "iosApp/test/MockSampleRepositoryBridge.swift"

# --- 3. Maestro sample flows ---
section "3. Maestro smoke flows"
delete_path "maestro/features/sample"

# --- 4. project.yaml module entries ---
section "4. project.yaml module entries"
PROJECT_FILE="$PROJECT_ROOT/project.yaml"
if [ -f "$PROJECT_FILE" ] && grep -q 'feature/sample-' "$PROJECT_FILE"; then
  if $DRY_RUN; then
    printf '  would remove feature/sample-* modules from project.yaml\n'
  else
    sed -i '' '/feature\/sample-/d' "$PROJECT_FILE"
    printf '  removed feature/sample-* modules from project.yaml\n'
  fi
  deleted=$((deleted + 1))
else
  skipped=$((skipped + 1))
fi

# --- 5. Umbrella module.yaml sample dependencies ---
section "5. Umbrella module.yaml files (sample deps)"
for module_file in \
  "shared/umbrella-core/module.yaml" \
  "shared/umbrella-ui/module.yaml"; do
  full="$PROJECT_ROOT/$module_file"
  if [ -f "$full" ] && grep -q "feature/sample-" "$full"; then
    if $DRY_RUN; then
      printf '  would remove sample deps from %s\n' "$module_file"
    else
      sed -i '' '/feature\/sample-/d' "$full"
      printf '  removed sample deps from %s\n' "$module_file"
    fi
    deleted=$((deleted + 1))
  else
    skipped=$((skipped + 1))
  fi
done

# --- 6. UiModule.kt sample imports ---
section "6. UiModule.kt (sample imports)"
UI_MODULE="$PROJECT_ROOT/shared/umbrella-ui/src/com/dream/template/shared/di/UiModule.kt"
if [ -f "$UI_MODULE" ] && grep -q "sample\|Sample" "$UI_MODULE"; then
  if $DRY_RUN; then
    printf '  would strip sample imports from UiModule.kt\n'
    printf '  NOTE: manual edits still needed for route and module refs\n'
  else
    sed -i '' '/import com.dream.template.feature.sample/d' "$UI_MODULE"
    sed -i '' '/import com.dream.template.shared.core.data.local.SampleDatabaseFactory/d' "$UI_MODULE"
    printf '  stripped sample imports from UiModule.kt\n'
    printf '  NOTE: UiModule.kt still needs manual edits:\n'
    printf '    - Replace SampleRoute with new feature route\n'
    printf '    - Replace sampleUiModule with new feature UI module\n'
    printf '    - Remove sampleDatabaseFactory parameter from initKoin()\n'
  fi
  deleted=$((deleted + 1))
else
  skipped=$((skipped + 1))
fi

# --- 7. Host app entry points ---
section "7. Host app entry points"
for host_file in \
  "androidApp/src/com/dream/template/android/MainActivity.kt"; do
  full="$PROJECT_ROOT/$host_file"
  if [ -f "$full" ] && grep -q "Sample" "$full"; then
    if $DRY_RUN; then
      printf '  would strip sample factory refs from %s\n' "$host_file"
      printf '  NOTE: manual edits still needed for new feature wiring\n'
    else
      sed -i '' '/AndroidSampleDatabaseFactory/d' "$full"
      sed -i '' '/IosSampleDatabaseFactory/d' "$full"
      sed -i '' '/sampleDatabaseFactory/d' "$full"
      printf '  stripped sample factory refs from %s\n' "$host_file"
      printf '  NOTE: %s still needs manual edits for new feature wiring\n' "$host_file"
    fi
    deleted=$((deleted + 1))
  else
    skipped=$((skipped + 1))
  fi
done

# --- Summary ---
section "Summary"
printf '  Deleted/would delete: %d items\n' "$deleted"
printf '  Skipped (not found): %d items\n' "$skipped"

# --- Verification greps ---
section "Verification: checking for leftover sample references"
leftovers=0

check_leftover() {
  local pattern="$1"
  local scope="$2"
  local matches
  matches=$(grep -rn "$pattern" "$PROJECT_ROOT" \
    --include='*.kt' --include='*.swift' \
    --include='*.xml' --include='*.json' --include='*.yaml' --include='*.xcstrings' \
    --include='*.md' \
    2>/dev/null | grep -v 'node_modules\|/build/\|\.opencode/' | head -20 || true)
  if [ -n "$matches" ]; then
    printf '  LEFTOVER in %s:\n' "$scope"
    printf '%s\n' "$matches" | sed 's/^/    /'
    leftovers=$((leftovers + 1))
  fi
}

check_leftover "feature[./]sample" "module/project files"
check_leftover "SampleDatabase|SampleItemDao|SampleItemEntity|SampleDatabaseFactory" "Kotlin sources"
check_leftover "sampleDataModule|sampleUiModule|sampleDatabaseFactory" "DI wiring"
check_leftover '"sample_' "string resources"
check_leftover "dream_sample" "database config"
check_leftover "iosSampleRepository|SampleRepositoryExtensions" "iOS bridge helpers"
check_leftover "Sample" "iosApp"

if [ "$leftovers" -eq 0 ]; then
  printf '  No leftover sample references found.\n'
else
  printf '\n  Found %d categories with leftovers. Review and clean up manually.\n' "$leftovers"
fi

# --- Next steps ---
section "Next Steps (manual edits required)"
printf '  1.  Update UiModule.kt: replace SampleRoute, swap in the new feature data/UI modules, fix initKoin() signature\n'
printf '  2.  Update MainActivity.kt: pass the new feature database factory to initKoin()\n'
printf '  3.  Update umbrella-core/module.yaml: add new feature deps (exported)\n'
printf '  4.  Update umbrella-ui/module.yaml: add new feature deps\n'
printf '  5.  Update ContentView.swift: replace SampleListView()\n'
printf '  6.  Update AppDependencies.swift: replace bridge type\n'
printf '  7.  Add the new feature bridge under iosApp/src/<Feature>/Bridge/ (import KotlinModules) and Swift tests under iosApp/test\n'
printf '  8.  Update shared/core-strings/composeResources and iosApp/src/Localizable.xcstrings: replace string keys\n'
printf '  9.  Add new modules to project.yaml (new Swift files in iosApp/src need no project registration)\n'
printf '  10. Update AGENTS.md, README.md, maestro/README.md\n'
printf '  11. Run verification: ./kotlin build && ./kotlin test --platform=android && ./kotlin build -m androidApp --platform=android\n'
