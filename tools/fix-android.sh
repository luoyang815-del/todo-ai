#!/usr/bin/env bash
set -euo pipefail

echo "[fix] 规范化 weight 写法…"
sed -i -E 's/weight\(([[:space:]]*1f[[:space:]]*)\)[[:space:]]*\(\)/weight(1f)/g' app/src/main/java/com/aihelper/app/MainActivity.kt

echo "[fix] 去除 res XML 头（如有）…"
for f in $(grep -RIl --include=\*.xml '^<\?xml' app/src/main/res || true); do
  tail -n +2 "$f" > "$f.tmp" && mv "$f.tmp" "$f"
done

echo "[fix] 移除 res XML BOM（如有）…"
python3 - <<'PY' || true
import pathlib
for p in pathlib.Path("app/src/main/res").rglob("*.xml"):
    b = p.read_bytes()
    if b.startswith(b"\xEF\xBB\xBF"):
        p.write_bytes(b[3:])
PY

echo "[fix] 完成。"
