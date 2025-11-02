
package com.example.pocketassistant.net
import android.app.Application
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
private val Application.dataStore by preferencesDataStore("settings")
data class Settings(val useGatewayKey:Boolean,val apiKey:String,val baseUrl:String,val model:String,
    val proxyType:String,val proxyHost:String,val proxyPort:Int,val proxyUser:String,val proxyPass:String)
class SettingsRepository(private val app: Application){
  object Keys{
    val USE_GATEWAY_KEY= booleanPreferencesKey("use_gateway_key")
    val API_KEY= stringPreferencesKey("api_key")
    val BASE_URL= stringPreferencesKey("base_url")
    val MODEL= stringPreferencesKey("model")
    val PROXY_TYPE= stringPreferencesKey("proxy_type")
    val PROXY_HOST= stringPreferencesKey("proxy_host")
    val PROXY_PORT= intPreferencesKey("proxy_port")
    val PROXY_USER= stringPreferencesKey("proxy_user")
    val PROXY_PASS= stringPreferencesKey("proxy_pass")
  }
  suspend fun load(): Settings {
    val p = app.dataStore.data.first()
    return Settings(p[Keys.USE_GATEWAY_KEY]?:true, p[Keys.API_KEY]?:"", p[Keys.BASE_URL]?:"https://api.openai.com/",
      p[Keys.MODEL]?:"gpt-4.1-mini", p[Keys.PROXY_TYPE]?:"NONE", p[Keys.PROXY_HOST]?:"", p[Keys.PROXY_PORT]?:0,
      p[Keys.PROXY_USER]?:"", p[Keys.PROXY_PASS]?:"")
  }
}
