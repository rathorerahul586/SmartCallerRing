package com.rathoreapps.smartring

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rathoreapps.smartring.database.SharedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioManagerUtils(context: Context) {
    var appSharedPreference: SharedPreference = SharedPreference(context)

    val audioManager = context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
    var initialVolumeLevel: Int? = 0
    var isRinging = false

    private fun setVolumeLevel(level: Int) {
        if (isRinging && level == audioManager.getStreamVolume(AudioManager.STREAM_RING)) return

        Log.d(
            "TAG",
            "setVolumeLevel $level:before-> ${audioManager.getStreamVolume(AudioManager.STREAM_RING)}"
        )
        audioManager.setStreamVolume(AudioManager.STREAM_RING, level, 0)
        Log.d(
            "TAG",
            "setVolumeLevel:after-> ${audioManager.getStreamVolume(AudioManager.STREAM_RING)}"
        )
        if (isRinging) {
            Thread.sleep(500)
            checkNoiseAndSetVolume()
        }
    }

    private fun resetRingVolume() {
        Log.d("TAG", "resetRingVolume -> initialVolumeLevel $initialVolumeLevel")
        initialVolumeLevel?.let { audioManager.setStreamVolume(AudioManager.STREAM_RING, it, 0) }
    }

    fun checkNoiseAndSetVolume() {
        CoroutineScope(Dispatchers.Main).launch {
            val amplitude = getAmplitude()
            Log.d("TAG", "checkNoiseAndSetVolume: $amplitude")
            when (amplitude) {
                in 0..3000 -> setVolumeLevel(1)
                in 3001..6000 -> setVolumeLevel(2)
                in 6001..8000 -> setVolumeLevel(3)
                in 8001..11000 -> setVolumeLevel(4)
                in 11001..14000 -> setVolumeLevel(5)
                in 14001..17000 -> setVolumeLevel(6)
                else -> setVolumeLevel(7)
            }
        }
    }

    private var mRecorder: MediaRecorder? = null

    fun startRecorder() {
        if (mRecorder == null) {
            mRecorder = MediaRecorder()
            mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR)
            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mRecorder?.setOutputFile("/dev/null")
            mRecorder?.prepare()
            mRecorder?.start()
        }
    }

    private fun stopRecorder() {
        if (mRecorder != null) {
            mRecorder?.stop()
            mRecorder?.release()
        }
    }

    private fun getAmplitude(): Int {
        //20 * log10(mRecorder?.maxAmplitude?.div(2700.0) ?: 0.0)
        return mRecorder?.maxAmplitude ?: 0
    }


    fun startAutoRingVolume() {
        appSharedPreference.putPreferences(
            "initial_volume",
            audioManager.getStreamVolume(AudioManager.STREAM_RING)
        )
        Log.d("TAG", "startAutoRingVolume -> initialVolumeLevel: $initialVolumeLevel")
        startRecorder()
        isRinging = true
        checkNoiseAndSetVolume()
    }

    fun stopAutoRingVolume() {
        isRinging = false
        resetRingVolume()
        stopRecorder()
        mRecorder = null
    }

    init {
        //SmartRingApplication.applicationComponent.inject(this)

        initialVolumeLevel = appSharedPreference.getPreference("initial_volume", 0)
    }
}