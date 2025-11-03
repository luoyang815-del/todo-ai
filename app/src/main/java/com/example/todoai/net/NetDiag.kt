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
    private fun normalizeEndpoint(src: String): String {
        var s = src.trim().trim('"', '\'', ' ')
        s = s.replace(Regex("^(https?://)+", RegexOption.IGNORE_CASE)) { m ->
            val v = m.value.lowercase()
            if (v.contains("https://")) "https://" else "http://"
        }
        if (!s.startsWith("http://") && !s.startsWith("https://")) s = "https://"+s
        return s.trimEnd('/')
    }
    suspend fun ping(s: AppSettings): String = withContext(Dispatchers.IO) {
        val client = buildClient(s)
        val base = normalizeEndpoint(s.endpoint.ifBlank { "https://api.openai.com" })
        val req = Request.Builder().url(base + "/robots.txt").get().build()
        try { client.newCall(req).execute().use { r -> "连通性 OK：HTTP ${r.code} ${r.message}" } }
        catch (e: Throwable) { "连通性失败：" + (e.message ?: e.toString()) }
    }
    private fun buildClient(s: AppSettings): OkHttpClient {
        val b = OkHttpClient.Builder().callTimeout(Duration.ofSeconds(30)).connectTimeout(Duration.ofSeconds(15)).readTimeout(Duration.ofSeconds(30))
        if (s.useProxy) {
            val t = s.proxyType.uppercase()
            val type = when (t) { "SOCKS5" -> Proxy.Type.SOCKS; "HTTPS","GATEWAY","HTTP" -> Proxy.Type.HTTP; else -> null }
            if (type != null && s.proxyHost.isNotBlank() && s.proxyPort > 0) {
                b.proxy(Proxy(type, InetSocketAddress(s.proxyHost, s.proxyPort)))
                if (s.proxyUser.isNotBlank() || s.proxyPass.isNotBlank()) {
                    b.proxyAuthenticator { _, resp ->
                        val c = Credentials.basic(s.proxyUser, s.proxyPass)
                        resp.request.newBuilder().header("Proxy-Authorization", c).build()
                    }
                }
            }
        }
        return b.build()
    }
}
