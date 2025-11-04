package com.example.todoai.settings

import android.content.Context

data class AppSettings(
    var endpoint: String = "",
    var apiKey: String = "",
    var model: String = "",
    var useProxy: Boolean = false,
    var proxyType: String = "HTTP",
    var proxyHost: String = "",
    var proxyPort: Int = 0,
    var proxyUser: String = "",
    var proxyPass: String = ""
) {
    companion object {
        private const val PREFS = "todoai_settings"

        fun load(context: Context): AppSettings {
            val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            return AppSettings(
                endpoint = p.getString("endpoint", "") ?: "",
                apiKey = p.getString("apiKey", "") ?: "",
                model = p.getString("model", "") ?: "",
                useProxy = p.getBoolean("useProxy", false),
                proxyType = p.getString("proxyType", "HTTP") ?: "HTTP",
                proxyHost = p.getString("proxyHost", "") ?: "",
                proxyPort = try { p.getInt("proxyPort", 0) } catch (_: Throwable) { 0 },
                proxyUser = p.getString("proxyUser", "") ?: "",
                proxyPass = p.getString("proxyPass", "") ?: ""
            )
        }

        fun save(context: Context, s: AppSettings) {
            val e = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            e.putString("endpoint", s.endpoint)
            e.putString("apiKey", s.apiKey)
            e.putString("model", s.model)
            e.putBoolean("useProxy", s.useProxy)
            e.putString("proxyType", s.proxyType)
            e.putString("proxyHost", s.proxyHost)
            e.putInt("proxyPort", s.proxyPort)
            e.putString("proxyUser", s.proxyUser)
            e.putString("proxyPass", s.proxyPass)
            e.apply()
        }
    }
}
