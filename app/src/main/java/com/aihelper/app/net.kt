
package com.aihelper.app
import android.content.Context
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.CertificatePinner
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.InputStream
import java.net.InetSocketAddress
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
data class SyncBatch(val client_id:String, val since_todo:Long, val since_msg:Long, val todos:List<Todo>, val messages:List<Message>)
data class PullResp(val ok:Boolean, val todos:List<Todo>, val messages:List<Message>, val ts_todo:Long, val ts_msg:Long)
interface SyncApi { @POST("/api/v1/sync/push") suspend fun push(@Header("Authorization") auth:String, @Body batch:SyncBatch): Map<String,Any>; @GET("/api/v1/sync/pull") suspend fun pull(@Header("Authorization") auth:String, @Query("since_todo") sinceTodo:Long, @Query("since_msg") sinceMsg:Long): PullResp }
data class ChatMsg(val role:String, val content:String)
data class ChatReq(val model:String, val messages:List<ChatMsg>)
data class ChatChoice(val index:Int, val message:ChatMsg)
data class ChatResp(val choices:List<ChatChoice> = emptyList())
interface OpenAiApi { @POST("/v1/chat/completions") suspend fun chat(@Header("Authorization") auth:String, @Body req:ChatReq): ChatResp }
private fun proxyAuth(user:String, pass:String) = Authenticator { _, response -> val cred = Credentials.basic(user, pass); response.request.newBuilder().header("Proxy-Authorization", cred).build() }
private fun customCaTrust(ctx: Context): Pair<javax.net.ssl.SSLSocketFactory, X509TrustManager> {
  val cf = CertificateFactory.getInstance("X.509")
  val caInput: InputStream = ctx.resources.openRawResource(com.aihelper.app.R.raw.ca)
  val ca = cf.generateCertificate(caInput)
  val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply { load(null, null); setCertificateEntry("ca", ca) }
  val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply { init(keyStore) }
  val trustManager = tmf.trustManagers[0] as X509TrustManager
  val sslContext = SSLContext.getInstance("TLS").apply { init(null, arrayOf(trustManager), null) }
  return Pair(sslContext.socketFactory, trustManager)
}
fun okHttpSecure(ctx: Context, proxyType:Int, host:String, port:Int, user:String="", pass:String="", pinHost:String="", pinSha256:String="", trustCustomCA:Boolean=false): OkHttpClient {
  val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
  val b = OkHttpClient.Builder().addInterceptor(log)
  if (proxyType in 1..3 && host.isNotBlank() && port>0){
    val p = when(proxyType){ 1,2 -> java.net.Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress(host, port)); 3 -> java.net.Proxy(java.net.Proxy.Type.SOCKS, InetSocketAddress(host, port)); else -> null }
    if (p!=null) b.proxy(p)
    if (user.isNotBlank()) b.proxyAuthenticator(proxyAuth(user, pass))
  }
  if (pinHost.isNotBlank() && pinSha256.isNotBlank()) b.certificatePinner(CertificatePinner.Builder().add(pinHost, "sha256/$pinSha256").build())
  if (trustCustomCA) { val (ssl, tm) = customCaTrust(ctx); b.sslSocketFactory(ssl, tm) }
  return b.build()
}
fun retrofitFor(baseUrl:String, client:OkHttpClient): Retrofit = Retrofit.Builder().baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/").addConverterFactory(MoshiConverterFactory.create()).client(client).build()
