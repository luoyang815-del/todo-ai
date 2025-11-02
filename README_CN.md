# Patch 说明：修复 Manifest `package=` 导致的构建失败

**错误原因（AGP 8+）**  
不再允许在 `AndroidManifest.xml` 中使用 `package="..."` 指定包名；必须在 `app/build.gradle(.kts)` 中通过 `namespace` 指定。

**本补丁包含：**
1. `app/src/main/AndroidManifest.xml`（已移除 `package` 属性）
2. `app/build.gradle.kts` 示例（包含 `namespace = "com.example.todoai"`）

**应用步骤：**
1. 用补丁中的 `AndroidManifest.xml` 覆盖你项目的 `app/src/main/AndroidManifest.xml`；
2. 确保 `app/build.gradle.kts`（或 `build.gradle`）里存在：  
   ```kotlin
   android {
       namespace = "com.example.todoai"
   }
   ```
   若你使用 `build.gradle`（Groovy），写法为：
   ```groovy
   android {
       namespace 'com.example.todoai'
   }
   ```
3. **不要**在 Manifest 再写 `package`；如有其它库/清单片段也有 `package`，同样移除。  
4. 重新执行：
   ```bash
   ./gradlew :app:assembleDebug --no-daemon --stacktrace
   ```

**额外提示：**
- `applicationId` 仍在 `defaultConfig` 内设置（面向打包的包名）；
- 你的 Settings 与 SmartOrganizer 代码包名要与 `namespace` 保持一致或调整 `package` 声明。

祝编译通过！
