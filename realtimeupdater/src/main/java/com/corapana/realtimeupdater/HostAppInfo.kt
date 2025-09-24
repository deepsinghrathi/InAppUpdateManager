package com.corapana.realtimeupdater

import android.content.Context
import android.graphics.drawable.Drawable

object HostAppInfo {
    var appIcon: Drawable? = null
        private set

    fun init(context: Context) {
        try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(context.packageName, 0)
            appIcon = pm.getApplicationIcon(appInfo)
        } catch (e: Exception) {
            e.printStackTrace()
            appIcon = null
        }
    }
}
