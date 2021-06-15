package com.rathoreapps.smartring

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.widget.Toast


class BroadcastService : Service() {


    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        val filter = IntentFilter(Intent.ACTION_CALL)
        registerReceiver(PhoneStateReceiver(), filter)
        Toast.makeText(this, "BroadcastService-> onCreate", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {}

}