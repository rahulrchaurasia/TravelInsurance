package com.interstellar.travelInsurance.core.facade

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject




class SharedPreferenceManager @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "TravelInsurance"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val INITIAL_PIN = "INITIAL_PIN"
        // Add other keys as needed
    }

    private fun getEditor(): SharedPreferences.Editor {
        return sharedPref.edit()
    }

    fun savePin(pin : String){

        getEditor().putString(INITIAL_PIN, pin).apply()

    }

    fun getPin()
            = sharedPref.getString(INITIAL_PIN, "")

    fun setLoggedIn(isLoggedIn: Boolean) {
        getEditor().putBoolean(KEY_IS_LOGGED_IN ,isLoggedIn).apply()
        //getEditor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean = sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearData() {
        getEditor().clear().apply()
    }


}