package com.fourofourfound.aims_delivery.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.network.UserLoginInfo
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) :AndroidViewModel(application) {

    //required variables
    private val myApplication = application

    private val _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean>
        get() = _navigate


    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    val userName= MutableLiveData<String>()
    val password= MutableLiveData<String>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage


    //save the user to the shared preferences encrypting the given username and password
    private fun saveUser(userName:String, password:String) {
         CustomSharedPreferences(myApplication).apply {
            setEncryptedPreference("username", userName)
            setEncryptedPreference("password", password)
         }
    }

    //checks if shared preferences already contain a user that is logged in
    fun checkUserLoggedIn(): Boolean {
        CustomSharedPreferences(myApplication).apply {
             val userName = getEncryptedPreference("username")
             val password = getEncryptedPreference("password")
            if(userName!="" && password!="") return true
            return false
        }
    }

    //calls retrofit service to verify the user
    fun authenticateUser()
    {
        _loading.value = true
        viewModelScope.launch {
           try{
                MakeNetworkCall.retrofitService.validateUser(UserLoginInfo(userName.value.toString(),password.value.toString()))
                _navigate.value = true
               _loading.value = false
               saveUser(userName.value.toString(),password.value.toString())
            }
            catch (t:Throwable)
            {
                _errorMessage.value = t.message
               _navigate.value = false
                _loading.value = false
            }
        }
    }

    //callback to ensure that the navigation is not triggered twice
    fun doneNavigatingToHomePage()
    {
        _navigate.value = false
    }


}