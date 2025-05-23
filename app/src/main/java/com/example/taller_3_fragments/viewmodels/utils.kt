package com.example.taller_3_fragments.utils

import android.content.Context
import android.content.SharedPreferences

object UserProfileManager {

    private const val PREFS_NAME = "UserProfilePrefs"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_USER_PHONE = "userPhone"
    private const val KEY_USER_ADDRESS = "userAddress"
    private const val KEY_USER_IMAGE_URI = "userImageUri"
    private const val KEY_USER_PASSWORD = "userPassword"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun saveUserProfile(
        context: Context,
        name: String?,
        email: String?,
        phone: String?,
        address: String?,
        password: String?,
        imageUri: String?
    ) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_PHONE, phone)
        editor.putString(KEY_USER_ADDRESS, address)
        editor.putString(KEY_USER_PASSWORD, password)
        editor.putString(KEY_USER_IMAGE_URI, imageUri)
        editor.apply()
    }

    // Metodos para obtener
    fun getUserPassword(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_PASSWORD, null)
    }

    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_NAME, null)
    }

    fun getUserEmail(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_EMAIL, null)
    }

    fun getUserPhone(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_PHONE, null)
    }

    fun getUserAddress(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ADDRESS, null)
    }

    fun getUserImageUri(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_IMAGE_URI, null)
    }

    fun saveUserImageUri(context: Context, imageUri: String?) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_IMAGE_URI, imageUri)
        editor.apply()
    }

    fun clearUserProfile(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }

    fun hasUserProfile(context: Context): Boolean {
        return getUserName(context) != null || getUserEmail(context) != null
    }
}