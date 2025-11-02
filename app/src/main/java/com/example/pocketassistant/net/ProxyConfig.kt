
package com.example.pocketassistant.net
data class ProxyConfig(
    val type: String = "NONE", // NONE/HTTP/HTTPS/SOCKS5
    val host: String = "",
    val port: Int = 0,
    val username: String? = null,
    val password: String? = null
)
