
# PocketAssistant（随身助手 · Android 版 · v3）
- UI 已按你确认的方案完成（首页/聊天/设置/小组件/事件详情）。
- 当前为演示逻辑，便于你确认界面；确认后我再接 Whisper 与 GPT 真实解析。

## GitHub Actions（已修复 gradlew 权限）
详见 `.github/workflows/android.yml`，包含：
- `chmod +x ./gradlew`
- JDK 17、Gradle 缓存。。。
- `./gradlew :app:assembleDebug` 产出 APK Artifact。
