package com.example.todoai.core

import android.content.Context
import com.example.todoai.settings.AppConfig

object Gateway {
    fun sendText(ctx: Context, cfg: AppConfig, text: String): String {
        val base = if (cfg.gatewayEnabled && cfg.baseUrl.isNotBlank()) cfg.baseUrl else "https://example.org"
        val url = "$base/robots.txt" // 演示：用 robots.txt 替代真实接口（你配置网关后即可切换）
        val client = Http.client(cfg)
        return try {
            Http.get(url, client)
        } catch (e: Exception) {
            "错误：" + (e.message ?: e.toString())
        }
    }
}
