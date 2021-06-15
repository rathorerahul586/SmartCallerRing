package com.rathoreapps.smartring.application

import android.app.Application
import android.content.Context
import com.rathoreapps.smartring.dagger.ApplicationComponent
import com.rathoreapps.smartring.dagger.DaggerApplicationComponent
import com.rathoreapps.smartring.dagger.MainModule
import com.rathoreapps.smartring.database.SharedPreference

class SmartRingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applicationComponent =
            DaggerApplicationComponent.builder().mainModule(MainModule(applicationContext)).build()
        injectDagger()
        appContext = applicationContext
    }

    private fun injectDagger() {
        applicationComponent.inject(this)
    }

    fun getInstance(): SmartRingApplication {
        return instance
    }

    companion object {
        const val TAG = "Smart Ring Application Debug Tag"
        var instance: SmartRingApplication = SmartRingApplication()
        lateinit var applicationComponent: ApplicationComponent
        lateinit var appContext: Context
    }
}