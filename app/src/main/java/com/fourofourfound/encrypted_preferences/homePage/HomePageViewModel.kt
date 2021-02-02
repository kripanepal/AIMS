package com.fourofourfound.encrypted_preferences.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.encrypted_preferences.CustomSharedPreferences

class HomePageViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val userLoggedIn:LiveData<Boolean>
        get() = _userLoggedIn

    fun logoutUser() {
         CustomSharedPreferences(myApplication).apply {
            deleteEncryptedPreference("username")
             deleteEncryptedPreference("password")
         }
        _userLoggedIn.value = false
    }



}