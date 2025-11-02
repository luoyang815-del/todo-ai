pluginManagement {
    repositories {
        // 这三行是关键：让插件解析能在 Google / MavenCentral / PluginPortal 找到
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "todo-ai"   // 或保留你现有的名字
include(":app")
