# TodoAI 完整工程（v2）— 已修复 Kotlin 2.0 需要 Compose Compiler 插件的问题

**修复点新增：**
- 在 `app/build.gradle.kts` 增加插件：  
  ```kotlin
  plugins {
      id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
  }
  ```
- 去掉旧的 `composeOptions.kotlinCompilerExtensionVersion`，由插件接管。
- 其余：Manifest 合规、JDK17 统一、设置持久化、代理下拉、模型含 GPT‑5、智能整理通知、widget 字符串资源补齐。

**构建：**
```bash
./gradlew :app:assembleDebug --no-daemon --stacktrace
```

如需我把包名/应用名/图标/签名、GitHub Actions 工作流一起打好，请把信息给我。

