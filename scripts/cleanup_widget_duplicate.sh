#!/usr/bin/env bash
set -euo pipefail
PROJECT_ROOT="${1:-}"
if [[ -z "${PROJECT_ROOT}" ]]; then
  read -rp "请输入你的工程根目录（包含 app/ 的目录路径）: " PROJECT_ROOT
fi
if [[ ! -d "${PROJECT_ROOT}" ]]; then
  echo "路径不存在: ${PROJECT_ROOT}" >&2
  exit 1
fi

files=(
  "app/src/main/java/com/aihelper/app/widget/TodosWidget.kt"
  "app/src/main/java/com/aihelper/app/widget/TodosWidgetReceiver.kt"
)

for f in "${files[@]}"; do
  p="${PROJECT_ROOT}/${f}"
  if [[ -f "$p" ]]; then
    rm -f "$p"
    echo "已删除: $f"
  fi
done

echo
echo "请检查 AndroidManifest.xml 是否还包含旧的 <receiver>（TodosWidgetReceiver），若有请删除该段。"
echo "文件路径: ${PROJECT_ROOT}/app/src/main/AndroidManifest.xml"
echo
echo "建议执行构建：gradle clean :app:assembleRelease --no-build-cache --refresh-dependencies"
