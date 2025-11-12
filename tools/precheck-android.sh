#!/usr/bin/env bash
set -euo pipefail

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
fail() { echo -e "${RED}✗ $*${NC}"; exit 1; }
ok()   { echo -e "${GREEN}✓ $*${NC}"; }

echo "== Android 可用包-SOP预检 v2.8 =="

# 1) weight(1f)() 误用
echo "[1/6] 检查 Modifier.weight(1f)() 误用…"
hits=$(grep -RIn --include=\*.kt 'weight\([[:space:]]*1f[[:space:]]*\)[[:space:]]*\(\)' app/src/main/java || true)
if [ -n "$hits" ]; then
  echo -e "${YELLOW}发现以下误用：${NC}"; echo "$hits"
  fail "存在 'Modifier.weight(1f)()' 误用，请修复为 'Modifier.weight(1f)'"
else
  ok "未发现 weight(1f)() 误用"
fi

# 2) Application 与 Composable 重名 App
echo "[2/6] 检查 Application(App) 与 @Composable(App) 重名…"
if grep -RIn --include=\*.kt 'class[[:space:]]\+App[[:space:]]*:' app/src/main/java >/dev/null 2>&1; then
  if grep -RIn --include=\*.kt '@Composable[[:space:]]*fun[[:space:]]\+App\(' app/src/main/java >/dev/null 2>&1; then
    fail "存在 class App 与 @Composable fun App 重名；请把 Composable 改名为 MainScreen 等"
  fi
fi
ok "未发现 App 重名冲突"

# 3) 资源XML 不允许 XML 声明头 / BOM
echo "[3/6] 检查 res XML 头/BOM…"
bad_xml=$(grep -RIl --include=\*.xml '^<\?xml' app/src/main/res 2>/dev/null || true)
if [ -n "$bad_xml" ]; then
  echo -e "${YELLOW}以下资源XML包含 XML 头：${NC}"; echo "$bad_xml"
  fail "请去掉 XML 声明头（<?xml ...?>）"
fi
bom_files=$(grep -RIl --include=\*.xml $'\xEF\xBB\xBF' app/src/main/res 2>/dev/null || true)
if [ -n "$bom_files" ]; then
  echo -e "${YELLOW}以下资源XML包含 BOM：${NC}"; echo "$bom_files"
  fail "请移除 BOM（保存为 UTF-8 无BOM）"
fi
ok "资源 XML 头/BOM 检查通过"

# 4) Todo 类型仅允许一处定义（core/Models.kt）
echo "[4/6] 检查 Todo 类型重复定义…"
todo_defs=$(grep -RIn --include=\*.kt 'data[[:space:]]\+class[[:space:]]\+Todo' app/src/main/java || true)
if [ -n "$todo_defs" ]; then
  count=$(echo "$todo_defs" | wc -l | xargs)
  if [ "$count" -gt 1 ]; then
    echo "$todo_defs"
    fail "发现多个 Todo 定义；仅保留 app/core/Models.kt 内的 Todo"
  fi
fi
ok "Todo 类型定义正常"

# 5) 旧模块残留（Room/DataStore/WorkManager/Security）
echo "[5/6] 检查旧模块残留…"
residual=$(grep -RIn --include=\*.kt -E '(room\.|datastore|work-runtime|WorkManager|Security\.crypto)' app/src/main/java || true)
if [ -n "$residual" ]; then
  echo -e "${YELLOW}检测到旧模块引用：${NC}"; echo "$residual"
  fail "存在旧模块残留（请删除 data/db/prefs/repo/work 等旧文件或补齐依赖）"
fi
ok "无旧模块残留引用"

# 6) 版本链（文本）
echo "[6/6] 检查版本链…"
grep -RIn 'kotlinCompilerExtensionVersion[[:space:]]*=[[:space:]]*"1.6.10"' app/build.gradle.kts >/dev/null || fail "Compose 编译扩展版本应为 1.6.10"
ok "版本链文本检查通过"

echo -e "${GREEN}全部预检通过，可进入构建阶段。${NC}"
