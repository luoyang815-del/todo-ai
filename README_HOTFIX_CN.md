# todo-ai 仓库热修包（V2）

本包用于快速修复以下常见“仓库一推就报错”的问题：
1. 缺少 `ic_launcher` 图标资源导致 `@mipmap/ic_launcher` 引用失败。
2. CI 依赖 Gradle Wrapper 但仓库中暂无 `gradle-wrapper.jar`。

**做法：**
- 新增自适应图标资源（前景矢量 + 背景色），不依赖外部图片；
- 将 CI 切换为“安装指定版本 Gradle（8.6）然后直接执行 `gradle ...`”，不再依赖 `./gradlew`；

**使用：**
把压缩包解压到仓库根目录并覆盖同名文件 → `git add . && git commit -m "hotfix: icons + no-wrapper CI" && git push`。  
推送后到 **Actions** 下载产物即可。
