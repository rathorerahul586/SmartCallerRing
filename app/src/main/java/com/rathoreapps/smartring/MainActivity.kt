package com.rathoreapps.smartring

import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    var audioManager: AudioManager? = null
    var initialVolumeLevel: Int? = 0
    var isRinging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        audioManager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        initViews()

    }

    private fun initViews() {
        EventBus.getDefault().register(this)
        check_noise.setOnClickListener { testNoise() }
    }

    private fun resetRingVolume() {
        Log.d("TAG", "resetRingVolume: $initialVolumeLevel")
        initialVolumeLevel?.let { audioManager?.setStreamVolume(AudioManager.STREAM_RING, it, 0) }
    }

    private fun setVolumeLevel(level: Int) {
        if (isRinging && level == audioManager?.getStreamVolume(AudioManager.STREAM_RING)) return

        Log.d(
            "TAG",
            "setVolumeLevel $level:before-> ${audioManager?.getStreamVolume(AudioManager.STREAM_RING)}"
        )
        audioManager?.setStreamVolume(AudioManager.STREAM_RING, level, 0)
        Log.d(
            "TAG",
            "setVolumeLevel:after-> ${audioManager?.getStreamVolume(AudioManager.STREAM_RING)}"
        )
        if (isRinging) {
            Thread.sleep(500)
            checkNoiseAndSetVolume()
        }
    }

    // use only for testing purpose. comment out checkNoiseAndSetVolume() in setVolumeLevel() while testing
    private fun testNoise() {
        initialVolumeLevel = audioManager?.getStreamVolume(AudioManager.STREAM_RING)
        Log.d("TAG", "initialVolumeLevel: $initialVolumeLevel")
        CoroutineScope(Dispatchers.Main).launch {
            startRecorder()
            isRinging = true

            repeat(40) {
                val amplitude = getAmplitude()
                Log.d("TAG", "checkNoiseAndSetVolume: $amplitude")
                when (amplitude) {
                    in 0..3000 -> setVolumeLevel(1)
                    in 3001..6000 -> setVolumeLevel(2)
                    in 8001..10000 -> setVolumeLevel(3)
                    in 10001..12000 -> setVolumeLevel(4)
                    in 12001..14000 -> setVolumeLevel(5)
                    in 14001..16000 -> setVolumeLevel(6)
                    else -> setVolumeLevel(7)
                }
                delay(1000)
            }
            isRinging = false
            resetRingVolume()
            stopRecorder()
            mRecorder = null
        }
    }

    private fun checkNoiseAndSetVolume() {
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

    private fun startRecorder() {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String?) {
        if (event == TelephonyManager.EXTRA_STATE_RINGING) {
            Log.d("TAG", "Call Incoming: $event}")
            initialVolumeLevel = audioManager?.getStreamVolume(AudioManager.STREAM_RING)
            startRecorder()
            isRinging = true
            checkNoiseAndSetVolume()
        } else if (event == TelephonyManager.EXTRA_STATE_IDLE) {
            isRinging = false
            resetRingVolume()
            stopRecorder()
            mRecorder = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}