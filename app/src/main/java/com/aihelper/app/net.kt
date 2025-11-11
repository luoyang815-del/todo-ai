
package com.aihelper.app

import android.content.Context
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Proxy
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.net.InetSocketAddress

data class SyncBatch(val client_id:String, val since:Long, val todos:List<Todo>, val messages:List<Message>)
data class PullResp(val ok:Boolean, val todos:List<Todo>, val messages:List<Message>, val ts:Long)

interface SyncApi {
    @POST("/api/v1/sync/push") suspend fun push(@Header("Authorization") auth:String, @Body batch:SyncBatch): Map<String,Any>
    @GET("/api/v1/sync/pull") suspend fun pull(@Header("Authorization") auth:String, @Query("since") since:Long): PullResp
}

data class ChatMsg(val role:String, val content:String)
data class ChatReq(val model:String, val messages:List<ChatMsg})
data class ChatChoice(val index:Int, val message:ChatMsg)
data class ChatResp(val choices:List<ChatChoice> = emptyList())

interface OpenAiApi {
    @POST("/v1/chat/completions")
    suspend fun chat(@Header("Authorization") auth:String, @Body req:ChatReq): ChatResp
}

fun okHttpWithProxy(proxyType:Int, host:String, port:Int, user:String="", pass:String=""): OkHttpClient {
    val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    val b = OkHttpClient.Builder().addInterceptor(log)
    if (proxyType in 1..3 && host.isNotBlank() && port>0){
        val p = when(proxyType){
            1,2 -> Proxy(Proxy.Type.HTTP, InetSocketAddress(host, port))
            3 -> Proxy(Proxy.Type.SOCKS, InetSocketAddress(host, port))
            else -> null
        }
        if (p!=null) b.proxy(p)
        if (user.isNotBlank()) {
            b.proxyAuthenticator { _, response ->
                val cred = Credentials.basic(user, pass)
                response.request.newBuilder().header("Proxy-Authorization", cred).build()
            }
        }
    }
    return b.build()
}

fun retrofitFor(baseUrl:String, client:OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()
