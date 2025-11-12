
param([string]$ProjectRoot)

if (-not $ProjectRoot) {
  $ProjectRoot = Read-Host "请输入你的工程根目录（包含 app/ 的目录路径）"
}
if (-not (Test-Path $ProjectRoot)) {
  Write-Error "路径不存在：$ProjectRoot"
  exit 1
}

$files = @(
  "app/src/main/java/com/aihelper/app/widget/TodosWidget.kt",       # 旧文件（与 Provider 重名）
  "app/src/main/java/com/aihelper/app/widget/TodosWidgetReceiver.kt" # 旧 Glance Receiver（若存在）
)

foreach ($f in $files) {
  $p = Join-Path $ProjectRoot $f
  if (Test-Path $p) {
    Remove-Item -Force $p
    Write-Host "已删除: $f"
  }
}

# 提示检查 Manifest 是否仍包含旧的 <receiver>（Glance 版），若有请手工删除
$manifest = Join-Path $ProjectRoot "app/src/main/AndroidManifest.xml"
Write-Host "`n请检查 AndroidManifest.xml 中是否还存在旧的 <receiver>（TodosWidgetReceiver）；若有请删除该段。"
Write-Host "文件路径: $manifest"

Write-Host "`n建议执行构建：gradle clean :app:assembleRelease --no-build-cache --refresh-dependencies"
