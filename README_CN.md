# 修复包说明（Settings 修复 + 闪退保护）

> 文件名使用英文，内容中文。将以下文件合并到你的项目中对应路径即可。

## 主要修复点
1. **设置持久化**：进入设置页会从 SharedPreferences 读取；点击保存会写回。
2. **代理/网关类型下拉选择**：不再手填，避免大小写/非法值导致的崩溃。
3. **模型列表加入 GPT‑5**：含 `gpt-5`、`gpt-5-turbo`、`gpt-4o`、`gpt-4o-mini` 等。
4. **“保存到智能整理”闪退**：外围 `try/catch`，判空保护；加通知提示成功/失败。
5. **权限补齐**：`INTERNET` 与 `POST_NOTIFICATIONS`，避免“Permission denied”。

## 放置路径建议
```
app/
└─ src/
   └─ main/
      ├─ AndroidManifest.xml              ← 可合并权限声明
      ├─ res/values/strings.xml
      └─ java/com/example/todoai/
         ├─ settings/SettingsPage.kt
         └─ smart/SmartOrganizer.kt
```

## 使用方式
1. 将 `AndroidManifest.xml` 中的权限合并到你的清单（如已有则无需重复）。
2. 把 `strings.xml` 合并到你项目的 `res/values/strings.xml`。
3. 将 `SettingsPage.kt`、`SmartOrganizer.kt` 放到任意包路径（同步修改包名）。
4. 在你的导航/入口中调用 `SettingsPage()`。
5. 运行并验证：
   - 修改设置后返回再进入，配置应保持；
   - 代理/网关类型为下拉可选；
   - 模型列表包含 `gpt-5`；
   - 点击“保存到智能整理”，应收到系统通知且不闪退。

## 备注
- 该实现使用 `SharedPreferences` 简化集成；如需迁移 `DataStore` 可在后续版本替换。
- 通知渠道 ID：`smart_org_channel`，可在 `SmartOrganizer.kt` 中修改。
