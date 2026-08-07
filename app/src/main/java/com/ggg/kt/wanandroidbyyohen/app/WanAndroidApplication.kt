package com.ggg.kt.wanandroidbyyohen.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.StrictMode

class WanAndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        if (isDebuggable()) {
            enableStrictMode()
        }
    }

    private fun isDebuggable() : Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }
}