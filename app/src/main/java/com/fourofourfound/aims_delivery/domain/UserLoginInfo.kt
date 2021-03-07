package com.fourofourfound.aims_delivery.domain
/**
 * User login info
 *simple data class to send the login info to the server
 * @property email email to be sent to the server
 * @property password password to be sent to the server
 * @constructor Create empty User login info
 */
data class UserLoginInfo(val email:String,val password:String)