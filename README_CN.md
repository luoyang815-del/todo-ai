# CI 修复补丁（无 Gradle Wrapper 也能跑）

你的 CI 报错：
> Cannot locate Gradle Wrapper script at '.../gradlew'. Specify 'gradle-version' for projects without Gradle wrapper configured.

**原因**：仓库没有 `gradlew`/`gradlew.bat` 和 `gradle/wrapper/*`。

**解决**：在 GitHub Actions 里**显式指定 Gradle 版本**，无需 wrapper。

## 使用方法
把本补丁中的 `.github/workflows/android.yml` 覆盖你仓库的同名文件即可。

该工作流会：
1. 安装 JDK 17；
2. 安装 Gradle 8.7（无 wrapper 模式）；
3. 执行 `:app:assembleDebug` 构建。

如需 release 构建，可把最后一步 `arguments` 改为 `:app:assembleRelease` 并配置签名。
