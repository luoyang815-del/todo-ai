# todo-ai（完整仓库 v2）

这版 CI **不使用** `gradle/gradle-build-action`，而是：
1) setup-java 安装 JDK；2) setup-gradle 安装 Gradle 8.7；3) 直接 `run: gradle :app:assembleDebug`。

- Android：AGP 8.5.2、Kotlin 2.0.0、Compose 插件、JDK 17
- 功能：设置持久化、代理/网关下拉、模型含 GPT-5；“保存到智能整理”通知

推送到 main/master 后将自动出包。
