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

# --- 1. Feature module ---
section "1. Feature module"
for m in domain data api impl; do
  delete_path "feature/sample-$m"
done

# --- 2. Shared core data sample files ---
section "2. Shared core data (sample files)"
delete_path "shared/core-data/src/com/dream/template/shared/core/data/local/SampleDatabase.kt"
delete_path "shared/core-data/src/com/dream/template/shared/core/data/local/SampleItemDao.kt"
delete_path "shared/core-data/src/com/dream/template/shared/core/data/local/SampleItemEntity.kt"
delete_path "shared/core-data/src@android/com/dream/template/shared/core/data/local/SampleDatabaseFactory.android.kt"
delete_path "shared/core-data/src@ios/com/dream/template/shared/core/data/local/SampleDatabaseFactory.ios.kt"
delete_path "shared/core-data/schemas/com.dream.template.shared.core.data.local.SampleDatabase"

# --- 3. Umbrella core sample files ---
section "3. Umbrella core (sample files)"
delete_path "shared/umbrella-core/src/com/dream/template/shared/di/SampleRepositoryExtensions.kt"
delete_path "shared/umbrella-core/src@ios/com/dream/template/shared/di/SampleRepositoryFactory.kt"
delete_path "shared/umbrella-core/test/com/dream/template/shared/di/SampleDataModuleTest.kt"

# --- 4. iOS host sample files ---
section "4. iOS host (sample files)"
delete_path "iosApp/src/Sample"
delete_path "iosApp/test/SampleListViewModelTests.swift"
delete_path "iosApp/test/SampleDetailViewModelTests.swift"
delete_path "iosApp/test/MockSampleRepositoryBridge.swift"

# --- 5. Maestro sample flows ---
section "5. Maestro smoke flows"
delete_path "maestro/features/sample"

# --- 6. project.yaml module entries ---
section "6. project.yaml module entries"
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

# --- 7. Umbrella module.yaml sample dependencies ---
section "7. Umbrella module.yaml files (sample deps)"
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

# --- 8. CoreModule.kt sample imports and DI ---
section "8. CoreModule.kt (sample DI)"
CORE_MODULE="$PROJECT_ROOT/shared/umbrella-core/src/com/dream/template/shared/di/CoreModule.kt"
if [ -f "$CORE_MODULE" ] && grep -q "sample\|Sample" "$CORE_MODULE"; then
  if $DRY_RUN; then
    printf '  would strip sample imports and DI from CoreModule.kt\n'
  else
    sed -i '' '/import com.dream.template.feature.sample/d' "$CORE_MODULE"
    sed -i '' '/import com.dream.template.shared.core.data.local.SampleDatabase$/d' "$CORE_MODULE"
    sed -i '' '/import com.dream.template.shared.core.data.local.SampleDatabaseFactory/d' "$CORE_MODULE"
    sed -i '' '/import com.dream.template.shared.core.data.local.SampleItemDao/d' "$CORE_MODULE"
    sed -i '' '/import com.dream.template.shared.core.data.local.createSampleDatabase/d' "$CORE_MODULE"
    sed -i '' '/sampleDatabaseFactory/d' "$CORE_MODULE"
    sed -i '' '/SampleDatabase/d' "$CORE_MODULE"
    sed -i '' '/SampleItemDao/d' "$CORE_MODULE"
    sed -i '' '/RoomSampleRepository/d' "$CORE_MODULE"
    sed -i '' '/SampleRepository/d' "$CORE_MODULE"
    sed -i '' '/sampleDataModule/d' "$CORE_MODULE"
    printf '  stripped sample imports and DI from CoreModule.kt\n'
  fi
  deleted=$((deleted + 1))
else
  skipped=$((skipped + 1))
fi

# --- 9. UiModule.kt sample imports ---
section "9. UiModule.kt (sample imports)"
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

# --- 10. Host app entry points ---
section "10. Host app entry points"
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
check_leftover "iosSampleRepository|SampleRepositoryExtensions" "umbrella-core"
check_leftover "Sample" "iosApp"

if [ "$leftovers" -eq 0 ]; then
  printf '  No leftover sample references found.\n'
else
  printf '\n  Found %d categories with leftovers. Review and clean up manually.\n' "$leftovers"
fi

# --- Next steps ---
section "Next Steps (manual edits required)"
printf '  1.  Update CoreModule.kt: add new feature DI module, fix function name and params\n'
printf '  2.  Update UiModule.kt: replace SampleRoute, swap module refs, fix initKoin() signature\n'
printf '  3.  Update MainActivity.kt: pass new feature database factory to initKoin()\n'
printf '  4.  Update umbrella-core/module.yaml: add new feature deps (exported)\n'
printf '  5.  Update umbrella-ui/module.yaml: add new feature deps\n'
printf '  6.  Update ContentView.swift: replace SampleListView()\n'
printf '  7.  Update AppDependencies.swift: replace bridge type\n'
printf '  8.  Rewrite SharedCoreBridge.swift for the new feature (import KotlinModules) and add Swift tests under iosApp/test\n'
printf '  9.  Update shared/core-strings/composeResources and iosApp/src/Localizable.xcstrings: replace string keys\n'
printf '  10. Add new modules to project.yaml (new Swift files in iosApp/src need no project registration)\n'
printf '  11. Update AGENTS.md, README.md, maestro/README.md\n'
printf '  12. Run verification: ./kotlin build && ./kotlin test --platform=android && ./kotlin build -m androidApp --platform=android\n'
