package com.corapana.realtimeupdater

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

//{
//  "latestVersionCode": 12,
//  "latestVersionName": "2.4.0",
//  "whatsNew": "✅ Dark mode\n🐞 Bug fixes\n⚡ Faster load time",
//  "apkUrl": "https://yourserver.com/apks/app-release-v12.apk",
//  "forceUpdate": true
//}
object RealtimeUpdater {

    private const val PREF_SHOWN_SUCCESS = "update_success_shown"

    @JvmStatic
    fun init(context: Context, updateUrl: String) {
        val appContext = context.applicationContext as Application
        Log.d("RealtimeUpdater", "Init called with URL = $updateUrl")

        HostAppInfo.init(context)
        val appIcon = HostAppInfo.appIcon?.let { drawableToBitmap(it) } ?: drawableToBitmap(
            ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!)


        appContext.unregisterActivityLifecycleCallbacks(ActivityTracker)
        appContext.registerActivityLifecycleCallbacks(ActivityTracker)

        // 1. Check if update just installed
        val currentVersion = getAppVersionCode(appContext)
        Log.d("RealtimeUpdater", "Current app version = $currentVersion")

        if (UpdatePrefs.getLastInstalledVersion(appContext) != currentVersion) {
            Log.d("RealtimeUpdater", "Detected new install/update. Last = ${UpdatePrefs.getLastInstalledVersion(appContext)}")

            // Show success once
            if (!UpdatePrefs.wasSuccessShown(appContext)) {
                Log.d("RealtimeUpdater", "Showing success toast for version $currentVersion")
                Toast.makeText(appContext, "App updated successfully 🎉", Toast.LENGTH_LONG).show()
                UpdatePrefs.setLastInstalledVersion(appContext, currentVersion)
                UpdatePrefs.setSuccessShown(appContext, true)
            } else {
                Log.d("RealtimeUpdater", "Success toast already shown")
            }
        } else {
            Log.d("RealtimeUpdater", "No new install/update detected")
        }

        // 2. Check for updates from server
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("RealtimeUpdater", "Checking server for updates...")
            val info = checkForUpdate(appContext, updateUrl)
            withContext(Dispatchers.Main) {
                if (info != null) {
                    Log.d("RealtimeUpdater", "Update available: version=${info.latestVersionCode}, force=${info.forceUpdate}")
                    UpdateActivity.launch(appContext, info,appIcon)
                } else {
                    Log.d("RealtimeUpdater", "No update available")
                }
            }
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    private suspend fun checkForUpdate(context: Context, url: String): UpdateInfo? {
        return try {
            val json = URL(url).readText()
            val info = Gson().fromJson(json, UpdateInfo::class.java)
            val currentVersion = getAppVersionCode(context)
            if (info.latestVersionCode > currentVersion) info else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getAppVersionCode(context: Context): Int {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            -1
        }
    }

}

