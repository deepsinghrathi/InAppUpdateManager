package com.corapana.realtimeupdater

import android.content.Context

object UpdatePrefs {
    private const val PREF_NAME = "update_prefs"
    private const val KEY_LAST_VERSION = "last_version"
    private const val KEY_SUCCESS_SHOWN = "success_shown"

    fun setLastInstalledVersion(context: Context, version: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_LAST_VERSION, version).apply()
    }

    fun getLastInstalledVersion(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_LAST_VERSION, -1)
    }

    fun wasSuccessShown(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_SUCCESS_SHOWN, false)
    }

    fun setSuccessShown(context: Context, shown: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SUCCESS_SHOWN, shown).apply()
    }
}

