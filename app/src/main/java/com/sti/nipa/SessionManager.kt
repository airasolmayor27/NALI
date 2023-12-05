package com.sti.nipa

import android.content.Context
import android.content.SharedPreferences
class SessionManager(context: Context) {
    private val PREF_NAME = "MyAppSession"
    private val KEY_USERNAME = "username"
    private val KEY_PASSWORD = "password"
    private val KEY_USER_ID = "userid"
    private val KEY_ROLE_ID = "role_id"
    private val KEY_USER_NAME = "user_name"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun createLoginSession(
        username: String, password: String, userid: Int, role_id: Int,
        user_name:String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.putInt(KEY_USER_ID, userid)
        editor.putInt(KEY_ROLE_ID, role_id)
        editor.putString(KEY_USER_NAME,user_name)// Store as an integer
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun checkLogin(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserDetails(): HashMap<String, Any> { // Change the return type to Any
        val user = HashMap<String, Any>() // Change the value type to Any
        user[KEY_USERNAME] = sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        user[KEY_PASSWORD] = sharedPreferences.getString(KEY_PASSWORD, "") ?: ""
        user[KEY_USER_NAME] = sharedPreferences.getString(KEY_USER_NAME,"")?:""
        user[KEY_USER_ID] = sharedPreferences.getInt(KEY_USER_ID, 0) // Retrieve as an integer
        user[KEY_ROLE_ID] = sharedPreferences.getInt(KEY_ROLE_ID, 0) // Retrieve as an integer
        return user
    }

    fun logoutUser() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // Getter method for KEY_USERNAME
    fun getUsernameKey(): String {
        return KEY_USERNAME
    }

    // Getter method for KEY_USERNAME
    fun getUser_nameKey(): String {
        return KEY_USER_NAME
    }

    // Getter method for KEY_USERNAME
    fun getUserID(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, 0)
    }

    // Getter method for KEY_ROLE_ID
    fun getRoleID(): Int {
        return sharedPreferences.getInt(KEY_ROLE_ID, 0)
    }
}
