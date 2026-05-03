package com.ggg.kt.wanandroidbyyohen.app

import android.app.Application

class WanAndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
    }
}