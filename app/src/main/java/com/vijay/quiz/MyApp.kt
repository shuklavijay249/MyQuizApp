package com.vijay.quiz

import FlagRepository
import android.app.Application

class MyApp : Application() {
    val repository by lazy { FlagRepository(this) }

    override fun onCreate() {
        super.onCreate()
        // Any additional initialization can be done here
    }
}
