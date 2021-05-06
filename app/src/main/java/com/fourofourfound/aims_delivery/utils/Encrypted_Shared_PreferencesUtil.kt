package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Custom Shared Preferences
 * This class is responsible for saving and retrieving any key value pair
 * from the file system.
 * @constructor
 * @param context the current context of the application
 */
class CustomSharedPreferences(context: Context) {

    /**
     * Creates a shared preferences
     */
    private var sharedPreference: SharedPreferences

    init {
        //key used for encryption
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        //uses the defined scheme to encrypt the keys
        sharedPreference = EncryptedSharedPreferences.create(
            context,
            "encrypted-shared-preferences",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    }

    /**
     * Set Encrypted Preference
     * This method saves the key and value pair and encrypts it
     * @param key unique key assigned to a value
     * @param value value attached with the key
     */
    fun setEncryptedPreference(key: String, value: String) {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Get Encrypted Preference
     * This methods returns decrypted value for any given key
     * @param key unique key whose value to be returned
     * @return decrypted value for the given key
     */
    fun getEncryptedPreference(key: String): String {
        val result = sharedPreference.getString(key, "")
        return result.toString()
    }

    /**
     * Delete Encrypted Preference
     * This method deletes the key and value from the file system
     * @param key unique key whose value to be deleted
     * @return true if value is deleted
     */
    fun deleteEncryptedPreference(key: String): Boolean {
        sharedPreference.edit().apply {
            remove(key)
            apply()
        }
        return sharedPreference.contains(key)
    }

}