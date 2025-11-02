
package com.example.pocketassistant.net
import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
class OpenAiService(private val app: Application){
  private val json = "application/json; charset=utf-8".toMediaType()
  suspend fun testConnectivity(): Pair<Boolean,String> = withContext(Dispatchers.IO){
    try{
      val s = SettingsRepository(app).load()
      val base = s.baseUrl.trimEnd('/')
      val client = NetworkClient(app).client()
      val req = Request.Builder().url("$base/v1/models").header("Authorization","Bearer "+(if(s.useGatewayKey) "" else s.apiKey)).build()
      val resp = client.newCall(req).execute()
      Pair(resp.isSuccessful, "HTTP "+resp.code)
    }catch(e:Exception){ Pair(false, e.message?:"error") }
  }
  suspend fun parseTodo(text:String): ParsedTodo = withContext(Dispatchers.IO){
    val s = SettingsRepository(app).load()
    val base = s.baseUrl.trimEnd('/')
    val client = NetworkClient(app).client()
    val prompt = "请把用户的一句话整理为JSON:{\"title\":...,\"priority\":0|1|2,\"minutes_from_now\":整数,\"note\":...} 只输出JSON。用户输入:"+text
    val body = JSONObject()
      .put("model", s.model)
      .put("messages", org.json.JSONArray()
        .put(JSONObject().put("role","system").put("content","You are a helpful assistant."))
        .put(JSONObject().put("role","user").put("content", prompt)))
      .put("temperature", 0.2)
    try{
      val req = Request.Builder().url("$base/v1/chat/completions").header("Authorization","Bearer "+(if(s.useGatewayKey) "" else s.apiKey)).post(body.toString().toRequestBody(json)).build()
      val resp = client.newCall(req).execute()
      val txt = resp.body?.string() ?: ""
      val content = JSONObject(txt).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
      val obj = JSONObject(content)
      ParsedTodo(obj.optString("title", text.take(30)), obj.optInt("priority",1), obj.optInt("minutes_from_now",60), obj.optString("note",""))
    }catch(e:Exception){
      ParsedTodo(text.take(30),1,60,"解析失败，使用默认")
    }
  }
}
data class ParsedTodo(val title:String,val priority:Int,val minutesFromNow:Int,val note:String)
