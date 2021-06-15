package com.rathoreapps.smartring.dagger

import com.rathoreapps.smartring.AudioManagerUtils
import com.rathoreapps.smartring.application.SmartRingApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class])
interface ApplicationComponent {

    fun inject(smartRingApplication: SmartRingApplication)
    fun inject(audioManagerUtils: AudioManagerUtils)

}