package com.example.zguba.database

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    @Volatile
    private var INSTANCE: AppDatabase? = null
    
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "car_swiper_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

