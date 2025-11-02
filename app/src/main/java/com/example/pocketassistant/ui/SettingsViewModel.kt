
package com.example.pocketassistant.ui
import android.app.Application
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
private val Application.dataStore by preferencesDataStore("settings")
class SettingsViewModel(app: Application): AndroidViewModel(app){
  private val ds = app.dataStore
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
  val useGatewayKey:Flow<Boolean> = ds.data.map{ it[Keys.USE_GATEWAY_KEY]?:true }
  val apiKey:Flow<String> = ds.data.map{ it[Keys.API_KEY]?:"" }
  val baseUrl:Flow<String> = ds.data.map{ it[Keys.BASE_URL]?:"https://api.openai.com/" }
  val model:Flow<String> = ds.data.map{ it[Keys.MODEL]?:"gpt-4.1-mini" }
  val proxyType:Flow<String> = ds.data.map{ it[Keys.PROXY_TYPE]?:"NONE" }
  val proxyHost:Flow<String> = ds.data.map{ it[Keys.PROXY_HOST]?:"" }
  val proxyPort:Flow<Int> = ds.data.map{ it[Keys.PROXY_PORT]?:0 }
  val proxyUser:Flow<String> = ds.data.map{ it[Keys.PROXY_USER]?:"" }
  val proxyPass:Flow<String> = ds.data.map{ it[Keys.PROXY_PASS]?:"" }
  fun save(useGateway:Boolean, apiKey:String, baseUrl:String, model:String, proxyType:String, proxyHost:String, proxyPort:Int, proxyUser:String, proxyPass:String)=
    viewModelScope.launch {
      ds.edit{
        it[Keys.USE_GATEWAY_KEY]=useGateway; it[Keys.API_KEY]=apiKey; it[Keys.BASE_URL]=baseUrl; it[Keys.MODEL]=model
        it[Keys.PROXY_TYPE]=proxyType; it[Keys.PROXY_HOST]=proxyHost; it[Keys.PROXY_PORT]=proxyPort; it[Keys.PROXY_USER]=proxyUser; it[Keys.PROXY_PASS]=proxyPass
      }
    }
}
