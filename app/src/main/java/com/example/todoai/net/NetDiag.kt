// SPDX-License-Identifier: MIT
package com.example.todoai.net

import com.example.todoai.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Proxy
import java.time.Duration

object NetDiag {
    suspend fun ping(s: AppSettings): String = withContext(Dispatchers.IO) {
        val client = buildClient(s)
        val base = (s.endpoint.ifBlank { "https://api.openai.com" }).trimEnd('/')
        val req = Request.Builder().url("$base/robots.txt").get().build()
        try {
            client.newCall(req).execute().use { resp ->
                "连通性 OK：HTTP ${resp.code} ${resp.message}"
            }
        } catch (e: Throwable) {
            "连通性失败：" + (e.message ?: e.toString())
        }
    }

    private fun buildClient(s: AppSettings): OkHttpClient {
        val b = OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(30))
            .connectTimeout(Duration.ofSeconds(15))
            .readTimeout(Duration.ofSeconds(30))

        if (s.useProxy) {
            val type = s.proxyType.uppercase()
            val proxyType = when (type) {
                "SOCKS5" -> Proxy.Type.SOCKS
                "HTTPS", "GATEWAY", "HTTP" -> Proxy.Type.HTTP
                else -> null
            }
            if (proxyType != null && s.proxyHost.isNotBlank() && s.proxyPort > 0) {
                b.proxy(Proxy(proxyType, InetSocketAddress(s.proxyHost, s.proxyPort)))
                if (s.proxyUser.isNotBlank() || s.proxyPass.isNotBlank()) {
                    b.proxyAuthenticator { _, response ->
                        val c = Credentials.basic(s.proxyUser, s.proxyPass)
                        response.request.newBuilder().header("Proxy-Authorization", c).build()
                    }
                }
            }
        }
        return b.build()
    }
}
