package com.androidapp.navweiandroidv2.util.helper

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by S.Nur Uysal on 2020-01-07.
 */

class SharedPreference(val context: Context) {
    private val PREFS_NAME = "com.androidapp.navweiandroidv2"
    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    fun save(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status!!)
        editor.commit()
    }

    fun getValue(KEY_NAME: String): Boolean? {
        return sharedPref.getBoolean(KEY_NAME, true)
    }

}
