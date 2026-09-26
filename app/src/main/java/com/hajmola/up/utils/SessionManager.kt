package com.hajmola.up.utils

import android.content.Context

object SessionManager {
    private const val PREFS_NAME = "my_app_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_IS_ADMIN_LOGGED_IN = "is_admin_logged_in"
    private const val KEY_IS_FETCH_HANDLED = "KEY_IS_FETCH_HANDLED"
    private const val KEY_UPLOADER_NAME = "KEY_UPLOADER_NAME"

    fun setLogin(context: Context, loggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setAdminLogin(context: Context, loggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_ADMIN_LOGGED_IN, loggedIn).apply()
    }

    fun isFetchHandled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_FETCH_HANDLED, false)
    }

    fun setFetchHandled(context: Context, fetched: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_FETCH_HANDLED, fetched).apply()
    }

    fun isAdminLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_ADMIN_LOGGED_IN, false)
    }

    fun setUploader(context: Context, name: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_UPLOADER_NAME, name).apply()
    }

    fun getUploader(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_UPLOADER_NAME, AppConstants.ADMIN_NAME)
    }
}
