#!/usr/bin/env bash
# Applies the local-crud archetype to a project exactly as it is (feature name "sample", no renaming).
#
# This exists for validation only: the archetype check, and the fixture used by test-verify-feature.sh.
# Real features are adapted by hand following ../README.md and wiring.md; do not use this script to add
# a feature to an app.
#
# Usage: apply-verbatim.sh <project-dir>
#   <project-dir>  a copy of the starter (the directory that holds project.yaml), outside any git
#                  repository. It is modified in place; the script refuses directories inside a repo.
#
# Needs bash, rsync, perl and python3. Idempotent: running it twice leaves the same result.

set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
DEST="${1:-}"
if [ -z "$DEST" ] || [ ! -f "$DEST/project.yaml" ]; then
  echo "Usage: $0 <project-dir>   (directory containing project.yaml)" >&2
  exit 2
fi
DEST="$(cd "$DEST" && pwd)"
if git -C "$DEST" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Refusing to modify $DEST: it is inside a git repository (the real starter or an app)." >&2
  echo "Copy the starter to a scratch directory outside any repository and pass that." >&2
  exit 2
fi

# The package root is com.dream.template in the starter and is renamed by dream-init in generated apps,
# so derive it from the Android host instead of assuming it.
MAIN="$(find "$DEST/androidApp/src" -name MainActivity.kt | head -1)"
[ -n "$MAIN" ] || { echo "Cannot find androidApp MainActivity.kt under $DEST" >&2; exit 2; }
PKG_PATH="${MAIN#"$DEST/androidApp/src/"}"; PKG_PATH="${PKG_PATH%/android/MainActivity.kt}"
PKG="${PKG_PATH//\//.}"
fix_pkg() { sed "s/com\.dream\.template/$PKG/g"; }   # for inline file contents written below

# 1. Kotlin modules and the DI composition test
rsync -a "$HERE/kotlin/" "$DEST/"

# 2. project.yaml: add the four modules before umbrella-core
python3 -I - "$DEST/project.yaml" <<'PY'
import sys
p = sys.argv[1]
lines = open(p).read().split("\n")
mods = ["feature/sample-domain", "feature/sample-data", "feature/sample-api", "feature/sample-impl"]
have = {l.strip()[2:].strip() for l in lines if l.strip().startswith("- ")}
new = [f"  - {m}" for m in mods if m not in have]
if new:
    i = next(k for k, l in enumerate(lines) if l.strip() == "- shared/umbrella-core")
    lines[i:i] = new
open(p, "w").write("\n".join(lines))
PY

# 3. module.yaml dependencies of the umbrella modules
add_deps() { # file, then dependency lines to add after "dependencies:"
  python3 -I - "$@" <<'PY'
import sys
p, deps = sys.argv[1], sys.argv[2:]
s = open(p).read()
missing = [d for d in deps if d not in s]
if missing:
    marker = "dependencies:\n"
    i = s.index(marker) + len(marker)
    s = s[:i] + "".join(f"  - {d}\n" for d in missing) + s[i:]
open(p, "w").write(s)
PY
}
add_deps "$DEST/shared/umbrella-core/module.yaml" "//feature/sample-data: exported" "//feature/sample-domain: exported"
add_deps "$DEST/shared/umbrella-ui/module.yaml" "//feature/sample-data" "//feature/sample-domain" "//feature/sample-api: exported" "//feature/sample-impl"

# 4. Composition root and Android host (whole files, as in wiring.md)
fix_pkg > "$DEST/shared/umbrella-ui/src/$PKG_PATH/shared/di/UiModule.kt" <<'EOF'
package com.dream.template.shared.di

import com.dream.template.feature.sample.api.navigation.SampleRoute
import com.dream.template.feature.sample.data.di.sampleDataModule
import com.dream.template.feature.sample.data.local.SampleDatabaseFactory
import com.dream.template.feature.sample.impl.di.sampleUiModule
import com.dream.template.shared.navigation.Navigator
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val navigatorModule = module {
    single { Navigator(startDestination = SampleRoute) }
}

private var hasStartedKoin = false

fun initKoin(
    sampleDatabaseFactory: SampleDatabaseFactory,
    appContext: Any? = null,
    appDeclaration: KoinAppDeclaration = {}
) {
    if (hasStartedKoin) return

    startKoin {
        appDeclaration()
        modules(
            coreModule(appContext),
            sampleDataModule(sampleDatabaseFactory),
            sampleUiModule,
            navigatorModule,
        )
    }
    hasStartedKoin = true
}
EOF
fix_pkg > "$MAIN" <<'EOF'
package com.dream.template.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dream.template.feature.sample.data.local.AndroidSampleDatabaseFactory
import com.dream.template.shared.app.DreamApp
import com.dream.template.shared.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        initKoin(
            sampleDatabaseFactory = AndroidSampleDatabaseFactory(applicationContext),
            appContext = applicationContext,
        )
        enableEdgeToEdge()
        setContent {
            DreamApp()
        }
    }
}
EOF

# 5. Strings: merge the sample keys into both platforms, keeping existing keys
python3 -I - "$HERE/strings" "$DEST" <<'PY'
import json, re, sys
src, dest = sys.argv[1], sys.argv[2]
# Android
sp, dp = f"{src}/strings.xml", f"{dest}/shared/core-strings/composeResources/values/strings.xml"
have = open(dp, encoding="utf-8").read()
new = [l for l in open(sp, encoding="utf-8").read().split("\n")
       if re.search(r'<string name="', l) and re.search(r'name="([^"]+)"', l).group(1) not in have]
if new:
    have = have.replace("</resources>", "\n".join(new) + "\n</resources>")
    open(dp, "w", encoding="utf-8").write(have)
# iOS
xs, xd = f"{src}/Localizable.xcstrings", f"{dest}/iosApp/src/Localizable.xcstrings"
a, b = json.load(open(xs, encoding="utf-8")), json.load(open(xd, encoding="utf-8"))
for k, v in a["strings"].items():
    b["strings"].setdefault(k, v)
b["strings"] = dict(sorted(b["strings"].items()))
open(xd, "w", encoding="utf-8").write(json.dumps(b, indent=2, separators=(",", " : "), ensure_ascii=False) + "\n")
PY

# 6. iOS sources and tests, Maestro flows
mkdir -p "$DEST/iosApp/src" "$DEST/iosApp/test" "$DEST/maestro/features"
rsync -a "$HERE/ios/src/Sample" "$DEST/iosApp/src/"
cp "$HERE/ios/src/Core/AppDependencies.swift" "$DEST/iosApp/src/Core/AppDependencies.swift"
cp "$HERE/ios/src/ContentView.swift" "$DEST/iosApp/src/ContentView.swift"
cp "$HERE"/ios/test/*.swift "$DEST/iosApp/test/"
rsync -a "$HERE/maestro/features/sample" "$DEST/maestro/features/"

echo "Applied local-crud verbatim to $DEST"
