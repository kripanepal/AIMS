package com.fourofourfound.aims_delivery.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.network.Driver
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

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

    lateinit var loggedInDriver: Driver


    /**
     * Save user
     * save the user to the shared preferences encrypting the given username and password
     * @param userName
     * @param password
     */
    private fun saveUser(
       driver:Driver,password:String
    ) {
        CustomSharedPreferences(myApplication).apply {
            setEncryptedPreference("driverCode", driver.code)
            setEncryptedPreference("password", password)
            setEncryptedPreference("id", driver.id.toString())
            setEncryptedPreference("name", driver.driverName)
            setEncryptedPreference("companyId",driver.companyID.toString())
            setEncryptedPreference("driverDescription", driver.driverDescription)
            setEncryptedPreference("compasDriverId", driver.compasDriverID.toString())
            setEncryptedPreference("truckId", driver.truckId.toString())
            setEncryptedPreference("truckDescription", driver.truckDescription.toString())
            setEncryptedPreference("active", driver.active.toString())
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
            val code = getEncryptedPreference("driverCode")
            val id = getEncryptedPreference("id")
            val name = getEncryptedPreference("name")
            val companyId = getEncryptedPreference("companyId")
            val driverDescription = getEncryptedPreference("driverDescription")
            val compasDriverId = getEncryptedPreference("compasDriverId")
            val truckId =  getEncryptedPreference("truckId")
            val truckDescription = getEncryptedPreference("truckDescription")
            val active = getEncryptedPreference("active")
            return try {
                loggedInDriver = Driver(id.toInt() ,companyId.toInt(),code,name,driverDescription,compasDriverId,truckId.toInt(),truckDescription,active.toBoolean())
                true
            } catch (e:Exception) {
                false
            }
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
                val driver = MakeNetworkCall.retrofitService.validateUser(
                    userName.value.toString()
                ).data.resultSet1
                if (driver.isNotEmpty()) {
                    loggedInDriver = driver[0]
                    _navigate.value = true
                    _loading.value = false
                    saveUser(loggedInDriver, password.value.toString())
                } else {
                    _errorMessage.value = "No driver associated with this code"
                }
            } catch (t: HttpException) {
                _errorMessage.value = "Connection failed"
            }
            catch (e: UnknownHostException) {
                _errorMessage.value = "Connection failed"
            }
            catch (e: Exception) {
               e.printStackTrace()
                _errorMessage.value = "Something Went Wrong"
            }
            finally {
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