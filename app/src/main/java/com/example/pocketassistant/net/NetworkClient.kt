
package com.example.pocketassistant.net
import android.app.Application
import okhttp3.*
import java.net.*
import java.util.concurrent.TimeUnit
class NetworkClient(private val app: Application){
  suspend fun client(): OkHttpClient {
    val s = SettingsRepository(app).load()
    val b = OkHttpClient.Builder().connectTimeout(30,TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).writeTimeout(60,TimeUnit.SECONDS)
    when(s.proxyType.uppercase()){
      "HTTP","HTTPS" -> if(s.proxyHost.isNotBlank() && s.proxyPort>0){
         val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(s.proxyHost,s.proxyPort)); b.proxy(proxy)
         if(s.proxyUser.isNotBlank()){ b.proxyAuthenticator { _,resp ->
           val cred = Credentials.basic(s.proxyUser, s.proxyPass); resp.request.newBuilder().header("Proxy-Authorization", cred).build()
         } }
      }
      "SOCKS5" -> if(s.proxyHost.isNotBlank() && s.proxyPort>0){
         val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress(s.proxyHost,s.proxyPort)); b.proxy(proxy)
      }
    }
    return b.build()
  }
}
