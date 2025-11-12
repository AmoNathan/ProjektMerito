package com.example.zguba.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "car_swiper_prefs",
        Context.MODE_PRIVATE
    )
    
    fun saveUserId(userId: Long) {
        prefs.edit().putLong("user_id", userId).apply()
    }
    
    fun getUserId(): Long? {
        val userId = prefs.getLong("user_id", -1L)
        return if (userId == -1L) null else userId
    }
    
    fun clearUserId() {
        prefs.edit().remove("user_id").apply()
    }
}

