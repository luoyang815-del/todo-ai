
package com.aihelper.app
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.map
val Context.dataStore by preferencesDataStore("prefs")
object Keys {
  val SERVER = stringPreferencesKey("server")
  val GATEWAY = stringPreferencesKey("gateway")
  val PROXY_TYPE = intPreferencesKey("proxy_type")
  val PROXY_HOST = stringPreferencesKey("proxy_host")
  val PROXY_PORT = intPreferencesKey("proxy_port")
  val PROXY_USER = stringPreferencesKey("proxy_user")
  val PROXY_PASS = stringPreferencesKey("proxy_pass")
  val LAST_SYNC_TODO = longPreferencesKey("last_sync_todo")
  val LAST_SYNC_MSG  = longPreferencesKey("last_sync_msg")
}
fun encryptedPrefs(ctx: Context) = EncryptedSharedPreferences.create(ctx,"secure_prefs",MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
suspend fun saveServer(ctx: Context, v: String) = ctx.dataStore.edit { it[Keys.SERVER] = v }
suspend fun saveGateway(ctx: Context, v: String) = ctx.dataStore.edit { it[Keys.GATEWAY] = v }
suspend fun saveProxy(ctx: Context, type:Int, host:String, port:Int, user:String, pass:String) = ctx.dataStore.edit { it[Keys.PROXY_TYPE]=type; it[Keys.PROXY_HOST]=host; it[Keys.PROXY_PORT]=port; it[Keys.PROXY_USER]=user; it[Keys.PROXY_PASS]=pass }
suspend fun saveLastSyncTodo(ctx: Context, ts:Long) = ctx.dataStore.edit { it[Keys.LAST_SYNC_TODO] = ts }
suspend fun saveLastSyncMsg(ctx: Context, ts:Long) = ctx.dataStore.edit { it[Keys.LAST_SYNC_MSG] = ts }
fun serverFlow(ctx: Context) = ctx.dataStore.data.map { it[Keys.SERVER] ?: "http://127.0.0.1:8000" }
fun gatewayFlow(ctx: Context) = ctx.dataStore.data.map { it[Keys.GATEWAY] ?: "https://api.openai.com" }
fun proxyFlow(ctx: Context) = ctx.dataStore.data.map { listOf(it[Keys.PROXY_TYPE]?:0, it[Keys.PROXY_HOST]?:"", it[Keys.PROXY_PORT]?:0, it[Keys.PROXY_USER]?:"", it[Keys.PROXY_PASS]?:"") }
fun lastSyncTodoFlow(ctx: Context) = ctx.dataStore.data.map { it[Keys.LAST_SYNC_TODO] ?: 0L }
fun lastSyncMsgFlow(ctx: Context) = ctx.dataStore.data.map { it[Keys.LAST_SYNC_MSG] ?: 0L }
