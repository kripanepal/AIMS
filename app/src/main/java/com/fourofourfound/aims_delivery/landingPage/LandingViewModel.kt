package com.fourofourfound.aims_delivery.landingPage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fourofourfound.aims_delivery.CustomSharedPreferences

class LandingViewModel(application: Application) : AndroidViewModel(application){
    //checks if shared preferences already contain a user that is logged in
    private var myApplication = application

    fun checkUserLoggedIn(): Boolean {
        CustomSharedPreferences(myApplication).apply {
            val userName = getEncryptedPreference("username")
            val password = getEncryptedPreference("password")
            if(userName!="" && password!="") return true
            return false
        }
    }
}