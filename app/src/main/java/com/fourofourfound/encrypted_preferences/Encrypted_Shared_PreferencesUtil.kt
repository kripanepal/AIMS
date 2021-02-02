package com.fourofourfound.encrypted_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
class CustomSharedPreferences(context: Context)
{
    private var sharedPreference: SharedPreferences
    init {
        var masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

         sharedPreference  = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    fun setEncryptedPreference(key:String,value:String)
    {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getEncryptedPreference(key:String):String
    {
        val result =  sharedPreference.getString(key,"" )
        return result.toString()
    }

    fun deleteEncryptedPreference(key:String):Boolean
    {
        sharedPreference.edit().apply {
            remove(key)
            apply()
        }
        return sharedPreference.contains(key)
    }



}






