# todo-ai 安卓交付加速包（SOP+CI）（V1）

适配仓库：`github.com/luoyang815-del/todo-ai`
默认模块：`:app`（如不同请全文替换）
签名：Debug（本次交付）
目标设备：华为（建议放开电池与后台限制）
证书：使用我方自签，**应用信任“系统+用户证书”**（你后续导入 CER/PEM 到设备“受信任凭据-用户”即可）
代理：留空，待成型后在应用里填写

## 你需要做的最少动作
1. 把本 ZIP 解压到仓库根目录（不会覆盖源码）。
2. （可选）把 `SOP/TEMPLATES/network_security_config.xml` 复制到 `app/src/main/res/xml/`，并在 `AndroidManifest.xml` 的 `<application>` 增加：
   ```xml
   android:networkSecurityConfig="@xml/network_security_config"
   ```
3. 直接 push；GitHub Actions 会自动 `assembleDebug` 和 `assembleRelease`，产物在 Actions Artifact 里下载。
4. 我会用云端产物做冒烟后，给你**最终可安装 APK 包**。你无需本地预检。