package com.corapana.realtimeupdater.app

import android.app.Application
import com.corapana.realtimeupdater.RealtimeUpdater

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // init updater once when app starts
        RealtimeUpdater.init(this, "https://prateektraders.com/kuka.json")
    }
}
