# todo-ai（完整仓库）

- **已修复**：CI 无 Gradle Wrapper 报错（通过先运行 `gradle/actions/setup-gradle` 并指定 `gradle-version: 8.7`，再运行 `gradle/gradle-build-action`）。
- **Android**：AGP 8.5.2、Kotlin 2.0.0、Compose 插件、JDK 17 统一。
- **功能**：设置持久化、代理/网关下拉、模型含 GPT-5；“保存到智能整理”通知；桌面小工具（总数/重要/前十）。

## 本地构建
```bash
# 无 wrapper，也可使用：
./gradlew :app:assembleDebug  # 如果你后来加了 wrapper
# 或在 IDE 直接构建
```

## GitHub Actions
已内置 `.github/workflows/android.yml`，**顺序非常关键**：
1) `setup-java` → 2) `setup-gradle (gradle-version: 8.7)` → 3) `gradle-build-action`。

如果你更偏向使用 **Gradle Wrapper**，请告知，我将提供带 `gradlew` 的补丁（含 `chmod +x gradlew` 步骤）。
