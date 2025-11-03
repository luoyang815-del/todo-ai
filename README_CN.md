# todo-ai（完整仓库 v3）

**修复**：AAPT 资源链接失败（缺少 `Theme.Material3.DayNight.NoActionBar`）——已添加 `com.google.android.material:material:1.12.0` 并保持该主题。

**CI**：不依赖 wrapper；`setup-gradle` 安装 Gradle 8.7，直接 `run: gradle :app:assembleDebug`。

**Android 工程**：AGP 8.5.2、Kotlin 2.0.0、Compose 插件、JDK 17 对齐；设置持久化、代理/网关下拉、模型含 GPT-5；“保存到智能整理”通知。

推送到 main/master 后自动构建。
