package com.fourofourfound.aims_delivery.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.domain.UserLoginInfo
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import kotlinx.coroutines.launch

/**
 * Login view model
 *This view model is used by the Login Fragment to store the information about user login info.
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val myApplication = application

    /**
     * Inform login fragment if it needs to navigate to homepage
     */
    private val _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean>
        get() = _navigate

    /**
     * Inform login fragment if loading animation needs to be shown
     * if the user clicks login button.
     */
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    /**
     * Username of the user that needs to be checked with the database
     */
    val userName = MutableLiveData<String>()

    /**
     * Password of the user that needs to be checked with the database
     */
    val password = MutableLiveData<String>()

    /**
     * Informs the login fragments about the error if the user cannot be authenticated
     */
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    /**
     * Informs the login fragments when user field gains/loses focus
     */
    private val _userFieldTouched = MutableLiveData<Boolean>()
    val userFieldTouched: LiveData<Boolean>
        get() = _userFieldTouched

    /**
     * Informs the login fragments if password field gains/loses focus
     */
    private val _passwordFieldTouched = MutableLiveData<Boolean>()
    val passwordFieldTouched: LiveData<Boolean>
        get() = _passwordFieldTouched


    /**
     * Save user
     * save the user to the shared preferences encrypting the given username and password
     * @param userName
     * @param password
     */
    private fun saveUser(userName: String, password: String) {
        CustomSharedPreferences(myApplication).apply {
            setEncryptedPreference("username", userName)
            setEncryptedPreference("password", password)
        }
    }

    /**
     * Check user logged in
     * This method checks if shared preferences already contain a user
     * that is logged in
     *
     * @return if the user is logged in or not
     */

    fun checkUserLoggedIn(): Boolean {
        CustomSharedPreferences(myApplication).apply {
            val userName = getEncryptedPreference("username")
            val password = getEncryptedPreference("password")
            if (userName != "" && password != "") return true
            return false
        }
    }

    /**
     * Authenticate user
     * This method calls retrofit service to verify the user
     */
    fun authenticateUser() {
        if (checkFieldsEmpty()) return
        _loading.value = true
        viewModelScope.launch {
            try {
                MakeNetworkCall.retrofitService.validateUser(
                    UserLoginInfo(
                        userName.value.toString(),
                        password.value.toString()
                    )
                )
                _navigate.value = true
                _loading.value = false
                saveUser(userName.value.toString(), password.value.toString())
            } catch (t: Throwable) {
                _errorMessage.value = t.message
                _navigate.value = false
                _loading.value = false
            }
        }
    }

    /**
     * Check Fields Empty
     * This method checks if the user id or password field is empty
     *
     * @return if the text field is empty or not
     */
    private fun checkFieldsEmpty(): Boolean {
        if (userName.value.isNullOrEmpty() or password.value.isNullOrEmpty()) {
            if (userName.value.isNullOrEmpty()) {
                _userFieldTouched.value = true
            }
            if (password.value.isNullOrEmpty()) {
                _passwordFieldTouched.value = true
            }
            return true
        }
        return false
    }

    /**
     * This method ensures that the navigation is not
     * triggered twice
     */
    fun doneNavigatingToHomePage() {
        _navigate.value = false
    }

    /**
     * User id text changed
     * This method informs login fragment to hide error message
     * whenever user starts typing their user id
     */
    fun userIdTextChanged() {
        _userFieldTouched.value = false
    }

    /**
     * Password text changed
     * This method informs login fragment to hide error message
     * whenever user starts typing their password
     */
    fun passwordTextChanged() {
        _passwordFieldTouched.value = false
    }

}