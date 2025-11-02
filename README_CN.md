# Patch 说明：补齐 `widget_todo.xml` 缺失的字符串资源

**构建失败原因（来自日志）**
- `layout/widget_todo.xml` 引用了 `@string/widget_header` 与 `@string/widget_legend`，但 `values/strings*.xml` 中不存在，导致 `:app:processDebugResources` 失败。

**本补丁包含**
- `app/src/main/res/values/strings_widget.xml`：新增上述两个字符串资源，避免与现有 `strings.xml` 冲突。

**应用步骤**
1. 将 `strings_widget.xml` 放入你仓库的 `app/src/main/res/values/` 目录（不要覆盖原有文件）。
2. 重新编译：
   ```bash
   ./gradlew :app:assembleDebug --no-daemon --stacktrace
   ```

**注意**
- 若后续仍有 `aapt` 类似报错，请逐条补齐被引用但缺失的资源（string/color/dimen/style），或把 `widget_todo.xml` 发我，我直接对齐资源表并一次性补全。
