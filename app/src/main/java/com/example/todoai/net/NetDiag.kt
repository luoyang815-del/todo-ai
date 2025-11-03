// SPDX-License-Identifier: MIT
package com.example.todoai.net

import android.content.Context
import com.example.todoai.settings.AppSettings
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Proxy

object NetDiag {
    fun ping(context: Context, s: AppSettings): String {
        val client = buildClient(s)
        val base = (s.endpoint.ifBlank { "https://api.openai.com" }).trimEnd('/')
        val url = "$base/robots.txt"
        val req = Request.Builder().url(url).get().build()
        return try {
            client.newCall(req).execute().use { resp ->
                "连通性 OK：HTTP ${resp.code} ${resp.message}"
            }
        } catch (e: Throwable) {
            "连通性失败：" + (e.message ?: e.toString())
        }
    }

    private fun buildClient(s: AppSettings): OkHttpClient {
        val b = OkHttpClient.Builder()
            .callTimeout(java.time.Duration.ofSeconds(30))
            .connectTimeout(java.time.Duration.ofSeconds(15))
            .readTimeout(java.time.Duration.ofSeconds(30))

        if (s.useProxy) {
            val type = s.proxyType.uppercase()
            val host = s.proxyHost
            val port = s.proxyPort
            if (host.isNotBlank() && port > 0) {
                val proxyType = when (type) {
                    "SOCKS5" -> Proxy.Type.SOCKS
                    "HTTPS", "GATEWAY", "HTTP" -> Proxy.Type.HTTP
                    else -> null
                }
                if (proxyType != null) {
                    val proxy = Proxy(proxyType, InetSocketAddress(host, port))
                    b.proxy(proxy)
                    if (s.proxyUser.isNotBlank() || s.proxyPass.isNotBlank()) {
                        b.proxyAuthenticator { _, response ->
                            val credential = Credentials.basic(s.proxyUser, s.proxyPass)
                            response.request.newBuilder().header("Proxy-Authorization", credential).build()
                        }
                    }
                }
            }
        }
        return b.build()
    }
}
