
package com.aihelper.app.core
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
object Sync {
  interface Api {
    @POST("/sync/push") suspend fun push(@Header("Authorization") token: String, @Body body: Map<String, Any>): Map<String, Any>
    @POST("/sync/pull") suspend fun pull(@Header("Authorization") token: String, @Body body: Map<String, Any>): Map<String, Any>
  }
  private fun client(): OkHttpClient {
    val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    return OkHttpClient.Builder().addInterceptor(log).build()
  }
  fun api(baseUrl: String): Api =
    Retrofit.Builder().baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
      .addConverterFactory(MoshiConverterFactory.create()).client(client()).build().create(Api::class.java)
}
