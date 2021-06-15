package com.rathoreapps.smartring.database

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) {
    companion object {
        const val PREFERENCE_FILE_NAME = "user"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_FILE_NAME, Activity.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun putPreferences(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    fun putPreferences(key: String, value: Boolean?) {
        value?.let { editor.putBoolean(key, it).apply() }
    }

    fun putPreferences(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun putPreferences(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun getPreference(key: String, defValue: String): String? {
        return sharedPreferences.getString(key, defValue)
    }

    fun getPreference(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    fun getPreference(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun getPreference(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    @Synchronized
    operator fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    fun deleteAllData() {
        editor.clear()
        editor.apply()
    }
}