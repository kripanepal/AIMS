package com.fourofourfound.aims_delivery

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


//this class is responsible for saving and retrieving any key value pair
class CustomSharedPreferences(context: Context)
{

    //create a shared preferences
    private var sharedPreference: SharedPreferences
    init {
        // this is equivalent to using deprecated MasterKeys.AES256_GCM_SPEC

        //key used for encryption
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreference = EncryptedSharedPreferences.create(
            context,
            "encrypted-shared-preferences1",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    }
    //saves key and value pair
    fun setEncryptedPreference(key: String, value: String)
    {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    //returns the value for any given key
    fun getEncryptedPreference(key: String):String
    {
        val result =  sharedPreference.getString(key, "")
        return result.toString()
    }

    //deletes the key and value
    fun deleteEncryptedPreference(key: String):Boolean
    {
        sharedPreference.edit().apply {
            remove(key)
            apply()
        }
        return sharedPreference.contains(key)
    }

}