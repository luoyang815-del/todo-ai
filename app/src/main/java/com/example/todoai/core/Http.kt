package com.example.todoai.core

import com.example.todoai.settings.AppConfig
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

object Http {
    fun client(cfg: AppConfig): OkHttpClient {
        val b = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        if (cfg.proxyEnabled && cfg.proxyHost.isNotBlank() && cfg.proxyPort > 0) {
            val proxyType = when (cfg.proxyType.lowercase()) {
                "socks5" -> Proxy.Type.SOCKS
                else -> Proxy.Type.HTTP
            }
            b.proxy(Proxy(proxyType, InetSocketAddress(cfg.proxyHost, cfg.proxyPort)))
            if (cfg.proxyAuthEnabled && cfg.proxyUsername.isNotBlank()) {
                b.proxyAuthenticator { _, resp ->
                    val cred = Credentials.basic(cfg.proxyUsername, cfg.proxyPassword)
                    resp.request.newBuilder().header("Proxy-Authorization", cred).build()
                }
            }
        }
        return b.build()
    }

    fun get(url: String, client: OkHttpClient): String {
        val req = Request.Builder().url(url).get().build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return "HTTP ${resp.code}: ${resp.message}"
            return resp.body?.string() ?: ""
        }
    }
}
