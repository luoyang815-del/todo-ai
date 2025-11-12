package com.example.todoai.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("todoai_prefs")

data class AppConfig(
    val proxyEnabled: Boolean = false,
    val proxyType: String = "http",
    val proxyHost: String = "",
    val proxyPort: Int = 0,
    val proxyAuthEnabled: Boolean = false,
    val proxyUsername: String = "",
    val proxyPassword: String = "",
    val gatewayEnabled: Boolean = true,
    val baseUrl: String = "https://gw.example.com",
    val tlsVerify: Boolean = true,
    val syncMode: String = "manual" // manual/auto/off
)

object SettingsStore {
    private val KEY_PROXY_ENABLED = booleanPreferencesKey("proxy_enabled")
    private val KEY_PROXY_TYPE = stringPreferencesKey("proxy_type")
    private val KEY_PROXY_HOST = stringPreferencesKey("proxy_host")
    private val KEY_PROXY_PORT = intPreferencesKey("proxy_port")
    private val KEY_PROXY_AUTH = booleanPreferencesKey("proxy_auth")
    private val KEY_PROXY_USER = stringPreferencesKey("proxy_user")
    private val KEY_PROXY_PASS = stringPreferencesKey("proxy_pass")
    private val KEY_GATEWAY_ENABLED = booleanPreferencesKey("gateway_enabled")
    private val KEY_BASE_URL = stringPreferencesKey("base_url")
    private val KEY_TLS_VERIFY = booleanPreferencesKey("tls_verify")
    private val KEY_SYNC_MODE = stringPreferencesKey("sync_mode")

    suspend fun read(ctx: Context): AppConfig {
        val p = ctx.dataStore.data.first()
        return AppConfig(
            proxyEnabled = p[KEY_PROXY_ENABLED] ?: false,
            proxyType = p[KEY_PROXY_TYPE] ?: "http",
            proxyHost = p[KEY_PROXY_HOST] ?: "",
            proxyPort = p[KEY_PROXY_PORT] ?: 0,
            proxyAuthEnabled = p[KEY_PROXY_AUTH] ?: false,
            proxyUsername = p[KEY_PROXY_USER] ?: "",
            proxyPassword = p[KEY_PROXY_PASS] ?: "",
            gatewayEnabled = p[KEY_GATEWAY_ENABLED] ?: true,
            baseUrl = p[KEY_BASE_URL] ?: "https://gw.example.com",
            tlsVerify = p[KEY_TLS_VERIFY] ?: true,
            syncMode = p[KEY_SYNC_MODE] ?: "manual",
        )
    }

    suspend fun write(ctx: Context, cfg: AppConfig) {
        ctx.dataStore.edit { p ->
            p[KEY_PROXY_ENABLED] = cfg.proxyEnabled
            p[KEY_PROXY_TYPE] = cfg.proxyType
            p[KEY_PROXY_HOST] = cfg.proxyHost
            p[KEY_PROXY_PORT] = cfg.proxyPort
            p[KEY_PROXY_AUTH] = cfg.proxyAuthEnabled
            p[KEY_PROXY_USER] = cfg.proxyUsername
            p[KEY_PROXY_PASS] = cfg.proxyPassword
            p[KEY_GATEWAY_ENABLED] = cfg.gatewayEnabled
            p[KEY_BASE_URL] = cfg.baseUrl
            p[KEY_TLS_VERIFY] = cfg.tlsVerify
            p[KEY_SYNC_MODE] = cfg.syncMode
        }
    }
}
