package com.corapana.realtimeupdater

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity

internal object ActivityTracker : Application.ActivityLifecycleCallbacks {

    private var currentActivity: ComponentActivity? = null

    fun getTopActivity(): ComponentActivity? = currentActivity

    override fun onActivityResumed(activity: Activity) {
        if (activity is ComponentActivity) {
            currentActivity = activity
            Log.d("RealtimeUpdater", "Top activity set: ${activity.localClassName}")
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity == currentActivity) {
            Log.d("RealtimeUpdater", "Top activity cleared: ${activity.localClassName}")
            currentActivity = null
        }
    }

    // Other lifecycle methods required but not used
    override fun onActivityCreated(a: Activity, s: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (activity is ComponentActivity) {
            currentActivity = activity
            Log.d("RealtimeUpdater", "Top activity set: ${activity.localClassName}")
        }
    }
    override fun onActivityStopped(activity: Activity) {
        if (activity == currentActivity) {
            Log.d("RealtimeUpdater", "Top activity cleared: ${activity.localClassName}")
            currentActivity = null
        }
    }
    override fun onActivitySaveInstanceState(a: Activity, out: Bundle) {}
    override fun onActivityDestroyed(a: Activity) {}
}
