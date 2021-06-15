package com.rathoreapps.smartring.dagger

import android.content.Context
import com.rathoreapps.smartring.application.SmartRingApplication
import com.rathoreapps.smartring.database.SharedPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule(var context: Context) {
    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideSmartRingApplication(): SmartRingApplication {
        return SmartRingApplication()
    }

    @Singleton
    @Provides
    fun sharedPref(): SharedPreference {
        return SharedPreference(context)
    }
}