package com.rathoreapps.smartring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import javax.inject.Singleton

@Singleton
class PhoneStateReceiver : BroadcastReceiver() {
    var audioManagerUtils: AudioManagerUtils? = null
    override fun onReceive(context: Context?, intent: Intent?) {

        if (audioManagerUtils == null) {
            audioManagerUtils = context?.let { AudioManagerUtils(it) }
        }

        try {
            println("Receiver start")
            Toast.makeText(context, " Receiver start ", Toast.LENGTH_SHORT).show()
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Toast.makeText(context, "Ringing State Number is -", Toast.LENGTH_SHORT).show();
                println("Receiver Ringing State Number is - $incomingNumber")
                //EventBus.getDefault().post(TelephonyManager.EXTRA_STATE_RINGING)

                audioManagerUtils?.startAutoRingVolume()
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                Toast.makeText(context, "Received State", Toast.LENGTH_SHORT).show();
                println("Receiver Received State - ")
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Toast.makeText(context, "Idle State", Toast.LENGTH_SHORT).show();
                println("Receiver Idle State ")
                //EventBus.getDefault().post(TelephonyManager.EXTRA_STATE_IDLE)
                audioManagerUtils?.stopAutoRingVolume()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}