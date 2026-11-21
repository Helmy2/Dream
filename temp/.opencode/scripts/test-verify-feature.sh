#!/usr/bin/env bash
# Self-test for verify-feature.sh. Copies the project to a scratch directory, applies the local-crud
# archetype verbatim (the starter itself has no features), breaks the result in specific ways, and checks
# that verify-feature.sh reports the expected check. Never modifies the real project.
#
# Usage: .opencode/scripts/test-verify-feature.sh [feature-name]   (default: sample)
# Needs rsync, perl and python3, and a TMPDIR outside any git repository. Runs verify-feature.sh with
# --quick, so no builds are started. The feature under test is the archetype's "sample".

set -u
SRC="$(cd "$(dirname "$0")/../.." && pwd)"
F="${1:-sample}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
W="$TMP/w"

PASS=0; FAIL=0
fresh() {
  rm -rf "$W"; mkdir -p "$W"
  rsync -a --exclude=build/ --exclude=.kotlin/ --exclude=node_modules/ --exclude=.git/ "$SRC/" "$W/"
  bash "$W/.opencode/archetypes/local-crud/apply-verbatim.sh" "$W" >/dev/null 2>&1 \
    || { echo "Could not apply the local-crud archetype to the scratch copy (is TMPDIR inside a git repository?)" >&2; exit 2; }
}
run() { (cd "$W" && bash .opencode/scripts/verify-feature.sh "$F" --quick 2>&1); }

# expect <description> <FAILED|warning|passed-only> <check id> <mutation shell run inside the scratch copy>
expect() {
  local desc="$1" level="$2" id="$3" mutation="$4" out
  fresh
  (cd "$W" && eval "$mutation") >/dev/null 2>&1
  out="$(run)"
  if [ "$level" = clean ]; then
    if ! printf '%s\n' "$out" | grep -q '^FAILED'; then PASS=$((PASS + 1)); printf 'ok    %s\n' "$desc"
    else FAIL=$((FAIL + 1)); printf 'FAIL  %s\n        unexpected: %s\n' "$desc" "$(printf '%s\n' "$out" | grep '^FAILED' | head -2)"; fi
  elif printf '%s\n' "$out" | grep -Eq "^${level}[[:space:]]+${id}[[:space:]]"; then
    PASS=$((PASS + 1)); printf 'ok    %s\n' "$desc"
  else
    FAIL=$((FAIL + 1)); printf 'FAIL  %s (expected %s %s)\n' "$desc" "$level" "$id"
  fi
}

IMPL="feature/$F-impl"; DOM="feature/$F-domain"; DATA="feature/$F-data"; API="feature/$F-api"

expect "unmodified project has no FAILED check"                clean   -    ":"
expect "module missing from project.yaml"                      FAILED  M2   "perl -ni -e \"print unless m{feature/$F-data}\" project.yaml"
expect "missing module.yaml"                                   FAILED  M1   "rm $API/module.yaml"
expect "wrong Koin moduleId"                                   FAILED  K1   "perl -pi -e 's/moduleId: $F-impl/moduleId: wrong-id/' $IMPL/module.yaml"
expect "duplicate Koin moduleId"                               FAILED  K2   "perl -pi -e 's/moduleId: $F-data/moduleId: $F-impl/' $DATA/module.yaml"
expect "domain depends on koin"                                FAILED  D1   "printf '  - \$libs.koin.core\n' >> $DOM/module.yaml"
expect "api has no NavKey"                                     FAILED  D3   "grep -rl NavKey $API/src | xargs perl -pi -e 's/NavKey/Any/g'"
expect "data depends on design system"                         FAILED  D4   "perl -pi -e 's|^dependencies:|dependencies:\n  - //shared/designsystem|' $DATA/module.yaml"
expect "impl depends on its own data module"                   FAILED  D5   "perl -pi -e 's|^dependencies:|dependencies:\n  - //$DATA|' $IMPL/module.yaml"
expect "another module depends on impl"                        FAILED  X1   "printf 'dependencies:\n  - //$IMPL\n' >> shared/core-testing/module.yaml"
expect "umbrella-ui does not depend on impl"                   FAILED  W1   "perl -ni -e \"print unless m{//$IMPL}\" shared/umbrella-ui/module.yaml"
expect "impl Koin module not installed in umbrella-ui"         FAILED  W2   "grep -rl ${F}UiModule shared/umbrella-ui/src | xargs perl -pi -e 's/${F}UiModule/otherModule/g'"
expect "impl tests deleted"                                    FAILED  T1   "rm -rf $IMPL/test"
expect "hardcoded UI text in impl"                             warning S1   "d=\$(find $IMPL/src -name '*.kt' | head -1); printf '\nfun x(){ Text(\"hello\") }\n' >> \$d"
expect "iOS string key missing"                                FAILED  S4   "python3 -I -c \"import json;p='iosApp/src/Localizable.xcstrings';d=json.load(open(p));del d['strings']['${F}_add'];json.dump(d,open(p,'w'))\""
expect "Maestro appId mismatch"                                FAILED  E1   "perl -pi -e 's/^appId:.*/appId: com.wrong.app/' maestro/features/$F/android.yaml"
expect "Swift tests removed"                                   FAILED  I3   "rm -f iosApp/test/*.swift"
expect "stripped appTests testable is repaired and reported"   warning I4   "perl -0pi -e 's/<Testables>.*?<\\/Testables>/<Testables>\n      <\\/Testables>/s' iosApp/module.xcodeproj/xcshareddata/xcschemes/iosApp.xcscheme"

printf '\n%d passed, %d failed\n' "$PASS" "$FAIL"
[ "$FAIL" -eq 0 ]
