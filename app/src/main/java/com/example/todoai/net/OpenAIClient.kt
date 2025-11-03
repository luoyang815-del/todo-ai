// SPDX-License-Identifier: MIT
package com.example.todoai.net

import com.example.todoai.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.InetSocketAddress
import java.net.Proxy

class OpenAIClient {

    private fun buildClient(s: AppSettings): OkHttpClient {
        val b = OkHttpClient.Builder()
            .callTimeout(java.time.Duration.ofSeconds(60))
            .connectTimeout(java.time.Duration.ofSeconds(30))
            .readTimeout(java.time.Duration.ofSeconds(60))
            .writeTimeout(java.time.Duration.ofSeconds(60))

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
        return b.build()
    }

    suspend fun chatOnce(s: AppSettings, prompt: String): String = withContext(Dispatchers.IO) {
        val client = buildClient(s)
        val url = (s.endpoint.ifBlank { "https://api.openai.com" }).trimEnd('/') + "/v1/chat/completions"

        val json = JSONObject()
            .put("model", s.model.ifBlank { "gpt-5" })
            .put("messages", org.json.JSONArray()
                .put(JSONObject().put("role", "user").put("content", prompt))
            )
            .put("temperature", 0.2)

        val req = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + s.apiKey)
            .header("Content-Type", "application/json")
            .post(json.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) {
                throw RuntimeException("HTTP " + resp.code + " - " + (resp.body?.string() ?: ""))
            }
            val body = resp.body?.string() ?: ""
            val root = JSONObject(body)
            val choices = root.optJSONArray("choices")
            val first = choices?.optJSONObject(0)
            val msg = first?.optJSONObject("message")
            msg?.optString("content")?.ifBlank { null } ?: body.take(2000)
        }
    }
}
