package com.fourofourfound.aims_delivery

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

//this class is responsible for saving and retrieving any key value pair
class CustomSharedPreferences(context: Context)
{

//create a shared preferences
    private var sharedPreference: SharedPreferences
    init {
        // this is equivalent to using deprecated MasterKeys.AES256_GCM_SPEC
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .build()

        //key used for encryption
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
         sharedPreference  =  EncryptedSharedPreferences.create(
             context,
             "encrypted_shared_preferences",
             masterKey,
             EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
             EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
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






