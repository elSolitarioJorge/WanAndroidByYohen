package com.ggg.kt.wanandroidbyyohen.app

import android.app.Application

object AppContext {
    lateinit var application: Application
        private set

    fun init(app: Application) {
        application = app
    }
}