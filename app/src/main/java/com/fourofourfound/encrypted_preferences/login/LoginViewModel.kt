package com.fourofourfound.encrypted_preferences.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fourofourfound.encrypted_preferences.CustomSharedPreferences

class LoginViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application

    fun saveUser(userName:String, password:String) {
         CustomSharedPreferences(myApplication).apply {
            setEncryptedPreference("username", userName)
            setEncryptedPreference("password", password)
         }
    }

    fun checkUserLoggedIn(): Boolean {
        CustomSharedPreferences(myApplication).apply {
            val userName = getEncryptedPreference("username")
            val password = getEncryptedPreference("password")
            if(userName!="" && password!="") return true
            return false
        }
    }

}