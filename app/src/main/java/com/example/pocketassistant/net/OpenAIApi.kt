
package com.example.pocketassistant.net

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OpenAIApi {
    @POST("v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") bearer: String,
        @Body body: Map<String, Any>
    ): Map<String, Any>

    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun whisper(
        @Header("Authorization") bearer: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody
    ): Map<String, Any>
}
