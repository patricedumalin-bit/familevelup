package com.slydroidstudio.familevelup.security

import android.content.Context
import android.content.SharedPreferences
import com.slydroidstudio.familevelup.BuildConfig

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("familevelup_prefs", Context.MODE_PRIVATE)

    var biometricEnabled: Boolean
        get() = prefs.getBoolean("biometric_enabled", false)
        set(value) = prefs.edit().putBoolean("biometric_enabled", value).apply()

    var isLicensed: Boolean
        get() = prefs.getBoolean("is_licensed", true) || BuildConfig.DEBUG
        set(value) {
            prefs.edit().putBoolean("is_licensed", value).commit()
        }

    var selectedTheme: String
        get() = prefs.getString("selected_theme", "OCEAN") ?: "OCEAN"
        set(value) = prefs.edit().putString("selected_theme", value).apply()
}
