# 可选：仅供我方在云端/本地跑预检；你方无需运行
$ErrorActionPreference = "Stop"
& .\gradlew.bat --version
try { & .\gradlew.bat lint } catch { Write-Host "lint 告警，继续" }
& .\gradlew.bat :app:assembleRelease -x test