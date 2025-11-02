
package com.example.pocketassistant.net

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

object OpenAIClient {
    fun create(baseUrl: String, proxy: ProxyConfig?): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)

        proxy?.let { conf ->
            val proxyObj = when (conf.type.uppercase()) {
                "HTTP","HTTPS" -> Proxy(Proxy.Type.HTTP, InetSocketAddress(conf.host, conf.port))
                "SOCKS5" -> Proxy(Proxy.Type.SOCKS, InetSocketAddress(conf.host, conf.port))
                else -> null
            }
            if (proxyObj != null) builder.proxy(proxyObj)
            if (!conf.username.isNullOrBlank()) {
                builder.proxyAuthenticator { _, response ->
                    val cred = Credentials.basic(conf.username!!, conf.password ?: "")
                    response.request.newBuilder()
                        .header("Proxy-Authorization", cred).build()
                }
            }
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(builder.build())
            .build()
    }
}
