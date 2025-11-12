#!/usr/bin/env python3
import re, pathlib, sys

root = pathlib.Path(".")
ok = True

# 1) weight misuse
pattern = re.compile(r'weight\(\s*1f\s*\)\s*\(\)')
hits = []
for f in root.rglob("app/src/main/java/**/*.kt"):
    txt = f.read_text(encoding="utf-8", errors="ignore")
    for i, line in enumerate(txt.splitlines(), 1):
        if pattern.search(line):
            hits.append(f"{f}:{i}: {line.strip()}")
if hits:
    print("FAIL weight(1f)():"); print("\n".join(hits)); ok = False
else:
    print("PASS weight(1f)()")

# 2) App name conflict
app_class = any(re.search(r'class\s+App\s*:', f.read_text(encoding="utf-8", errors="ignore")) for f in root.rglob("app/src/main/java/**/*.kt"))
fun_app  = any(re.search(r'@Composable\s*fun\s+App\s*\(', f.read_text(encoding="utf-8", errors="ignore")) for f in root.rglob("app/src/main/java/**/*.kt"))
if app_class and fun_app:
    print("FAIL App name conflict"); ok = False
else:
    print("PASS App name conflict")

# 3) XML header / BOM
header_bad = []; bom_bad = []
for f in root.rglob("app/src/main/res/**/*.xml"):
    b = f.read_bytes()
    if b.startswith(b"\xEF\xBB\xBF"): bom_bad.append(str(f))
    first = (b.decode("utf-8", errors="ignore").splitlines() or [""])[0].strip()
    if first.startswith("<?xml"): header_bad.append(str(f))
if header_bad:
    print("FAIL XML header:"); print("\n".join(header_bad)); ok=False
else:
    print("PASS XML header")
if bom_bad:
    print("FAIL XML BOM:"); print("\n".join(bom_bad)); ok=False
else:
    print("PASS XML BOM")

# 4) Todo duplicate
todo_files = [str(f) for f in root.rglob("app/src/main/java/**/*.kt") if re.search(r'\bdata\s+class\s+Todo\b', f.read_text(encoding="utf-8", errors="ignore"))]
if len(todo_files) != 1:
    print("FAIL Todo definitions:", todo_files); ok=False
else:
    print("PASS Todo definitions")

# 5) Residual modules
residual = []
for f in root.rglob("app/src/main/java/**/*.kt"):
    if re.search(r'(room\.|datastore|work-runtime|WorkManager|Security\.crypto)', f.read_text(encoding="utf-8", errors="ignore")):
        residual.append(str(f))
if residual:
    print("FAIL residual modules:"); print("\n".join(residual)); ok=False
else:
    print("PASS residual modules")

# 6) Version chain
gradle = (root/"app/build.gradle.kts").read_text(encoding="utf-8", errors="ignore")
if 'kotlinCompilerExtensionVersion = "1.6.10"' in gradle:
    print("PASS compose compiler ext 1.6.10")
else:
    print('FAIL compose compiler ext must be 1.6.10'); ok=False

if not ok:
    sys.exit(1)
print("ALL PASS")
