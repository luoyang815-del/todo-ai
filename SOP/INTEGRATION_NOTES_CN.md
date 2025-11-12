# 集成说明（todo-ai）

- 模块名假设 `:app`，如不同请全局替换。
- CI 默认构建 Debug 与 Release（不强制签名）；本次交付按 Debug 安装即可。
- 如需支持企业自签证书，请使用 `network_security_config.xml` 并导入用户证书。
- 华为设备请在“电池/应用启动管理”放开限制，保证通知与自动同步可用。