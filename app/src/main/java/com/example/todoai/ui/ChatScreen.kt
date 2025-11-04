package com.example.todoai.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Composable
fun ChatScreen() {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("输入") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            scope.launch { response = askGPT(context, prompt) }
        }) {
            Text("发送")
        }
        Text(response, Modifier.padding(top = 16.dp))
    }
}

suspend fun askGPT(context: Context, prompt: String): String {
    return kotlinx.coroutines.withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("todo_ai_prefs", Context.MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", "") ?: return@withContext "无 API Key"

        val json = JSONObject().apply {
            put("model", "gpt-3.5-turbo-instruct")
            put("prompt", prompt)
            put("max_tokens", 200)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        val client = OkHttpClient()
        try {
            val resp = client.newCall(request).execute()
            if (!resp.isSuccessful) return@withContext "请求失败"
            val obj = JSONObject(resp.body?.string() ?: "")
            obj.getJSONArray("choices").getJSONObject(0).getString("text")
        } catch (e: IOException) {
            "错误: ${e.message}"
        }
    }
}
